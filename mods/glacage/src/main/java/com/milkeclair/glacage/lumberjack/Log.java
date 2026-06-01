package com.milkeclair.glacage.lumberjack;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Log {
  public static boolean isLog(BlockState blockState) {
    return blockState.is(BlockTags.LOGS);
  }

  public static boolean isMatchingLog(BlockState blockState, Block block) {
    return isLog(blockState) && blockState.getBlock() == block;
  }
}
