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
  @DisplayName("#tick")
  class Tick {
    @Nested
    @DisplayName("次のバッチに空気ブロックと通常ブロックがある場合")
    class EmptyAndFilledBlocks {
      @Test
      @DisplayName("空気ではないブロックだけを破壊する")
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
        verify(player.gameMode()).destroyBlock(second);
        verify(player.gameMode(), never()).destroyBlock(third);
        assertThat(delayedBreak.isFinished()).isFalse();
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
