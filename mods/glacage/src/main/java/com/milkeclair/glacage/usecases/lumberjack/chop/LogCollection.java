package com.milkeclair.glacage.usecases.lumberjack.chop;

import com.milkeclair.glacage.models.Log;
import com.milkeclair.glacage.usecases.Lumberjack;
import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

/** 原木を探索するクラス。 */
public class LogCollection {
  private static final int MAX_LOG_BLOCKS = 128;

  private final BlockPos brokeLogPos;
  private final ServerLevel level;
  private final Block baseBlock;

  private final LinkedHashSet<BlockPos> collected = new LinkedHashSet<>();
  private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();

  public LogCollection(ServerLevel level, BlockPos brokeLogPos, Block baseBlock) {
    this.brokeLogPos = brokeLogPos;
    this.level = level;
    this.baseBlock = baseBlock;
  }

  /** 原木を探索する。 破壊されたブロックの位置からBFSで探索していき、収集した原木を返す。 */
  public LinkedHashSet<BlockPos> call() {
    recursiveCollectLogs();

    if (collected.size() > MAX_LOG_BLOCKS) {
      return new LinkedHashSet<>();
    } else {
      return collected;
    }
  }

  private void recursiveCollectLogs() {
    collected.add(brokeLogPos);
    queue.add(brokeLogPos);

    while (isExplorable()) {
      var current = queue.removeFirst();

      for (var direction : Direction.values()) {
        var neighbor = current.relative(direction).immutable();
        enqueue(neighbor);
      }
    }
  }

  private void enqueue(BlockPos pos) {
    if (collected.contains(pos) || !Lumberjack.isInsideSearchArea(brokeLogPos, pos)) {
      return;
    }

    if (!new Log(level.getBlockState(pos)).matches(baseBlock)) {
      return;
    }

    collected.add(pos);
    queue.add(pos);
  }

  private boolean isExplorable() {
    return !queue.isEmpty() && collected.size() <= MAX_LOG_BLOCKS;
  }
}
