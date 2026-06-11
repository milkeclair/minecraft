package com.milkeclair.glacage.models.block.log;

import static org.assertj.core.api.Assertions.assertThat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Log")
@ExtendWith(EphemeralTestServerProvider.class)
class LogTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#isLog")
  class IsLog {
    @Nested
    @DisplayName("原木の場合")
    class GivenLog {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var log = new Log(Blocks.OAK_LOG.defaultBlockState());

        assertThat(log.isLog()).isTrue();
      }
    }

    @Nested
    @DisplayName("原木ではない場合")
    class GivenNotLog {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var log = new Log(Blocks.STONE.defaultBlockState());

        assertThat(log.isLog()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#matches")
  class Matches {
    @Nested
    @DisplayName("同じ原木の場合")
    class SameLog {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var log = new Log(Blocks.OAK_LOG.defaultBlockState());

        assertThat(log.matches(Blocks.OAK_LOG)).isTrue();
      }
    }

    @Nested
    @DisplayName("違う原木の場合")
    class DifferentLog {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var log = new Log(Blocks.OAK_LOG.defaultBlockState());

        assertThat(log.matches(Blocks.BIRCH_LOG)).isFalse();
      }
    }

    @Nested
    @DisplayName("原木ではない場合")
    class NotLog {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var log = new Log(Blocks.STONE.defaultBlockState());

        assertThat(log.matches(Blocks.STONE)).isFalse();
      }
    }
  }
}
