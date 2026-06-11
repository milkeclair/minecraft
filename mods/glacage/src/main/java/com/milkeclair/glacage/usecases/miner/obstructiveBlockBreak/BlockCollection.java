package com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak;

import com.milkeclair.glacage.actions.search.breadthFirst.BreadthFirst;
import com.milkeclair.glacage.actions.search.breadthFirst.Node;
import com.milkeclair.glacage.actions.search.breadthFirst.OverflowPolicy;
import com.milkeclair.glacage.usecases.miner.Miner;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

/* 邪魔なブロックの探索を行うクラス。 */
public class BlockCollection {
  private final ServerLevel level;
  private final BlockPos brokePos;
  private final Direction forward;
  private final Block baseBlock;

  public BlockCollection(ServerLevel level, BlockPos brokePos, Direction forward, Block baseBlock) {
    this.level = level;
    this.brokePos = brokePos.immutable();
    this.forward = forward;
    this.baseBlock = baseBlock;
  }

  /* 邪魔なブロックを探索する。前方と上方を対象として、8マス探索する。 */
  public LinkedHashSet<BlockPos> call() {
    return new BreadthFirst<>(
            starts(),
            this::isInsideSearchArea,
            this::isCollectableBlock,
            this::neighbors,
            Miner.MAX_OBSTRUCTIVE_BLOCKS,
            OverflowPolicy.ELLIPSIS)
        .collectValues();
  }

  private List<Node<BlockPos>> starts() {
    return List.of(
        new Node<>(brokePos.relative(forward).immutable()),
        new Node<>(brokePos.above().immutable()));
  }

  private List<BlockPos> neighbors(Node<BlockPos> node) {
    return List.of(node.value().relative(forward).immutable(), node.value().above().immutable());
  }

  private boolean isInsideSearchArea(Node<BlockPos> node) {
    var position = node.value();
    var forwardDistance = forwardDistance(position);
    var upDistance = position.getY() - brokePos.getY();

    // 前方、上方、側方が探索範囲から出ていないか。
    return forwardDistance >= 0
        && forwardDistance <= Miner.MAX_FORWARD_DISTANCE
        && upDistance >= 0
        && upDistance <= Miner.MAX_UP_DISTANCE
        && sidewaysDistance(position) == 0;
  }

  private boolean isCollectableBlock(Node<BlockPos> node) {
    return level.getBlockState(node.value()).is(baseBlock);
  }

  private int forwardDistance(BlockPos position) {
    // 対象ノードから始点までの距離。
    var x = position.getX() - brokePos.getX();
    var z = position.getZ() - brokePos.getZ();

    return switch (forward) {
      case NORTH -> -z;
      case SOUTH -> z;
      case EAST -> x;
      case WEST -> -x;
      default -> 0;
    };
  }

  private int sidewaysDistance(BlockPos position) {
    // 対象ノードから始点までの距離。
    var x = position.getX() - brokePos.getX();
    var z = position.getZ() - brokePos.getZ();

    return switch (forward) {
      case EAST, WEST -> Math.abs(z);
      case NORTH, SOUTH -> Math.abs(x);
      default -> 0;
    };
  }
}
