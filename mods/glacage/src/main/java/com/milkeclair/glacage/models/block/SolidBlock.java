package com.milkeclair.glacage.models.block;

import com.milkeclair.glacage.models.block.leaf.Leaf;
import com.milkeclair.glacage.models.block.log.Log;
import com.milkeclair.glacage.models.block.tree.Tree;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/* 固体ブロック。 */
public class SolidBlock {
  private final BlockState blockState;

  public SolidBlock(BlockState blockState) {
    this.blockState = blockState;
  }

  /* 原木かどうかの判定。 */
  public boolean isLog() {
    return new Log(blockState).isLog();
  }

  /* 葉かどうかの判定。 */
  public boolean isLeaf() {
    return new Leaf(blockState).isLeaf();
  }

  /* 木の要素かどうかの判定。 */
  public boolean isTreeBlock() {
    return new Tree(blockState).isTreeBlock();
  }

  /* 邪魔なブロックかどうかの判定。 */
  public boolean isObstructiveTo(Obstructive obstructive) {
    return obstructive.includes(blockState);
  }

  /* 指定したブロックが一致するかの判定。 */
  public boolean isSameAs(Block block) {
    return blockState.getBlock() == block;
  }
}
