package com.milkeclair.glacage.models.ground;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.helpers.FakeLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Ground")
@ExtendWith(EphemeralTestServerProvider.class)
class GroundTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#surfaceYWithoutTrees")
  class SurfaceYWithoutTrees {
    @Nested
    @DisplayName("heightmapの高さに原木と葉が含まれている場合")
    class SurfaceWithTreeBlocks {
      @Test
      @DisplayName("原木と葉を除いた地表Y座標を返す")
      void returnsSurfaceYWithoutTreeBlocks() {
        var level = new FakeLevel();
        var pos = new BlockPos(0, 9, 0);
        level.setHeight(0, 0, 20);

        for (var y = 10; y < 15; y++) {
          level.setBlock(new BlockPos(0, y, 0), Blocks.OAK_LOG.defaultBlockState());
        }
        for (var y = 15; y < 20; y++) {
          level.setBlock(new BlockPos(0, y, 0), Blocks.OAK_LEAVES.defaultBlockState());
        }
        level.setBlock(pos, Blocks.DIRT.defaultBlockState());

        var ground = new Ground(level.serverLevel(), pos);

        assertThat(ground.surfaceYWithoutTrees()).isEqualTo(10);
      }
    }

    @Nested
    @DisplayName("heightmapの高さに木が含まれていない場合")
    class SurfaceWithoutTreeBlocks {
      @Test
      @DisplayName("heightmapの高さを返す")
      void returnsHeightmapY() {
        var level = new FakeLevel();
        var pos = new BlockPos(0, 10, 0);
        level.setHeight(0, 0, 20);

        var ground = new Ground(level.serverLevel(), pos);

        assertThat(ground.surfaceYWithoutTrees()).isEqualTo(20);
      }
    }
  }

  @Nested
  @DisplayName("#isUnderground")
  class IsUnderground {
    @Nested
    @DisplayName("地表より十分下にいる場合")
    class UnderSurface {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var level = new FakeLevel();
        var pos = new BlockPos(0, 19, 0);
        level.setHeight(0, 0, 40);

        var ground = new Ground(level.serverLevel(), pos);

        assertThat(ground.isUnderground()).isTrue();
      }
    }

    @Nested
    @DisplayName("地表に近い場合")
    class NearSurface {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var level = new FakeLevel();
        var pos = new BlockPos(0, 20, 0);
        level.setHeight(0, 0, 40);

        var ground = new Ground(level.serverLevel(), pos);

        assertThat(ground.isUnderground()).isFalse();
      }
    }

    @Nested
    @DisplayName("木の下にいる場合")
    class UnderTree {
      @Test
      @DisplayName("木を除いた地表で判定する")
      void usesSurfaceYWithoutTrees() {
        var level = new FakeLevel();
        var pos = new BlockPos(0, 9, 0);
        level.setHeight(0, 0, 20);

        for (var y = 10; y < 20; y++) {
          level.setBlock(new BlockPos(0, y, 0), Blocks.OAK_LOG.defaultBlockState());
        }
        level.setBlock(pos, Blocks.DIRT.defaultBlockState());

        var ground = new Ground(level.serverLevel(), pos);

        assertThat(ground.isUnderground()).isFalse();
      }
    }
  }
}
