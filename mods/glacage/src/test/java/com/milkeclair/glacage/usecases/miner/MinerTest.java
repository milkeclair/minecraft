package com.milkeclair.glacage.usecases.miner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.actions.delayedBreak.DelayedBreak;
import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.PlayerPreference;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakePlayer;
import com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak.ObstructiveBlockBreak;
import java.util.Optional;
import java.util.UUID;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Miner")
class MinerTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    Feature.clear();
    PlayerPreference.clear();
    for (var flag : Flag.values()) {
      ClientConfig.setEnabled(flag, flag.defaultEnabled());
    }
  }

  @Nested
  @DisplayName("#breakObstructiveBlock")
  class BreakObstructiveBlock {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("ObstructiveBlockBreakを作成しない")
      void doesNotConstructObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var miner = new Miner();
        Feature.forceDisable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

        try (var mockedObstructiveBlockBreaks = mockConstruction(ObstructiveBlockBreak.class)) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("ObstructiveBlockBreakを作成しない")
      void doesNotConstructObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var miner = new Miner();

        when(event.getPlayer()).thenReturn(player.serverPlayer());
        ClientConfig.setEnabled(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, true);
        PlayerPreference.setEnabled(
            Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), false);

        try (var mockedObstructiveBlockBreaks = mockConstruction(ObstructiveBlockBreak.class)) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("イベントを渡した場合")
    class GivenEvent {
      @Test
      @DisplayName("ObstructiveBlockBreakを作成して実行する")
      void constructsAndCallsObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var miner = new Miner();

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.empty()))) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(mockedObstructiveBlockBreaks.constructed().getFirst()).call();
        }
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyBreaking {
      @Test
      @DisplayName("再帰的なObstructiveBlockBreakを作成しない")
      void doesNotConstructRecursiveObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var miner = new Miner();

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) ->
                    when(mock.call())
                        .thenAnswer(
                            invocation -> {
                              miner.breakObstructiveBlock(event);

                              return Optional.empty();
                            }))) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(mockedObstructiveBlockBreaks.constructed().getFirst()).call();
        }
      }
    }
  }

  @Nested
  @DisplayName("#breakQueuedBlocks")
  class BreakQueuedBlocks {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("遅延破壊を実行せず、キューを破棄する")
      void doesNotTickAndClearsQueue() {
        var delayedBreak = mock(DelayedBreak.class);
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var miner = new Miner();

        Feature.forceEnable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          miner.breakObstructiveBlock(breakEvent);
          Feature.forceDisable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
          miner.breakQueuedBlocks(tickEvent);
          Feature.forceEnable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);
          miner.breakQueuedBlocks(tickEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(delayedBreak, never()).tick();
          verify(delayedBreak, never()).isFinished();
        }
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("遅延破壊を実行せず、キューから外す")
      void doesNotTickAndRemovesQueuedBreak() {
        var delayedBreak = mock(DelayedBreak.class);
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var miner = new Miner();

        when(breakEvent.getPlayer()).thenReturn(player.serverPlayer());
        when(delayedBreak.player()).thenReturn(player.serverPlayer());
        ClientConfig.setEnabled(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, true);
        PlayerPreference.setEnabled(
            Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), true);

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          miner.breakObstructiveBlock(breakEvent);
          PlayerPreference.setEnabled(
              Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), false);
          miner.breakQueuedBlocks(tickEvent);
          PlayerPreference.setEnabled(
              Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), true);
          miner.breakQueuedBlocks(tickEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(delayedBreak, never()).tick();
          verify(delayedBreak, never()).isFinished();
        }
      }
    }

    @Nested
    @DisplayName("キューが空の場合")
    class Empty {
      @Test
      @DisplayName("何も処理せず、次のobstructiveBlockBreakを妨げない")
      void doesNothingAndDoesNotBlockNextObstructiveBlockBreak() {
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var miner = new Miner();

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.empty()))) {
          miner.breakQueuedBlocks(tickEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).isEmpty();

          miner.breakObstructiveBlock(breakEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(mockedObstructiveBlockBreaks.constructed().getFirst()).call();
        }
      }
    }

    @Nested
    @DisplayName("遅延破壊がキューに入っている場合")
    class Queued {
      @Test
      @DisplayName("先頭の遅延破壊をtickする")
      void ticksFirstDelayedBreak() {
        var delayedBreak = mock(DelayedBreak.class);
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var miner = new Miner();

        when(delayedBreak.isFinished()).thenReturn(false);

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          miner.breakObstructiveBlock(breakEvent);
          miner.breakQueuedBlocks(tickEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(delayedBreak).tick();
          verify(delayedBreak).isFinished();
        }
      }
    }

    @Nested
    @DisplayName("先頭の遅延破壊が完了している場合")
    class Finished {
      @Test
      @DisplayName("キューから外す")
      void removesFinishedDelayedBreak() {
        var delayedBreak = mock(DelayedBreak.class);
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var miner = new Miner();

        when(delayedBreak.isFinished()).thenReturn(true);

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          miner.breakObstructiveBlock(breakEvent);
          miner.breakQueuedBlocks(tickEvent);
          miner.breakQueuedBlocks(tickEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(delayedBreak, times(1)).tick();
          verify(delayedBreak, times(1)).isFinished();
        }
      }
    }

    @Nested
    @DisplayName("先頭の遅延破壊が未完了の場合")
    class Unfinished {
      @Test
      @DisplayName("次のtickでも同じ遅延破壊を処理する")
      void keepsUnfinishedDelayedBreak() {
        var delayedBreak = mock(DelayedBreak.class);
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var miner = new Miner();

        when(delayedBreak.isFinished()).thenReturn(false);

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          miner.breakObstructiveBlock(breakEvent);
          miner.breakQueuedBlocks(tickEvent);
          miner.breakQueuedBlocks(tickEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(delayedBreak, times(2)).tick();
          verify(delayedBreak, times(2)).isFinished();
        }
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyBreaking {
      @Test
      @DisplayName("再帰的な遅延破壊を実行しない")
      void doesNotTickRecursively() {
        var delayedBreak = mock(DelayedBreak.class);
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var miner = new Miner();

        when(delayedBreak.isFinished()).thenReturn(false);
        doAnswer(
                invocation -> {
                  miner.breakQueuedBlocks(tickEvent);

                  return null;
                })
            .when(delayedBreak)
            .tick();

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          miner.breakObstructiveBlock(breakEvent);
          miner.breakQueuedBlocks(tickEvent);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(delayedBreak, times(1)).tick();
          verify(delayedBreak, times(1)).isFinished();
        }
      }
    }
  }
}
