package com.milkeclair.glacage.actions.delayedBreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Queue")
class QueueTest {
  @Nested
  @DisplayName("constructor")
  class Constructor {
    @Nested
    @DisplayName("1tickあたりの個数が0以下の場合")
    class NotPositiveBlocksPerTick {
      @Test
      @DisplayName("例外を投げる")
      void throwsException() {
        var block = new BlockPos(0, 0, 0);

        assertThatThrownBy(() -> new Queue(new LinkedHashSet<>(List.of(block)), 0))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }
  }

  @Nested
  @DisplayName("#nextBatch")
  class NextBatch {
    @Nested
    @DisplayName("デフォルト設定の場合")
    class Default {
      @Test
      @DisplayName("追加順で2個ずつ返す")
      void returnsTwoBlocksInInsertionOrder() {
        var first = new BlockPos(0, 0, 0);
        var second = new BlockPos(0, 1, 0);
        var third = new BlockPos(0, 2, 0);
        var fourth = new BlockPos(0, 3, 0);
        var fifth = new BlockPos(0, 4, 0);
        var queue = new Queue(new LinkedHashSet<>(List.of(first, second, third, fourth, fifth)));

        assertThat(queue.nextBatch()).containsExactly(first, second);
        assertThat(queue.nextBatch()).containsExactly(third, fourth);
        assertThat(queue.nextBatch()).containsExactly(fifth);
      }
    }

    @Nested
    @DisplayName("1tickあたりの個数を指定した場合")
    class SpecifiedBlocksPerTick {
      @Test
      @DisplayName("指定した個数ずつ返す")
      void returnsSpecifiedBlocksInInsertionOrder() {
        var first = new BlockPos(0, 0, 0);
        var second = new BlockPos(0, 1, 0);
        var third = new BlockPos(0, 2, 0);
        var queue = new Queue(new LinkedHashSet<>(List.of(first, second, third)), 1);

        assertThat(queue.nextBatch()).containsExactly(first);
        assertThat(queue.nextBatch()).containsExactly(second);
        assertThat(queue.nextBatch()).containsExactly(third);
      }
    }
  }

  @Nested
  @DisplayName("#isEmpty")
  class IsEmpty {
    @Nested
    @DisplayName("ブロックが残っている場合")
    class RemainingBlocks {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var block = new BlockPos(0, 0, 0);
        var queue = new Queue(new LinkedHashSet<>(List.of(block)));

        assertThat(queue.isEmpty()).isFalse();
      }
    }

    @Nested
    @DisplayName("すべてのブロックを取り出した場合")
    class AfterAllBlocksPolled {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var block = new BlockPos(0, 0, 0);
        var queue = new Queue(new LinkedHashSet<>(List.of(block)));

        queue.nextBatch();

        assertThat(queue.isEmpty()).isTrue();
      }
    }
  }
}
