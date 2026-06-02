package com.milkeclair.glacage.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Block.pos")
class BlockTest {
  @Nested
  @DisplayName("x, y, zを渡した場合")
  class ConvertToBlockPos {
    @Test
    @DisplayName("x, y, zを引き継いだBlockPosを返す")
    void returnsBlockPosWithSameCoordinates() {
      var block = new Block(1, 2, 3);

      var pos = block.pos();

      assertThat(pos.getX()).isEqualTo(1);
      assertThat(pos.getY()).isEqualTo(2);
      assertThat(pos.getZ()).isEqualTo(3);
    }
  }
}
