package com.milkeclair.glacage.usecases.lumberjack.chop;

import com.milkeclair.glacage.actions.search.breadthFirst.BreadthFirst;
import com.milkeclair.glacage.actions.search.breadthFirst.Node;
import com.milkeclair.glacage.actions.search.breadthFirst.OverflowPolicy;
import com.milkeclair.glacage.models.solidBlock.SolidBlock;
import com.milkeclair.glacage.usecases.lumberjack.Lumberjack;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

  public LogCollection(ServerLevel level, BlockPos brokeLogPos, Block baseBlock) {
    this.brokeLogPos = brokeLogPos;
    this.level = level;
    this.baseBlock = baseBlock;
  }

  /** 原木を探索する。 破壊されたブロックの位置からBFSで探索していき、収集した原木を返す。 */
  public LinkedHashSet<BlockPos> call() {
    return new BreadthFirst<>(
            List.of(new Node<>(brokeLogPos)),
            this::isInsideSearchArea,
            this::isCollectableLog,
            this::neighbors,
            MAX_LOG_BLOCKS,
            OverflowPolicy.EMPTY)
        .collectValues();
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

  private boolean isCollectableLog(Node<BlockPos> node) {
    var block = new SolidBlock(level.getBlockState(node.value()));

    return block.log().matches(baseBlock);
  }
}
