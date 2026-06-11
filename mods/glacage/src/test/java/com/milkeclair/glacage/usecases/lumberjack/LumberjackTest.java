package com.milkeclair.glacage.usecases.lumberjack;

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
import com.milkeclair.glacage.usecases.lumberjack.chop.Chop;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Lumberjack")
class LumberjackTest {
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
  @DisplayName(".isInsideSearchArea")
  class IsInsideSearchArea {
    @Nested
    @DisplayName("壊された原木より下の位置の場合")
    class BelowBrokeLog {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var brokeLogPos = new BlockPos(0, 1, 0);
        var pos = new BlockPos(0, 0, 0);

        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isFalse();
      }
    }

    @Nested
    @DisplayName("最大の高さを超えている場合")
    class AboveBrokeLogAndOverHeight {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var brokeLogPos = new BlockPos(0, 1, 0);
        // 最大の高さを超える位置 = 壊された原木の位置 + 最大の高さ + 1
        var pos = new BlockPos(0, Lumberjack.MAX_TREE_HEIGHT + 1 + 1, 0);

        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isFalse();
      }
    }

    @Nested
    @DisplayName("壊された原木から水平距離が最大の半径を超えている場合")
    class OverHorizontalRadius {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var brokeLogPos = new BlockPos(0, 1, 0);
        var overX = new BlockPos(Lumberjack.MAX_HORIZONTAL_RADIUS + 1, 1, 0);
        var overZ = new BlockPos(0, 1, Lumberjack.MAX_HORIZONTAL_RADIUS + 1);

        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, overX)).isFalse();
        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, overZ)).isFalse();
      }
    }

    @Nested
    @DisplayName("壊された原木より上で、最大の高さ以内で、水平距離が最大の半径以内の場合")
    class InsideSearchArea {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var brokeLogPos = new BlockPos(0, 1, 0);
        var pos =
            new BlockPos(
                Lumberjack.MAX_HORIZONTAL_RADIUS,
                Lumberjack.MAX_TREE_HEIGHT,
                Lumberjack.MAX_HORIZONTAL_RADIUS);

        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isTrue();
      }
    }

    @Nested
    @DisplayName("壊された原木から最大の高さちょうどの場合")
    class MaxHeight {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var brokeLogPos = new BlockPos(0, 1, 0);
        var pos = new BlockPos(0, 1 + Lumberjack.MAX_TREE_HEIGHT, 0);

        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isTrue();
      }
    }

    @Nested
    @DisplayName("壊された原木から水平距離が最大の半径ちょうどの場合")
    class MaxHorizontalRadius {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var brokeLogPos = new BlockPos(0, 1, 0);
        var maxX = new BlockPos(Lumberjack.MAX_HORIZONTAL_RADIUS, 1, 0);
        var maxZ = new BlockPos(0, 1, Lumberjack.MAX_HORIZONTAL_RADIUS);

        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, maxX)).isTrue();
        assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, maxZ)).isTrue();
      }
    }
  }

  @Nested
  @DisplayName("#chop")
  class ChopEvent {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("Chopを作成しない")
      void doesNotConstructChop() {
        var event = mock(BlockEvent.BreakEvent.class);
        var lumberjack = new Lumberjack();
        Feature.forceDisable(Feature.LUMBERJACK.CHOP);

        try (var mockedChops = mockConstruction(Chop.class)) {
          lumberjack.chop(event);

          assertThat(mockedChops.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("Chopを作成しない")
      void doesNotConstructChop() {
        var event = mock(BlockEvent.BreakEvent.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var lumberjack = new Lumberjack();

        when(event.getPlayer()).thenReturn(player.serverPlayer());
        ClientConfig.setEnabled(Feature.LUMBERJACK.CHOP, true);
        PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), false);

        try (var mockedChops = mockConstruction(Chop.class)) {
          lumberjack.chop(event);

          assertThat(mockedChops.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("イベントを渡した場合")
    class GivenEvent {
      @Test
      @DisplayName("Chopを作成して実行する")
      void constructsAndCallsChop() {
        var event = mock(BlockEvent.BreakEvent.class);
        var constructorArguments = new AtomicReference<List<?>>();
        var lumberjack = new Lumberjack();

        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) -> {
                  constructorArguments.set(context.arguments());
                  when(mock.call()).thenReturn(Optional.empty());
                })) {
          lumberjack.chop(event);

          assertThat(mockedChops.constructed()).hasSize(1);
          assertThat(constructorArguments.get()).hasSize(1);
          assertThat(constructorArguments.get().getFirst()).isSameAs(event);
          // 一度呼ばれることの確認。
          verify(mockedChops.constructed().getFirst()).call();
        }
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyBreaking {
      @Test
      @DisplayName("再帰的なChopを作成しない")
      void doesNotConstructRecursiveChop() {
        var event = mock(BlockEvent.BreakEvent.class);
        var lumberjack = new Lumberjack();

        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) ->
                    when(mock.call())
                        .thenAnswer(
                            invocation -> {
                              // 再帰的に呼び出されることのモデリング。
                              lumberjack.chop(event);

                              return Optional.empty();
                            }))) {
          lumberjack.chop(event);

          assertThat(mockedChops.constructed()).hasSize(1);
          verify(mockedChops.constructed().getFirst()).call();
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
        var lumberjack = new Lumberjack();

        Feature.forceEnable(Feature.LUMBERJACK.CHOP);
        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          lumberjack.chop(breakEvent);
          Feature.forceDisable(Feature.LUMBERJACK.CHOP);
          lumberjack.breakQueuedBlocks(tickEvent);
          Feature.forceEnable(Feature.LUMBERJACK.CHOP);
          lumberjack.breakQueuedBlocks(tickEvent);

          assertThat(mockedChops.constructed()).hasSize(1);
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
        var lumberjack = new Lumberjack();

        when(breakEvent.getPlayer()).thenReturn(player.serverPlayer());
        when(delayedBreak.player()).thenReturn(player.serverPlayer());
        ClientConfig.setEnabled(Feature.LUMBERJACK.CHOP, true);
        PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), true);

        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          lumberjack.chop(breakEvent);
          PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), false);
          lumberjack.breakQueuedBlocks(tickEvent);
          PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), true);
          lumberjack.breakQueuedBlocks(tickEvent);

          assertThat(mockedChops.constructed()).hasSize(1);
          verify(delayedBreak, never()).tick();
          verify(delayedBreak, never()).isFinished();
        }
      }
    }

    @Nested
    @DisplayName("キューが空の場合")
    class Empty {
      @Test
      @DisplayName("何も処理せず、次のchopを妨げない")
      void doesNothingAndDoesNotBlockNextChop() {
        var breakEvent = mock(BlockEvent.BreakEvent.class);
        var tickEvent = mock(ServerTickEvent.Post.class);
        var lumberjack = new Lumberjack();

        try (var mockedChops =
            mockConstruction(
                Chop.class, (mock, context) -> when(mock.call()).thenReturn(Optional.empty()))) {
          lumberjack.breakQueuedBlocks(tickEvent);

          assertThat(mockedChops.constructed()).isEmpty();

          lumberjack.chop(breakEvent);

          assertThat(mockedChops.constructed()).hasSize(1);
          verify(mockedChops.constructed().getFirst()).call();
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
        var lumberjack = new Lumberjack();

        when(delayedBreak.isFinished()).thenReturn(false);

        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          lumberjack.chop(breakEvent);
          lumberjack.breakQueuedBlocks(tickEvent);

          assertThat(mockedChops.constructed()).hasSize(1);
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
        var lumberjack = new Lumberjack();

        when(delayedBreak.isFinished()).thenReturn(true);

        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          lumberjack.chop(breakEvent);
          lumberjack.breakQueuedBlocks(tickEvent);
          lumberjack.breakQueuedBlocks(tickEvent);

          assertThat(mockedChops.constructed()).hasSize(1);
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
        var lumberjack = new Lumberjack();

        when(delayedBreak.isFinished()).thenReturn(false);

        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          lumberjack.chop(breakEvent);
          lumberjack.breakQueuedBlocks(tickEvent);
          lumberjack.breakQueuedBlocks(tickEvent);

          assertThat(mockedChops.constructed()).hasSize(1);
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
        var lumberjack = new Lumberjack();

        when(delayedBreak.isFinished()).thenReturn(false);
        doAnswer(
                invocation -> {
                  lumberjack.breakQueuedBlocks(tickEvent);

                  return null;
                })
            .when(delayedBreak)
            .tick();

        try (var mockedChops =
            mockConstruction(
                Chop.class,
                (mock, context) -> when(mock.call()).thenReturn(Optional.of(delayedBreak)))) {
          lumberjack.chop(breakEvent);
          lumberjack.breakQueuedBlocks(tickEvent);

          assertThat(mockedChops.constructed()).hasSize(1);
          verify(delayedBreak, times(1)).tick();
          verify(delayedBreak, times(1)).isFinished();
        }
      }
    }
  }
}
