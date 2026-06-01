package com.milkeclair.glacage.lumberjack;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.level.BlockEvent;

public class Chop {
  private static final int MIN_NATURAL_LEAVES = 6;

  private Set<BlockPos> logs;
  private Set<BlockPos> leaves;
  private final BlockEvent.BreakEvent event;

  public Chop(BlockEvent.BreakEvent event) {
    this.event = event;
  }

  public void call() {
    if (event.isCanceled() || !(event.getPlayer() instanceof ServerPlayer player)) {
      return;
    }

    var brokeLogState = event.getState();
    if (!Log.isLog(brokeLogState)) {
      return;
    }

    var level = player.level();
    var brokeLogPos = event.getPos().immutable();
    logs = collectLogs(level, brokeLogPos, brokeLogState.getBlock());
    leaves = collectLeaves(level, brokeLogPos, logs);
    if (leaves.size() < MIN_NATURAL_LEAVES) {
      return;
    }

    breakTree(player, level);
  }

  private Set<BlockPos> collectLogs(ServerLevel level, BlockPos brokeLogPos, Block block) {
    return new com.milkeclair.glacage.lumberjack.log.Collection(level, brokeLogPos, block).call();
  }

  private Set<BlockPos> collectLeaves(ServerLevel level, BlockPos brokeLogPos, Set<BlockPos> logs) {
    return new com.milkeclair.glacage.lumberjack.leaf.Collection(level, brokeLogPos, logs).call();
  }

  private void breakTree(ServerPlayer player, ServerLevel level) {
    for (var pos : logs) {
      if (canBreakLogs(level, pos)) {
        player.gameMode.destroyBlock(pos);
      }
    }

    for (var pos : leaves) {
      if (canBreakLeaf(level, pos)) {
        player.gameMode.destroyBlock(pos);
      }
    }
  }

  private boolean canBreakLogs(ServerLevel level, BlockPos pos) {
    return !pos.equals(event.getPos()) && !level.isEmptyBlock(pos);
  }

  private boolean canBreakLeaf(ServerLevel level, BlockPos pos) {
    return !level.isEmptyBlock(pos);
  }
}
