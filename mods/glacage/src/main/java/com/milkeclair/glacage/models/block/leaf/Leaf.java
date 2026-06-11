package com.milkeclair.glacage.models.block.leaf;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

/** 葉ブロックのモデル。 */
public class Leaf {
  private final BlockState blockState;

  public Leaf(BlockState blockState) {
    this.blockState = blockState;
  }

  /** 葉かどうかの判定。 */
  public boolean isLeaf() {
    return blockState.is(BlockTags.LEAVES) && blockState.hasProperty(LeavesBlock.DISTANCE);
  }

  /** 人が設置したかの判定。 */
  public boolean isArtificial() {
    return isLeaf()
        && blockState.hasProperty(LeavesBlock.PERSISTENT)
        && blockState.getValue(LeavesBlock.PERSISTENT);
  }

  /** 自然に設置されたかの判定。 */
  public boolean isNatural() {
    return !isArtificial() && isLeaf();
  }

  /** 木から離れているか。(枯れるか) */
  public boolean isTooFarFromLog(int distanceFromLog) {
    if (!isLeaf()) {
      return true;
    }

    var leafDistance = blockState.getValue(LeavesBlock.DISTANCE);
    if (leafDistance >= LeavesBlock.DECAY_DISTANCE) {
      return true;
    }

    // leafDistanceはminecraft側が判定する距離。
    // これがdistanceFromLogより小さい場合、別の原木に判定が吸われている。
    // つまり、現在見ている原木から遠いため判定が外れている。
    return distanceFromLog > leafDistance;
  }
}
