package com.milkeclair.glacage.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.Priority;
import com.milkeclair.glacage.config.ServerConfig;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.PlayerPreference;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Runner")
class RunnerTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    Feature.clear();
    PlayerPreference.clear();
    ServerConfig.setPriority(Priority.CLIENT);
    for (var flag : Flag.values()) {
      ClientConfig.setEnabled(flag, flag.defaultEnabled());
      ServerConfig.setEnabled(flag, flag.defaultEnabled());
    }
  }

  @Nested
  @DisplayName("#enabled")
  class Enabled {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

        Feature.forceDisable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

        assertThat(runner.enabled()).isFalse();
      }
    }

    @Nested
    @DisplayName("機能が有効の場合")
    class EnabledSetting {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

        Feature.forceEnable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

        assertThat(runner.enabled()).isTrue();
      }
    }
  }

  @Nested
  @DisplayName("#enabled(Player)")
  class EnabledPlayer {
    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        ClientConfig.setEnabled(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, true);
        PlayerPreference.setEnabled(
            Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), false);

        assertThat(runner.enabled(player.serverPlayer())).isFalse();
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が有効の場合")
    class EnabledPlayerSetting {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        ClientConfig.setEnabled(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, true);
        PlayerPreference.setEnabled(
            Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), true);

        assertThat(runner.enabled(player.serverPlayer())).isTrue();
      }
    }
  }

  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("実行しない")
      void doesNotRun() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        var called = new AtomicInteger();

        Feature.forceDisable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

        runner.call(null, called::incrementAndGet);

        assertThat(called.get()).isZero();
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("実行しない")
      void doesNotRun() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        var called = new AtomicInteger();
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        ClientConfig.setEnabled(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, true);
        PlayerPreference.setEnabled(
            Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), false);

        runner.call(player.serverPlayer(), called::incrementAndGet);

        assertThat(called.get()).isZero();
      }
    }

    @Nested
    @DisplayName("機能が有効の場合")
    class EnabledSetting {
      @Test
      @DisplayName("実行する")
      void runs() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        var called = new AtomicInteger();

        runner.call(null, called::incrementAndGet);

        assertThat(called.get()).isOne();
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyRunning {
      @Test
      @DisplayName("再帰的に実行しない")
      void doesNotRunRecursively() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        var called = new AtomicInteger();

        runner.call(
            null,
            () -> {
              called.incrementAndGet();
              runner.call(null, called::incrementAndGet);
            });

        assertThat(called.get()).isOne();
      }
    }

    @Nested
    @DisplayName("実行中に例外が発生した場合")
    class RaisedException {
      @Test
      @DisplayName("次の実行を妨げない")
      void doesNotBlockNextCall() {
        var runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        var called = new AtomicInteger();

        assertThatThrownBy(
                () ->
                    runner.call(
                        null,
                        () -> {
                          throw new IllegalStateException("failed");
                        }))
            .isInstanceOf(IllegalStateException.class);
        runner.call(null, called::incrementAndGet);

        assertThat(called.get()).isOne();
      }
    }
  }
}
