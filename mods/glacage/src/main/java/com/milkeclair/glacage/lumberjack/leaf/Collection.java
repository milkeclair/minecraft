package com.milkeclair.glacage.lumberjack.leaf;

import com.milkeclair.glacage.Lumberjack;
import com.milkeclair.glacage.lumberjack.Leaf;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class Collection {
  private static final int MAX_LEAF_BLOCKS = 512;

  private final ServerLevel level;
  private final BlockPos brokeLogPos;
  private final Set<BlockPos> logs;

  private final LinkedHashSet<BlockPos> collected = new LinkedHashSet<>();
  private final HashSet<BlockPos> visited = new HashSet<>();
  private final ArrayDeque<Leaf> queue = new ArrayDeque<>();

  public Collection(ServerLevel level, BlockPos brokeLogPos, Set<BlockPos> logs) {
    this.level = level;
    this.brokeLogPos = brokeLogPos;
    this.logs = logs;
  }

  public Set<BlockPos> call() {
    enqueueAroundLog();
    recursiveCollectLeaves();

    return collected;
  }

  private void enqueueAroundLog() {
    var distanceFromLog = 1;

    for (var log : logs) {
      for (var direction : Direction.values()) {
        var neighbor = log.relative(direction).immutable();

        enqueue(neighbor, distanceFromLog);
      }
    }
  }

  private void recursiveCollectLeaves() {
    while (isExplorable()) {
      var current = queue.removeFirst();
      collected.add(current.pos);

      for (var direction : Direction.values()) {
        var neighbor = current.pos.relative(direction).immutable();
        var distanceFromLog = current.distance + 1;

        enqueue(neighbor, distanceFromLog);
      }
    }
  }

  private void enqueue(BlockPos pos, int distanceFromLog) {
    if (visited.contains(pos) || !Lumberjack.isInsideSearchArea(brokeLogPos, pos)) {
      return;
    }

    var state = level.getBlockState(pos);
    if (!Leaf.isNatural(state) || Leaf.isTooFarFromLog(state, distanceFromLog)) {
      return;
    }

    visited.add(pos);
    queue.add(new Leaf(pos, distanceFromLog));
  }

  private boolean isExplorable() {
    return !queue.isEmpty() && collected.size() < MAX_LEAF_BLOCKS;
  }
}
