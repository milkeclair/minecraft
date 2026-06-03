package com.milkeclair.glacage.usecases.lumberjack.chop;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.helpers.FakeLevel;
import com.milkeclair.glacage.usecases.Lumberjack;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("LeafCollection")
@ExtendWith(EphemeralTestServerProvider.class)
class LeafCollectionTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("原木の隣に辿れる葉がある場合")
    class LeafAroundLog {
      @Test
      @DisplayName("原木の隣の葉を返す")
      void returnsLeafAroundLog() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var leaf = brokeLogPos.above();
        level.setBlock(leaf, naturalLeaf(1));

        var leaves =
            new LeafCollection(level.serverLevel(), brokeLogPos, Set.of(brokeLogPos)).call();

        assertThat(leaves).containsExactly(leaf);
      }
    }

    @Nested
    @DisplayName("葉からさらに辿れる葉がある場合")
    class ConnectedLeaves {
      @Test
      @DisplayName("距離を増やしながら葉を辿る")
      void returnsConnectedLeavesWithDistance() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var firstLeaf = brokeLogPos.above();
        var secondLeaf = firstLeaf.above();
        level.setBlock(firstLeaf, naturalLeaf(1));
        level.setBlock(secondLeaf, naturalLeaf(2));

        var leaves =
            new LeafCollection(level.serverLevel(), brokeLogPos, Set.of(brokeLogPos)).call();

        assertThat(leaves).containsExactlyInAnyOrder(firstLeaf, secondLeaf);
      }
    }

    @Nested
    @DisplayName("プレイヤーが設置した葉の場合")
    class ArtificialLeaf {
      @Test
      @DisplayName("葉を返さない")
      void doesNotReturnArtificialLeaf() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var leaf = brokeLogPos.above();
        level.setBlock(leaf, artificialLeaf(1));

        var leaves =
            new LeafCollection(level.serverLevel(), brokeLogPos, Set.of(brokeLogPos)).call();

        assertThat(leaves).isEmpty();
      }
    }

    @Nested
    @DisplayName("探索距離がMinecraft側の葉距離を超える場合")
    class TooFarLeaf {
      @Test
      @DisplayName("探索距離内の葉だけを返す")
      void doesNotReturnTooFarLeaf() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var firstLeaf = brokeLogPos.above();
        var secondLeaf = firstLeaf.above();
        level.setBlock(firstLeaf, naturalLeaf(1));
        // 対象の原木からの距離が2だが、Minecraft側の判定が1
        level.setBlock(secondLeaf, naturalLeaf(1));

        var leaves =
            new LeafCollection(level.serverLevel(), brokeLogPos, Set.of(brokeLogPos)).call();

        assertThat(leaves).containsExactly(firstLeaf);
      }
    }

    @Nested
    @DisplayName("同じ葉に複数の原木から到達できる場合")
    class DuplicatedLeaf {
      @Test
      @DisplayName("同じ葉を重複して返さない")
      void returnsLeafOnce() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var upperLog = brokeLogPos.above().above();
        var leaf = brokeLogPos.above();
        level.setBlock(leaf, naturalLeaf(1));

        var leaves =
            new LeafCollection(level.serverLevel(), brokeLogPos, Set.of(brokeLogPos, upperLog))
                .call();

        assertThat(leaves).containsExactly(leaf);
      }
    }

    @Nested
    @DisplayName("探索範囲外に辿れる葉がある場合")
    class OutsideSearchArea {
      @Test
      @DisplayName("探索範囲外の葉は返さない")
      void doesNotReturnOutsideLeaves() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var edgeLog = new BlockPos(Lumberjack.MAX_HORIZONTAL_RADIUS, 0, 0);
        var belowLeaf = brokeLogPos.below();
        var outsideLeaf = new BlockPos(Lumberjack.MAX_HORIZONTAL_RADIUS + 1, 0, 0);
        level.setBlock(belowLeaf, naturalLeaf(1));
        level.setBlock(outsideLeaf, naturalLeaf(1));

        var leaves =
            new LeafCollection(level.serverLevel(), brokeLogPos, Set.of(brokeLogPos, edgeLog))
                .call();

        assertThat(leaves).isEmpty();
      }
    }

    @Nested
    @DisplayName("葉が多すぎる場合")
    class TooManyLeaves {
      @Test
      @DisplayName("512個まで返す")
      void returnsUpToMaxLeafBlocks() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var logs = new LinkedHashSet<BlockPos>();
        var firstLayerLeaves = new LinkedHashSet<BlockPos>();

        // 17 * 17(289) * 2個の葉ブロックを設置して検証。
        for (var x = -Lumberjack.MAX_HORIZONTAL_RADIUS;
            x <= Lumberjack.MAX_HORIZONTAL_RADIUS;
            x++) {
          for (var z = -Lumberjack.MAX_HORIZONTAL_RADIUS;
              z <= Lumberjack.MAX_HORIZONTAL_RADIUS;
              z++) {
            var log = new BlockPos(x, 0, z);
            var firstLeaf = log.above();
            var secondLeaf = firstLeaf.above();

            logs.add(log);
            firstLayerLeaves.add(firstLeaf);
            level.setBlock(firstLeaf, naturalLeaf(1));
            level.setBlock(secondLeaf, naturalLeaf(2));
          }
        }

        var leaves = new LeafCollection(level.serverLevel(), brokeLogPos, logs).call();

        assertThat(leaves).hasSize(512);
        // 一段目は収まるため、全てあることを確認。
        assertThat(leaves).containsAll(firstLayerLeaves);
      }
    }
  }

  private static BlockState naturalLeaf(int distance) {
    return Blocks.OAK_LEAVES
        .defaultBlockState()
        .setValue(LeavesBlock.PERSISTENT, false)
        .setValue(LeavesBlock.DISTANCE, distance);
  }

  private static BlockState artificialLeaf(int distance) {
    return Blocks.OAK_LEAVES
        .defaultBlockState()
        .setValue(LeavesBlock.PERSISTENT, true)
        .setValue(LeavesBlock.DISTANCE, distance);
  }
}
