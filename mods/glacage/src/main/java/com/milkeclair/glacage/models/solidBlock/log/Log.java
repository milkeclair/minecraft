package com.milkeclair.glacage.models.solidBlock.log;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** 原木のモデル。 */
public class Log {
  private final BlockState blockState;

  public Log(BlockState blockState) {
    this.blockState = blockState;
  }

  /** 原木かどうかの判定。 */
  public boolean isLog() {
    return blockState.is(BlockTags.LOGS);
  }

  /** 同じ原木かの判定。 */
  public boolean matches(Block block) {
    return isLog() && blockState.getBlock() == block;
  }
}
