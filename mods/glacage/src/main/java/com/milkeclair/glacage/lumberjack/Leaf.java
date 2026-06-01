package com.milkeclair.glacage.lumberjack;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Leaf {
  public final BlockPos pos;
  public final int distance;

  public static boolean isLeaf(BlockState blockState) {
    return blockState.is(BlockTags.LEAVES) && blockState.hasProperty(LeavesBlock.DISTANCE);
  }

  public static boolean isArtificial(BlockState blockState) {
    return isLeaf(blockState)
        && blockState.hasProperty(LeavesBlock.PERSISTENT)
        && blockState.getValue(LeavesBlock.PERSISTENT);
  }

  public static boolean isNatural(BlockState blockState) {
    return !isArtificial(blockState) && isLeaf(blockState);
  }

  public static boolean isTooFarFromLog(BlockState blockState, int distanceFromLog) {
    if (!isLeaf(blockState)) {
      return false;
    }

    var leafDistance = blockState.getValue(LeavesBlock.DISTANCE);
    if (leafDistance >= LeavesBlock.DECAY_DISTANCE) {
      return false;
    }

    // leafDistanceはminecraft側が判定する距離
    // これがdistanceFromLogより小さい場合、別の原木に判定が吸われている
    // つまり、現在見ている原木から遠いため判定が外れている
    return distanceFromLog > leafDistance;
  }

  public Leaf(BlockPos pos, int distance) {
    this.pos = pos;
    this.distance = distance;
  }
}
