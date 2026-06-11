package com.milkeclair.glacage.actions.delayedBreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.Priority;
import com.milkeclair.glacage.config.ServerConfig;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.PlayerPreference;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.Optional;
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
  @DisplayName("#enqueue")
  class Enqueue {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("遅延破壊を作成しない")
      void doesNotCreateDelayedBreak() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var created = new AtomicInteger();

        Feature.forceDisable(Feature.LUMBERJACK.CHOP);

        runner.enqueue(
            null,
            () -> {
              created.incrementAndGet();

              return Optional.empty();
            });

        assertThat(created.get()).isZero();
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("遅延破壊を作成しない")
      void doesNotCreateDelayedBreak() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var created = new AtomicInteger();
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        ClientConfig.setEnabled(Feature.LUMBERJACK.CHOP, true);
        PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), false);

        runner.enqueue(
            player.serverPlayer(),
            () -> {
              created.incrementAndGet();

              return Optional.empty();
            });

        assertThat(created.get()).isZero();
      }
    }

    @Nested
    @DisplayName("遅延破壊を作成できた場合")
    class CreatedBreak {
      @Test
      @DisplayName("キューに追加する")
      void enqueuesDelayedBreak() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);

        when(delayedBreak.isFinished()).thenReturn(false);

        runner.enqueue(null, () -> Optional.of(delayedBreak));
        runner.tick();

        verify(delayedBreak).tick();
        verify(delayedBreak).isFinished();
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyRunning {
      @Test
      @DisplayName("再帰的な遅延破壊を作成しない")
      void doesNotCreateDelayedBreakRecursively() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var created = new AtomicInteger();

        runner.enqueue(
            null,
            () -> {
              created.incrementAndGet();
              runner.enqueue(
                  null,
                  () -> {
                    created.incrementAndGet();

                    return Optional.empty();
                  });

              return Optional.empty();
            });

        assertThat(created.get()).isOne();
      }
    }
  }

  @Nested
  @DisplayName("#tick")
  class Tick {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("遅延破壊を実行せず、キューを破棄する")
      void doesNotTickAndClearsQueue() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);

        Feature.forceEnable(Feature.LUMBERJACK.CHOP);
        runner.enqueue(null, () -> Optional.of(delayedBreak));

        Feature.forceDisable(Feature.LUMBERJACK.CHOP);
        runner.tick();
        Feature.forceEnable(Feature.LUMBERJACK.CHOP);
        runner.tick();

        verify(delayedBreak, never()).tick();
        verify(delayedBreak, never()).isFinished();
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("遅延破壊を実行せず、キューから外す")
      void doesNotTickAndRemovesQueuedBreak() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        ClientConfig.setEnabled(Feature.LUMBERJACK.CHOP, true);
        PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), true);
        when(delayedBreak.player()).thenReturn(player.serverPlayer());

        runner.enqueue(player.serverPlayer(), () -> Optional.of(delayedBreak));
        PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), false);
        runner.tick();
        PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), true);
        runner.tick();

        verify(delayedBreak, never()).tick();
        verify(delayedBreak, never()).isFinished();
      }
    }

    @Nested
    @DisplayName("キューが空の場合")
    class Empty {
      @Test
      @DisplayName("何も処理せず、次の遅延破壊を妨げない")
      void doesNothingAndDoesNotBlockNextEnqueue() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);

        when(delayedBreak.isFinished()).thenReturn(false);

        runner.tick();
        runner.enqueue(null, () -> Optional.of(delayedBreak));
        runner.tick();

        verify(delayedBreak).tick();
        verify(delayedBreak).isFinished();
      }
    }

    @Nested
    @DisplayName("遅延破壊がキューに入っている場合")
    class Queued {
      @Test
      @DisplayName("先頭の遅延破壊をtickする")
      void ticksFirstDelayedBreak() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);

        when(delayedBreak.isFinished()).thenReturn(false);

        runner.enqueue(null, () -> Optional.of(delayedBreak));
        runner.tick();

        verify(delayedBreak).tick();
        verify(delayedBreak).isFinished();
      }
    }

    @Nested
    @DisplayName("先頭の遅延破壊が完了している場合")
    class Finished {
      @Test
      @DisplayName("キューから外す")
      void removesFinishedDelayedBreak() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);

        when(delayedBreak.isFinished()).thenReturn(true);

        runner.enqueue(null, () -> Optional.of(delayedBreak));
        runner.tick();
        runner.tick();

        verify(delayedBreak, times(1)).tick();
        verify(delayedBreak, times(1)).isFinished();
      }
    }

    @Nested
    @DisplayName("先頭の遅延破壊が未完了の場合")
    class Unfinished {
      @Test
      @DisplayName("次のtickでも同じ遅延破壊を処理する")
      void keepsUnfinishedDelayedBreak() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);

        when(delayedBreak.isFinished()).thenReturn(false);

        runner.enqueue(null, () -> Optional.of(delayedBreak));
        runner.tick();
        runner.tick();

        verify(delayedBreak, times(2)).tick();
        verify(delayedBreak, times(2)).isFinished();
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyRunning {
      @Test
      @DisplayName("再帰的な遅延破壊を実行しない")
      void doesNotTickRecursively() {
        var runner = new Runner(Feature.LUMBERJACK.CHOP);
        var delayedBreak = mock(DelayedBreak.class);

        when(delayedBreak.isFinished()).thenReturn(false);
        doAnswer(
                invocation -> {
                  runner.tick();

                  return null;
                })
            .when(delayedBreak)
            .tick();

        runner.enqueue(null, () -> Optional.of(delayedBreak));
        runner.tick();

        verify(delayedBreak, times(1)).tick();
        verify(delayedBreak, times(1)).isFinished();
      }
    }
  }
}
