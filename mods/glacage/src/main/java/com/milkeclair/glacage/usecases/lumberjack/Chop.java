package com.milkeclair.glacage.usecases.lumberjack;

import com.milkeclair.glacage.actions.DelayedBreak;
import com.milkeclair.glacage.models.Log;
import com.milkeclair.glacage.usecases.lumberjack.chop.LeafCollection;
import com.milkeclair.glacage.usecases.lumberjack.chop.LogCollection;
import java.util.LinkedHashSet;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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

    return Optional.of(new DelayedBreak(player, level, breakOrder(brokeLogPos, logs, leaves)));
  }

  private LinkedHashSet<BlockPos> breakOrder(
      BlockPos brokeLogPos, LinkedHashSet<BlockPos> logs, LinkedHashSet<BlockPos> leaves) {
    var blocks = new LinkedHashSet<BlockPos>();
    for (var pos : logs) {
      if (!pos.equals(brokeLogPos)) {
        blocks.add(pos);
      }
    }

    blocks.addAll(leaves);

    return blocks;
  }
}
