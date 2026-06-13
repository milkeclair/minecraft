package com.milkeclair.glacage.usecases.foodie.saturation;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.models.livingPlayer.satiety.Bar;
import com.milkeclair.glacage.models.livingPlayer.satiety.Satiety;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Icons")
class IconsTest {
  @Nested
  @DisplayName("#collect")
  class Collect {
    @Nested
    @DisplayName("隠し満腹度が5の場合")
    class FiveSaturationPoints {
      @Test
      @DisplayName("満腹度ゲージと同じ位置のアイコンを返す")
      void returnsIconsOnFoodBar() {
        var icons = new Icons(new Satiety(20, 5.0F, false), new Bar(200, 100, 49)).collect();

        assertThat(icons)
            .containsExactly(
                new Icon(saturation("full"), 182, 61),
                new Icon(saturation("full"), 174, 61),
                new Icon(saturation("half"), 166, 61));
      }
    }

    @Nested
    @DisplayName("隠し満腹度が0の場合")
    class ZeroSaturation {
      @Test
      @DisplayName("空にする")
      void returnsEmpty() {
        var icons = new Icons(new Satiety(20, 0.0F, false), new Bar(200, 100, 49)).collect();

        assertThat(icons).isEmpty();
      }
    }

    @Nested
    @DisplayName("隠し満腹度が小数の場合")
    class FractionalSaturation {
      @Test
      @DisplayName("半アイコンを返す")
      void returnsHalfIcon() {
        var icons = new Icons(new Satiety(20, 0.25F, false), new Bar(200, 100, 49)).collect();

        assertThat(icons).containsExactly(new Icon(saturation("half"), 182, 61));
      }
    }

    @Nested
    @DisplayName("隠し満腹度が満腹度を超える場合")
    class OverFoodLevel {
      @Test
      @DisplayName("満腹度までのアイコンを返す")
      void returnsIconsUpToFoodLevel() {
        var icons = new Icons(new Satiety(3, 10.0F, false), new Bar(200, 100, 49)).collect();

        assertThat(icons)
            .containsExactly(
                new Icon(saturation("full"), 182, 61), new Icon(saturation("half"), 174, 61));
      }
    }

    @Nested
    @DisplayName("空腹エフェクト中の場合")
    class Hungry {
      @Test
      @DisplayName("空腹用のアイコンを使う")
      void usesHungryFoodSprites() {
        var icons = new Icons(new Satiety(20, 1.0F, true), new Bar(200, 100, 49)).collect();

        assertThat(icons).containsExactly(new Icon(saturation("half_hunger"), 182, 61));
      }
    }
  }

  static Identifier saturation(String type) {
    return Identifier.fromNamespaceAndPath("glacage", "hud/saturation_" + type);
  }
}
