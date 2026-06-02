package com.milkeclair.glacage;

import com.milkeclair.glacage.lumberjack.Chop;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

public class Lumberjack {
  public static final int MAX_TREE_HEIGHT = 64;
  public static final int MAX_HORIZONTAL_RADIUS = 8;

  private boolean isBreakingTree = false;

  public static boolean isInsideSearchArea(BlockPos brokeLogPos, BlockPos pos) {
    var isNotBelowBrokeLog = pos.getY() >= brokeLogPos.getY();
    var isNotAboveMaxHeight = pos.getY() - brokeLogPos.getY() <= MAX_TREE_HEIGHT;
    var xDistance = Math.abs(pos.getX() - brokeLogPos.getX());
    var zDistance = Math.abs(pos.getZ() - brokeLogPos.getZ());
    var xzDistance = Math.max(xDistance, zDistance);
    var isWithinHorizontalRadius = xzDistance <= MAX_HORIZONTAL_RADIUS;

    return isNotBelowBrokeLog && isNotAboveMaxHeight && isWithinHorizontalRadius;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void chop(BlockEvent.BreakEvent event) {
    if (isBreakingTree) {
      return;
    }

    isBreakingTree = true;
    try {
      new Chop(event).call();
    } finally {
      isBreakingTree = false;
    }
  }
}
