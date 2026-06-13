package com.milkeclair.glacage.models.solidBlock;

import com.milkeclair.glacage.models.solidBlock.leaf.Leaf;
import com.milkeclair.glacage.models.solidBlock.log.Log;
import com.milkeclair.glacage.models.solidBlock.tree.Tree;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/* 固体ブロック。 */
public class SolidBlock {
  private final BlockState blockState;

  public SolidBlock(BlockState blockState) {
    this.blockState = blockState;
  }

  /* 原木として見る。 */
  public Log log() {
    return new Log(blockState);
  }

  /* 原木かどうかの判定。 */
  public boolean isLog() {
    return log().isLog();
  }

  /* 葉として見る。 */
  public Leaf leaf() {
    return new Leaf(blockState);
  }

  /* 葉かどうかの判定。 */
  public boolean isLeaf() {
    return leaf().isLeaf();
  }

  /* 木として見る。 */
  public Tree tree() {
    return new Tree(blockState);
  }

  /* 木の要素かどうかの判定。 */
  public boolean isTreeBlock() {
    return tree().isTreeBlock();
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
