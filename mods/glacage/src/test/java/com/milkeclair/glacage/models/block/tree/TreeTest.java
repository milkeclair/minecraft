package com.milkeclair.glacage.models.block.tree;

import static org.assertj.core.api.Assertions.assertThat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Tree")
@ExtendWith(EphemeralTestServerProvider.class)
class TreeTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#isTreeBlock")
  class IsTreeBlock {
    @Nested
    @DisplayName("原木の場合")
    class GivenLog {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var tree = new Tree(Blocks.OAK_LOG.defaultBlockState());

        assertThat(tree.isTreeBlock()).isTrue();
      }
    }

    @Nested
    @DisplayName("葉の場合")
    class GivenLeaf {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var tree = new Tree(Blocks.OAK_LEAVES.defaultBlockState());

        assertThat(tree.isTreeBlock()).isTrue();
      }
    }

    @Nested
    @DisplayName("木ではない場合")
    class GivenNotTree {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var tree = new Tree(Blocks.STONE.defaultBlockState());

        assertThat(tree.isTreeBlock()).isFalse();
      }
    }
  }
}
