package com.milkeclair.glacage.usecases.lumberjack.chop;

import com.milkeclair.glacage.actions.search.breadthFirst.BreadthFirst;
import com.milkeclair.glacage.actions.search.breadthFirst.Node;
import com.milkeclair.glacage.actions.search.breadthFirst.OverflowPolicy;
import com.milkeclair.glacage.models.solidBlock.SolidBlock;
import com.milkeclair.glacage.usecases.lumberjack.Lumberjack;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

  public LeafCollection(ServerLevel level, BlockPos brokeLogPos, Set<BlockPos> logs) {
    this.brokeLogPos = brokeLogPos;
    this.logs = logs;
    this.level = level;
  }

  /** 葉を探索する。 原木の位置からBFSで探索していき、収集した葉を返す。 */
  public LinkedHashSet<BlockPos> call() {
    return new BreadthFirst<>(
            starts(),
            this::isInsideSearchArea,
            this::isCollectableLeaf,
            this::neighbors,
            MAX_LEAF_BLOCKS,
            OverflowPolicy.ELLIPSIS)
        .collectValues();
  }

  private ArrayList<Node<BlockPos>> starts() {
    var nodes = new ArrayList<Node<BlockPos>>();
    var distanceFromLog = 1;

    for (var log : logs) {
      for (var direction : Direction.values()) {
        var neighbor = log.relative(direction).immutable();
        nodes.add(new Node<>(neighbor, distanceFromLog, List.of(log, neighbor)));
      }
    }

    return nodes;
  }

  private ArrayList<BlockPos> neighbors(Node<BlockPos> node) {
    var nodes = new ArrayList<BlockPos>();

    for (var direction : Direction.values()) {
      nodes.add(node.value().relative(direction).immutable());
    }

    return nodes;
  }

  private boolean isInsideSearchArea(Node<BlockPos> node) {
    return Lumberjack.isInsideSearchArea(brokeLogPos, node.value());
  }

  private boolean isCollectableLeaf(Node<BlockPos> node) {
    var leaf = new SolidBlock(level.getBlockState(node.value())).leaf();

    return leaf.isNatural() && !leaf.isTooFarFromLog(node.distance());
  }
}
