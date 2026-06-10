package com.milkeclair.glacage.usecases.lumberjack.chop;

import com.milkeclair.glacage.actions.delayedBreak.DelayedBreak;
import com.milkeclair.glacage.models.Log;
import java.util.LinkedHashSet;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.event.level.BlockEvent;

/** 木こり機能。 */
public class Chop {
  private static final int MIN_NATURAL_LEAVES = 6;

  private final BlockEvent.BreakEvent event;

  public Chop(BlockEvent.BreakEvent event) {
    this.event = event;
  }

  /** 木を伐採する。 */
  public Optional<DelayedBreak> call() {
    if (event.isCanceled() || !(event.getPlayer() instanceof ServerPlayer player)) {
      return Optional.empty();
    }

    if (!isHoldingAxe(player)) {
      return Optional.empty();
    }

    if (!new Log(event.getState()).isLog()) {
      return Optional.empty();
    }

    var level = player.level();
    var brokeLogPos = event.getPos().immutable();
    var baseBlock = event.getState().getBlock();
    var logs = new LogCollection(level, brokeLogPos, baseBlock).call();
    var leaves = new LeafCollection(level, brokeLogPos, logs).call();
    if (leaves.size() < MIN_NATURAL_LEAVES) {
      return Optional.empty();
    }

    var breakableLogs = breakableLogs(brokeLogPos, logs);
    return Optional.of(
        new DelayedBreak(player, level, breakOrder(breakableLogs, leaves), breakableLogs));
  }

  private boolean isHoldingAxe(ServerPlayer player) {
    return player.getMainHandItem().is(ItemTags.AXES);
  }

  private LinkedHashSet<BlockPos> breakableLogs(
      BlockPos brokeLogPos, LinkedHashSet<BlockPos> logs) {
    var breakableLogs = new LinkedHashSet<BlockPos>();
    for (var pos : logs) {
      if (!pos.equals(brokeLogPos)) {
        breakableLogs.add(pos);
      }
    }

    return breakableLogs;
  }

  private LinkedHashSet<BlockPos> breakOrder(
      LinkedHashSet<BlockPos> logs, LinkedHashSet<BlockPos> leaves) {
    var blocks = new LinkedHashSet<BlockPos>();

    blocks.addAll(logs);
    blocks.addAll(leaves);

    return blocks;
  }
}
