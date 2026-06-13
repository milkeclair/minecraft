package com.milkeclair.glacage.models.ground;

import com.milkeclair.glacage.models.solidBlock.SolidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;

/** 地面のモデル。 */
public class Ground {
  private static final int UNDERGROUND_MARGIN = 20;
  private static final int SURFACE_SCAN_DEPTH = 64;

  private final ServerLevel level;
  private final BlockPos pos;

  public Ground(ServerLevel level, BlockPos pos) {
    this.level = level;
    this.pos = pos.immutable();
  }

  /** 地下にいるかどうかの判定。 */
  public boolean isUnderground() {
    return pos.getY() < surfaceYWithoutTrees() - UNDERGROUND_MARGIN;
  }

  /** 木を除いた地表Y座標。 */
  public int surfaceYWithoutTrees() {
    var height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
    // 岩盤より下でないことの保証。
    var minY = Math.max(level.getMinY(), height - SURFACE_SCAN_DEPTH);

    // 下に走査。
    for (var y = height - 1; y >= minY; y--) {
      var state = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
      if (new SolidBlock(state).isTreeBlock()) {
        continue;
      }

      return y + 1;
    }

    return height;
  }
}
