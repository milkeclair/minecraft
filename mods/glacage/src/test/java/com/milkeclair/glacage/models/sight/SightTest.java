package com.milkeclair.glacage.models.sight;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Sight")
class SightTest {
  @Nested
  @DisplayName("#tilt")
  class TiltValue {
    @Nested
    @DisplayName("上方向の閾値と同じ角度の場合")
    class UpThreshold {
      @Test
      @DisplayName("UPを返す")
      void returnsUp() {
        var sight = new Sight(-45);

        assertThat(sight.tilt(45)).isEqualTo(Tilt.UP);
      }
    }

    @Nested
    @DisplayName("上方向の閾値を超える角度の場合")
    class OverUpThreshold {
      @Test
      @DisplayName("UPを返す")
      void returnsUp() {
        var sight = new Sight(-45.1f);

        assertThat(sight.tilt(45)).isEqualTo(Tilt.UP);
      }
    }

    @Nested
    @DisplayName("下方向の閾値と同じ角度の場合")
    class DownThreshold {
      @Test
      @DisplayName("DOWNを返す")
      void returnsDown() {
        var sight = new Sight(45);

        assertThat(sight.tilt(45)).isEqualTo(Tilt.DOWN);
      }
    }

    @Nested
    @DisplayName("下方向の閾値を超える角度の場合")
    class OverDownThreshold {
      @Test
      @DisplayName("DOWNを返す")
      void returnsDown() {
        var sight = new Sight(45.1f);

        assertThat(sight.tilt(45)).isEqualTo(Tilt.DOWN);
      }
    }

    @Nested
    @DisplayName("中央の角度の場合")
    class Center {
      @Test
      @DisplayName("CENTERを返す")
      void returnsCenter() {
        var sight = new Sight(0);

        assertThat(sight.tilt(45)).isEqualTo(Tilt.CENTER);
      }
    }

    @Nested
    @DisplayName("上方向の閾値未満の場合")
    class LessThanUpThreshold {
      @Test
      @DisplayName("CENTERを返す")
      void returnsCenter() {
        var sight = new Sight(-44.9f);

        assertThat(sight.tilt(45)).isEqualTo(Tilt.CENTER);
      }
    }

    @Nested
    @DisplayName("下方向の閾値未満の場合")
    class LessThanDownThreshold {
      @Test
      @DisplayName("CENTERを返す")
      void returnsCenter() {
        var sight = new Sight(44.9f);

        assertThat(sight.tilt(45)).isEqualTo(Tilt.CENTER);
      }
    }
  }
}
