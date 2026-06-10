package com.milkeclair.glacage.usecases;

import com.milkeclair.glacage.actions.DelayedBreak;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.usecases.lumberjack.Chop;
import java.util.ArrayDeque;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** 木こり。 */
public class Lumberjack {
  /** 最大の木の高さ。 */
  public static final int MAX_TREE_HEIGHT = 64;

  /** 木と認める範囲。 */
  public static final int MAX_HORIZONTAL_RADIUS = 8;

  private boolean isBreakingTree = false;
  private final ArrayDeque<DelayedBreak> delayedTreeBreaks = new ArrayDeque<>();

  /** 同じ木として探索する範囲に入っているかどうかの判定。 */
  public static boolean isInsideSearchArea(BlockPos brokeLogPos, BlockPos pos) {
    var isNotBelowBrokeLog = pos.getY() >= brokeLogPos.getY();
    var isNotAboveMaxHeight = pos.getY() - brokeLogPos.getY() <= MAX_TREE_HEIGHT;
    var xDistance = Math.abs(pos.getX() - brokeLogPos.getX());
    var zDistance = Math.abs(pos.getZ() - brokeLogPos.getZ());
    var xzDistance = Math.max(xDistance, zDistance);
    var isWithinHorizontalRadius = xzDistance <= MAX_HORIZONTAL_RADIUS;

    return isNotBelowBrokeLog && isNotAboveMaxHeight && isWithinHorizontalRadius;
  }

  /** 木をこるアクション。 */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void chop(BlockEvent.BreakEvent event) {
    if (!Feature.enabled(Feature.LUMBERJACK.CHOP, event.getPlayer())) {
      return;
    }

    if (isBreakingTree) {
      return;
    }

    isBreakingTree = true;
    try {
      new Chop(event).call().ifPresent(delayedTreeBreaks::add);
    } finally {
      isBreakingTree = false;
    }
  }

  /* キューの処理。 */
  @SubscribeEvent
  public void breakQueuedBlocks(ServerTickEvent.Post event) {
    if (!Feature.enabled(Feature.LUMBERJACK.CHOP)) {
      delayedTreeBreaks.clear();
      return;
    }

    if (isBreakingTree || delayedTreeBreaks.isEmpty()) {
      return;
    }

    isBreakingTree = true;
    try {
      var current = delayedTreeBreaks.peek();
      if (!Feature.enabled(Feature.LUMBERJACK.CHOP, current.player())) {
        delayedTreeBreaks.removeFirst();
        return;
      }

      current.tick();
      if (current.isFinished()) {
        delayedTreeBreaks.removeFirst();
      }
    } finally {
      isBreakingTree = false;
    }
  }
}
