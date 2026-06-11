package com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.helpers.FakeLevel;
import com.milkeclair.glacage.usecases.miner.Miner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("BlockCollection")
@ExtendWith(EphemeralTestServerProvider.class)
class BlockCollectionTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("壊した砂利の前方向と上方向に砂利がある場合")
    class ForwardAndUpwardGravel {
      @Test
      @DisplayName("前方向と上方向の砂利を返す")
      void returnsForwardAndUpwardGravel() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        var forward = brokePos.north();
        var upward = brokePos.above();
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());
        level.setBlock(forward, Blocks.GRAVEL.defaultBlockState());
        level.setBlock(upward, Blocks.GRAVEL.defaultBlockState());

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.GRAVEL)
                .call();

        assertThat(blocks).containsExactly(forward, upward);
      }
    }

    @Nested
    @DisplayName("壊した土の前方向と上方向に土がある場合")
    class ForwardAndUpwardDirt {
      @Test
      @DisplayName("前方向と上方向の土を返す")
      void returnsForwardAndUpwardDirt() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        var forward = brokePos.north();
        var upward = brokePos.above();
        level.setBlock(brokePos, Blocks.DIRT.defaultBlockState());
        level.setBlock(forward, Blocks.DIRT.defaultBlockState());
        level.setBlock(upward, Blocks.DIRT.defaultBlockState());

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.DIRT).call();

        assertThat(blocks).containsExactly(forward, upward);
      }
    }

    @Nested
    @DisplayName("壊したブロックと違う種類の対象ブロックがある場合")
    class DifferentObstructiveBlock {
      @Test
      @DisplayName("違う種類の対象ブロックを返さない")
      void doesNotReturnDifferentObstructiveBlock() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());
        level.setBlock(brokePos.north(), Blocks.DIRT.defaultBlockState());
        level.setBlock(brokePos.above(), Blocks.GRAVEL.defaultBlockState());

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.GRAVEL)
                .call();

        assertThat(blocks).containsExactly(brokePos.above());
      }
    }

    @Nested
    @DisplayName("壊した砂利自身しかない場合")
    class OnlyBrokeGravel {
      @Test
      @DisplayName("空を返す")
      void returnsEmpty() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.GRAVEL)
                .call();

        assertThat(blocks).isEmpty();
      }
    }

    @Nested
    @DisplayName("探索先が対象ブロックではない場合")
    class NotCollectable {
      @Test
      @DisplayName("対象ではないブロックを返さない")
      void doesNotReturnNonCollectableBlock() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        var upward = brokePos.above();
        level.setBlock(brokePos.north(), Blocks.STONE.defaultBlockState());
        level.setBlock(upward, Blocks.GRAVEL.defaultBlockState());

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.GRAVEL)
                .call();

        assertThat(blocks).containsExactly(upward);
      }
    }

    @Nested
    @DisplayName("横方向に砂利がある場合")
    class SidewaysGravel {
      @Test
      @DisplayName("横方向の砂利を返さない")
      void doesNotReturnSidewaysGravel() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        level.setBlock(brokePos.east(), Blocks.GRAVEL.defaultBlockState());

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.GRAVEL)
                .call();

        assertThat(blocks).isEmpty();
      }
    }

    @Nested
    @DisplayName("探索範囲を超える砂利がある場合")
    class OutsideSearchArea {
      @Test
      @DisplayName("前方向と上方向の探索範囲内だけ返す")
      void returnsGravelInsideSearchArea() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        var forwardEdge = brokePos;
        var forwardOutside = brokePos;
        var upwardEdge = brokePos;
        var upwardOutside = brokePos;

        for (var i = 1; i <= Miner.MAX_FORWARD_DISTANCE + 1; i++) {
          forwardOutside = forwardOutside.north();
          level.setBlock(forwardOutside, Blocks.GRAVEL.defaultBlockState());

          if (i == Miner.MAX_FORWARD_DISTANCE) {
            forwardEdge = forwardOutside;
          }
        }
        for (var i = 1; i <= Miner.MAX_UP_DISTANCE + 1; i++) {
          upwardOutside = upwardOutside.above();
          level.setBlock(upwardOutside, Blocks.GRAVEL.defaultBlockState());

          if (i == Miner.MAX_UP_DISTANCE) {
            upwardEdge = upwardOutside;
          }
        }

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.GRAVEL)
                .call();

        assertThat(blocks).contains(forwardEdge, upwardEdge);
        assertThat(blocks).doesNotContain(forwardOutside, upwardOutside);
      }
    }

    @Nested
    @DisplayName("最大数を超える砂利がある場合")
    class OverMaxBlocks {
      @Test
      @DisplayName("最大数まで返す")
      void returnsGravelUpToMaxBlocks() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);

        for (var forward = 0; forward <= Miner.MAX_FORWARD_DISTANCE; forward++) {
          for (var up = 0; up <= Miner.MAX_UP_DISTANCE; up++) {
            if (forward == 0 && up == 0) {
              continue;
            }

            level.setBlock(
                relative(brokePos, Direction.NORTH, forward, up),
                Blocks.GRAVEL.defaultBlockState());
          }
        }

        var blocks =
            new BlockCollection(level.serverLevel(), brokePos, Direction.NORTH, Blocks.GRAVEL)
                .call();

        assertThat(blocks).hasSize(Miner.MAX_OBSTRUCTIVE_BLOCKS);
      }
    }
  }

  private static BlockPos relative(BlockPos origin, Direction direction, int forward, int up) {
    var position = origin;

    for (var i = 0; i < forward; i++) {
      position = position.relative(direction);
    }
    for (var i = 0; i < up; i++) {
      position = position.above();
    }

    return position;
  }
}
