package com.milkeclair.glacage.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DelayedBreak")
class DelayedBreakTest {
  @Nested
  @DisplayName("#player")
  class Player {
    @Nested
    @DisplayName("呼び出した場合")
    class Called {
      @Test
      @DisplayName("プレイヤーを返す")
      void returnPlayer() {
        var player = new FakePlayer();
        var level = mock(ServerLevel.class);
        var delayedBreak = new DelayedBreak(player.serverPlayer(), level, new LinkedHashSet<>());

        assertThat(delayedBreak.player()).isEqualTo(player.serverPlayer());
      }
    }
  }

  @Nested
  @DisplayName("#tick")
  class Tick {
    @Nested
    @DisplayName("次のバッチに空気ブロックと通常ブロックがある場合")
    class EmptyAndFilledBlocks {
      @Test
      @DisplayName("空気ではないブロックだけを耐久消費なしで破壊する")
      void destroysOnlyFilledBlocks() {
        var first = new BlockPos(0, 0, 0);
        var second = new BlockPos(0, 1, 0);
        var third = new BlockPos(0, 2, 0);
        var level = mock(ServerLevel.class);
        var player = new FakePlayer();
        var delayedBreak =
            new DelayedBreak(
                player.serverPlayer(), level, new LinkedHashSet<>(List.of(first, second, third)));
        when(level.isEmptyBlock(first)).thenReturn(true);
        when(level.isEmptyBlock(second)).thenReturn(false);

        delayedBreak.tick();

        verify(player.gameMode(), never()).destroyBlock(first);
        verify(level, never()).destroyBlock(first, true, player.serverPlayer());
        verify(level).destroyBlock(second, true, player.serverPlayer());
        verify(player.gameMode(), never()).destroyBlock(second);
        verify(player.gameMode(), never()).destroyBlock(third);
        verify(level, never()).destroyBlock(third, true, player.serverPlayer());
        assertThat(delayedBreak.isFinished()).isFalse();
      }
    }

    @Nested
    @DisplayName("次のバッチに耐久対象ブロックと耐久対象外ブロックがある場合")
    class DurabilityAndNonDurabilityBlocks {
      @Test
      @DisplayName("耐久対象ブロックだけをプレイヤーの採掘として破壊する")
      void destroysDurabilityBlocksWithPlayerGameMode() {
        var log = new BlockPos(0, 0, 0);
        var leaf = new BlockPos(0, 1, 0);
        var level = mock(ServerLevel.class);
        var player = new FakePlayer();
        var delayedBreak =
            new DelayedBreak(
                player.serverPlayer(),
                level,
                new LinkedHashSet<>(List.of(log, leaf)),
                new LinkedHashSet<>(List.of(log)));
        when(level.isEmptyBlock(log)).thenReturn(false);
        when(level.isEmptyBlock(leaf)).thenReturn(false);

        delayedBreak.tick();

        verify(player.gameMode()).destroyBlock(log);
        verify(level, never()).destroyBlock(log, true, player.serverPlayer());
        verify(player.gameMode(), never()).destroyBlock(leaf);
        verify(level).destroyBlock(leaf, true, player.serverPlayer());
        assertThat(delayedBreak.isFinished()).isTrue();
      }
    }
  }

  @Nested
  @DisplayName("#isFinished")
  class IsFinished {
    @Nested
    @DisplayName("キューが空の場合")
    class Empty {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var level = mock(ServerLevel.class);
        var player = new FakePlayer();
        var delayedBreak = new DelayedBreak(player.serverPlayer(), level, new LinkedHashSet<>());

        assertThat(delayedBreak.isFinished()).isTrue();
      }
    }

    @Nested
    @DisplayName("キューにブロックが残っている場合")
    class RemainingBlocks {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var block = new BlockPos(0, 0, 0);
        var level = mock(ServerLevel.class);
        var player = new FakePlayer();
        var delayedBreak =
            new DelayedBreak(player.serverPlayer(), level, new LinkedHashSet<>(List.of(block)));

        assertThat(delayedBreak.isFinished()).isFalse();
      }
    }
  }
}
