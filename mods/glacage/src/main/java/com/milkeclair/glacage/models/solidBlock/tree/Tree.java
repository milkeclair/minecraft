package com.milkeclair.glacage.models.solidBlock.tree;

import com.milkeclair.glacage.models.solidBlock.log.Log;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

/** 木のモデル。 */
public class Tree {
  private final BlockState blockState;

  public Tree(BlockState blockState) {
    this.blockState = blockState;
  }

  /** 木を構成するブロックかどうかの判定。 */
  public boolean isTreeBlock() {
    return new Log(blockState).isLog() || blockState.is(BlockTags.LEAVES);
  }
}
