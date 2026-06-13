package com.milkeclair.glacage.models.livingPlayer.satiety;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Satiety")
class SatietyTest {
  @Nested
  @DisplayName("#isHungry")
  class IsHungry {
    @Nested
    @DisplayName("空腹エフェクト中の場合")
    class Hungry {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        assertThat(new Satiety(20, 0.0F, true).isHungry()).isTrue();
      }
    }

    @Nested
    @DisplayName("空腹エフェクト中ではない場合")
    class NotHungry {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        assertThat(new Satiety(20, 0.0F, false).isHungry()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#saturationPoints")
  class SaturationPoints {
    @Nested
    @DisplayName("隠し満腹度が0の場合")
    class ZeroSaturation {
      @Test
      @DisplayName("0を返す")
      void returnsZero() {
        assertThat(new Satiety(20, 0.0F, false).saturationPoints()).isZero();
      }
    }

    @Nested
    @DisplayName("隠し満腹度が小数の場合")
    class FractionalSaturation {
      @Test
      @DisplayName("切り上げる")
      void roundsUp() {
        assertThat(new Satiety(20, 0.25F, false).saturationPoints()).isEqualTo(1);
      }
    }

    @Nested
    @DisplayName("隠し満腹度が満腹度を超える場合")
    class OverFoodLevel {
      @Test
      @DisplayName("満腹度で丸める")
      void clampsToFoodLevel() {
        assertThat(new Satiety(3, 10.0F, false).saturationPoints()).isEqualTo(3);
      }
    }

    @Nested
    @DisplayName("満腹度が最大値を超える場合")
    class OverMaxFoodLevel {
      @Test
      @DisplayName("最大値で丸める")
      void clampsToMaxFoodLevel() {
        assertThat(new Satiety(30, 30.0F, false).saturationPoints()).isEqualTo(20);
      }
    }

    @Nested
    @DisplayName("満腹度が0未満の場合")
    class NegativeFoodLevel {
      @Test
      @DisplayName("0を返す")
      void returnsZero() {
        assertThat(new Satiety(-1, 10.0F, false).saturationPoints()).isZero();
      }
    }
  }
}
