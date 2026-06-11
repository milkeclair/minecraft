package com.milkeclair.glacage.models.block;

import static org.assertj.core.api.Assertions.assertThat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Obstructive")
@ExtendWith(EphemeralTestServerProvider.class)
class ObstructiveTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#includes")
  class Includes {
    @Nested
    @DisplayName("採掘を阻害するブロックの場合")
    class MiningObstructiveBlock {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        assertThat(Obstructive.MINING.includes(Blocks.GRAVEL.defaultBlockState())).isTrue();
        assertThat(Obstructive.MINING.includes(Blocks.DIRT.defaultBlockState())).isTrue();
      }
    }

    @Nested
    @DisplayName("採掘を阻害しないブロックの場合")
    class NotMiningObstructiveBlock {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        assertThat(Obstructive.MINING.includes(Blocks.STONE.defaultBlockState())).isFalse();
      }
    }
  }
}
