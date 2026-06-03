package com.milkeclair.glacage.usecases.lumberjack.chop;

import com.milkeclair.glacage.models.Leaf;
import com.milkeclair.glacage.usecases.Lumberjack;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/** 葉の探索を行うクラス。 */
public class LeafCollection {
  private static final int MAX_LEAF_BLOCKS = 512;

  private final BlockPos brokeLogPos;
  private final Set<BlockPos> logs;
  private final ServerLevel level;

  private final LinkedHashSet<BlockPos> collected = new LinkedHashSet<>();
  private final HashSet<BlockPos> visited = new HashSet<>();
  private final ArrayDeque<Node> queue = new ArrayDeque<>();

  public LeafCollection(ServerLevel level, BlockPos brokeLogPos, Set<BlockPos> logs) {
    this.brokeLogPos = brokeLogPos;
    this.logs = logs;
    this.level = level;
  }

  /** 葉を探索する。 原木の位置からBFSで探索していき、収集した葉を返す。 */
  public LinkedHashSet<BlockPos> call() {
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
      collected.add(current.pos());

      for (var direction : Direction.values()) {
        var neighbor = current.pos().relative(direction).immutable();
        var distanceFromLog = current.distanceFromLog() + 1;

        enqueue(neighbor, distanceFromLog);
      }
    }
  }

  private void enqueue(BlockPos pos, int distanceFromLog) {
    if (visited.contains(pos) || !Lumberjack.isInsideSearchArea(brokeLogPos, pos)) {
      return;
    }

    if (!isCollectableLeaf(pos, distanceFromLog)) {
      return;
    }

    visited.add(pos);
    queue.add(new Node(pos, distanceFromLog));
  }

  private boolean isExplorable() {
    return !queue.isEmpty() && collected.size() < MAX_LEAF_BLOCKS;
  }

  private boolean isCollectableLeaf(BlockPos pos, int distanceFromLog) {
    var leaf = new Leaf(level.getBlockState(pos));

    return leaf.isNatural() && !leaf.isTooFarFromLog(distanceFromLog);
  }

  private record Node(BlockPos pos, int distanceFromLog) {}
}
