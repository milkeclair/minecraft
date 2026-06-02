package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.core.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Lumberjack.isInsideSearchArea")
class LumberjackTest {
  @Nested
  @DisplayName("壊された原木より下の位置の場合")
  class BelowBrokeLog {
    @Test
    @DisplayName("falseを返す")
    void returnsFalse() {
      var brokeLogPos = new Block(0, 1, 0).pos();
      var pos = new Block(0, 0, 0).pos();

      assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isFalse();
    }
  }

  @Nested
  @DisplayName("最大の高さを超えている場合")
  class AboveBrokeLogAndOverHeight {
    @Test
    @DisplayName("falseを返す")
    void returnsFalse() {
      var brokeLogPos = new Block(0, 1, 0).pos();
      // 最大の高さを超える位置 = 壊された原木の位置 + 最大の高さ + 1
      var pos = new Block(0, Lumberjack.MAX_TREE_HEIGHT + 1 + 1, 0).pos();

      assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isFalse();
    }
  }

  @Nested
  @DisplayName("壊された原木から水平距離が最大の半径を超えている場合")
  class OverHorizontalRadius {
    @Test
    @DisplayName("falseを返す")
    void returnsFalse() {
      var brokeLogPos = new Block(0, 1, 0).pos();
      var overX = new Block(Lumberjack.MAX_HORIZONTAL_RADIUS + 1, 1, 0).pos();
      var overZ = new Block(0, 1, Lumberjack.MAX_HORIZONTAL_RADIUS + 1).pos();

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
      var brokeLogPos = new Block(0, 1, 0).pos();
      var pos =
          new Block(
                  Lumberjack.MAX_HORIZONTAL_RADIUS,
                  Lumberjack.MAX_TREE_HEIGHT,
                  Lumberjack.MAX_HORIZONTAL_RADIUS)
              .pos();

      assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isTrue();
    }
  }

  @Nested
  @DisplayName("壊された原木から最大の高さちょうどの場合")
  class MaxHeight {
    @Test
    @DisplayName("trueを返す")
    void returnsTrue() {
      var brokeLogPos = new Block(0, 1, 0).pos();
      var pos = new Block(0, 1 + Lumberjack.MAX_TREE_HEIGHT, 0).pos();

      assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, pos)).isTrue();
    }
  }

  @Nested
  @DisplayName("壊された原木から水平距離が最大の半径ちょうどの場合")
  class MaxHorizontalRadius {
    @Test
    @DisplayName("trueを返す")
    void returnsTrue() {
      var brokeLogPos = new Block(0, 1, 0).pos();
      var maxX = new Block(Lumberjack.MAX_HORIZONTAL_RADIUS, 1, 0).pos();
      var maxZ = new Block(0, 1, Lumberjack.MAX_HORIZONTAL_RADIUS).pos();

      assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, maxX)).isTrue();
      assertThat(Lumberjack.isInsideSearchArea(brokeLogPos, maxZ)).isTrue();
    }
  }
}
