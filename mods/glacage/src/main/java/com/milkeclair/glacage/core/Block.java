package com.milkeclair.glacage.core;

import net.minecraft.core.BlockPos;

public class Block {
  public final int x;
  public final int y;
  public final int z;

  public Block(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public BlockPos pos() {
    return new BlockPos(x, y, z);
  }
}
