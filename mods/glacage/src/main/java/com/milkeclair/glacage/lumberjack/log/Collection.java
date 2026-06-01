package com.milkeclair.glacage.lumberjack.log;

import com.milkeclair.glacage.Lumberjack;
import com.milkeclair.glacage.lumberjack.Log;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class Collection {
  private static final int MAX_LOG_BLOCKS = 128;

  private final ServerLevel level;
  private final BlockPos brokeLogPos;
  private final Block baseBlock;

  private final LinkedHashSet<BlockPos> collected = new LinkedHashSet<>();
  private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();

  public Collection(ServerLevel level, BlockPos brokeLogPos, Block baseBlock) {
    this.level = level;
    this.brokeLogPos = brokeLogPos;
    this.baseBlock = baseBlock;
  }

  public Set<BlockPos> call() {
    recursiveCollectLogs();

    if (collected.size() > MAX_LOG_BLOCKS) {
      return Collections.emptySet();
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

    var state = level.getBlockState(pos);
    if (!Log.isMatchingLog(state, baseBlock)) {
      return;
    }

    collected.add(pos);
    queue.add(pos);
  }

  private boolean isExplorable() {
    return !queue.isEmpty() && collected.size() <= MAX_LOG_BLOCKS;
  }
}
