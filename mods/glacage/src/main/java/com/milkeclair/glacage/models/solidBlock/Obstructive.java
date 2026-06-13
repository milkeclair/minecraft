package com.milkeclair.glacage.models.solidBlock;

import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/* お邪魔オブジェクトなど。 */
public enum Obstructive {
  /* 炭鉱夫している際に邪魔になるブロック。 */
  MINING(Set.of(Blocks.GRAVEL, Blocks.DIRT));

  private final Set<Block> blocks;

  private Obstructive(Set<Block> blocks) {
    this.blocks = blocks;
  }

  /* 対象ブロックが含まれているかの判定。 */
  public boolean includes(BlockState blockState) {
    return blocks.contains(blockState.getBlock());
  }
}
