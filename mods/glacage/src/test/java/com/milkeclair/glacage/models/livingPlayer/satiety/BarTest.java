package com.milkeclair.glacage.models.livingPlayer.satiety;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Bar")
class BarTest {
  @Nested
  @DisplayName("#x")
  class X {
    @Test
    @DisplayName("満腹度ゲージのx座標を返す")
    void returnsIconXFromRightToLeft() {
      var bar = new Bar(200, 100, 49);

      assertThat(bar.x(0)).isEqualTo(182);
      assertThat(bar.x(1)).isEqualTo(174);
      assertThat(bar.x(2)).isEqualTo(166);
    }
  }

  @Nested
  @DisplayName("#y")
  class Y {
    @Test
    @DisplayName("満腹度ゲージのy座標を返す")
    void returnsFoodBarY() {
      assertThat(new Bar(200, 100, 49).y()).isEqualTo(61);
    }
  }
}
