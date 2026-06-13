package com.milkeclair.glacage.models.solidBlock;

import static org.assertj.core.api.Assertions.assertThat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("SolidBlock")
@ExtendWith(EphemeralTestServerProvider.class)
class SolidBlockTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#log")
  class LogContext {
    @Test
    @DisplayName("原木として扱える")
    void returnsLog() {
      var block = new SolidBlock(Blocks.OAK_LOG.defaultBlockState());

      assertThat(block.log().matches(Blocks.OAK_LOG)).isTrue();
    }
  }

  @Nested
  @DisplayName("#isLog")
  class IsLog {
    @Nested
    @DisplayName("原木の場合")
    class LogBlock {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var block = new SolidBlock(Blocks.OAK_LOG.defaultBlockState());

        assertThat(block.isLog()).isTrue();
      }
    }

    @Nested
    @DisplayName("原木ではない場合")
    class NotLogBlock {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var block = new SolidBlock(Blocks.STONE.defaultBlockState());

        assertThat(block.isLog()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#leaf")
  class LeafContext {
    @Test
    @DisplayName("葉として扱える")
    void returnsLeaf() {
      var block = new SolidBlock(Blocks.OAK_LEAVES.defaultBlockState());

      assertThat(block.leaf().isLeaf()).isTrue();
    }
  }

  @Nested
  @DisplayName("#isLeaf")
  class IsLeaf {
    @Nested
    @DisplayName("葉の場合")
    class LeafBlock {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var block = new SolidBlock(Blocks.OAK_LEAVES.defaultBlockState());

        assertThat(block.isLeaf()).isTrue();
      }
    }

    @Nested
    @DisplayName("葉ではない場合")
    class NotLeafBlock {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var block = new SolidBlock(Blocks.STONE.defaultBlockState());

        assertThat(block.isLeaf()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#tree")
  class TreeContext {
    @Test
    @DisplayName("木として扱える")
    void returnsTree() {
      var block = new SolidBlock(Blocks.OAK_LOG.defaultBlockState());

      assertThat(block.tree().isTreeBlock()).isTrue();
    }
  }

  @Nested
  @DisplayName("#isTreeBlock")
  class IsTreeBlock {
    @Nested
    @DisplayName("原木の場合")
    class LogBlock {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var block = new SolidBlock(Blocks.OAK_LOG.defaultBlockState());

        assertThat(block.isTreeBlock()).isTrue();
      }
    }

    @Nested
    @DisplayName("葉の場合")
    class LeafBlock {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var block = new SolidBlock(Blocks.OAK_LEAVES.defaultBlockState());

        assertThat(block.isTreeBlock()).isTrue();
      }
    }

    @Nested
    @DisplayName("木を構成しないブロックの場合")
    class NotTreeBlock {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var block = new SolidBlock(Blocks.STONE.defaultBlockState());

        assertThat(block.isTreeBlock()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#isObstructiveTo")
  class IsObstructiveTo {
    @Nested
    @DisplayName("指定した行動を阻害するブロックの場合")
    class ObstructiveBlock {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var block = new SolidBlock(Blocks.GRAVEL.defaultBlockState());

        assertThat(block.isObstructiveTo(Obstructive.MINING)).isTrue();
      }
    }

    @Nested
    @DisplayName("指定した行動を阻害しないブロックの場合")
    class NotObstructiveBlock {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var block = new SolidBlock(Blocks.STONE.defaultBlockState());

        assertThat(block.isObstructiveTo(Obstructive.MINING)).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#isSameAs")
  class IsSameAs {
    @Nested
    @DisplayName("同じブロックの場合")
    class SameBlock {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var block = new SolidBlock(Blocks.OAK_LOG.defaultBlockState());

        assertThat(block.isSameAs(Blocks.OAK_LOG)).isTrue();
      }
    }

    @Nested
    @DisplayName("違うブロックの場合")
    class DifferentBlock {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var block = new SolidBlock(Blocks.OAK_LOG.defaultBlockState());

        assertThat(block.isSameAs(Blocks.BIRCH_LOG)).isFalse();
      }
    }
  }
}
