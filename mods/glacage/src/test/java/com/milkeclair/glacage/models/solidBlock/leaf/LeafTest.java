package com.milkeclair.glacage.models.solidBlock.leaf;

import static org.assertj.core.api.Assertions.assertThat;

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

@DisplayName("Leaf")
@ExtendWith(EphemeralTestServerProvider.class)
class LeafTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#isLeaf")
  class IsLeaf {
    @Nested
    @DisplayName("葉の場合")
    class GivenLeaf {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var leaf = new Leaf(naturalLeaf(1));

        assertThat(leaf.isLeaf()).isTrue();
      }
    }

    @Nested
    @DisplayName("葉ではない場合")
    class GivenNotLeaf {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var leaf = new Leaf(Blocks.STONE.defaultBlockState());

        assertThat(leaf.isLeaf()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#isArtificial")
  class IsArtificial {
    @Nested
    @DisplayName("プレイヤーが設置した葉の場合")
    class PersistentLeaf {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var leaf = new Leaf(artificialLeaf(1));

        assertThat(leaf.isArtificial()).isTrue();
      }
    }

    @Nested
    @DisplayName("自然な葉の場合")
    class NaturalLeaf {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var leaf = new Leaf(naturalLeaf(1));

        assertThat(leaf.isArtificial()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#isNatural")
  class IsNatural {
    @Nested
    @DisplayName("自然な葉の場合")
    class Natural {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var leaf = new Leaf(naturalLeaf(1));

        assertThat(leaf.isNatural()).isTrue();
      }
    }

    @Nested
    @DisplayName("プレイヤーが設置した葉の場合")
    class Artificial {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var leaf = new Leaf(artificialLeaf(1));

        assertThat(leaf.isNatural()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#isTooFarFromLog")
  class IsTooFarFromLog {
    @Nested
    @DisplayName("葉ではない場合")
    class NotLeaf {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var leaf = new Leaf(Blocks.STONE.defaultBlockState());

        assertThat(leaf.isTooFarFromLog(1)).isTrue();
      }
    }

    @Nested
    @DisplayName("枯れる距離の場合")
    class DecayDistance {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var leaf = new Leaf(naturalLeaf(LeavesBlock.DECAY_DISTANCE));

        assertThat(leaf.isTooFarFromLog(1)).isTrue();
      }
    }

    @Nested
    @DisplayName("探索距離がMinecraft側の葉距離を超える場合")
    class OverLeafDistance {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var leaf = new Leaf(naturalLeaf(1));

        assertThat(leaf.isTooFarFromLog(2)).isTrue();
      }
    }

    @Nested
    @DisplayName("探索距離がMinecraft側の葉距離以内の場合")
    class InsideLeafDistance {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var leaf = new Leaf(naturalLeaf(3));

        assertThat(leaf.isTooFarFromLog(2)).isFalse();
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
