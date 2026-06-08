package com.milkeclair.glacage.models.satiety;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Visibility")
class VisibilityTest {
  @Nested
  @DisplayName("#canRender")
  class CanRender {
    @Nested
    @DisplayName("すべての表示条件を満たす場合")
    class Renderable {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var visibility = new Visibility(true, true, true, false, true, false);

        assertThat(visibility.canRender()).isTrue();
      }
    }

    @Nested
    @DisplayName("Minecraft初期化前の場合")
    class NotInitialized {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var visibility = new Visibility(false, true, true, false, true, false);

        assertThat(visibility.canRender()).isFalse();
      }
    }

    @Nested
    @DisplayName("playerが存在しない場合")
    class MissingPlayer {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var visibility = new Visibility(true, false, true, false, true, false);

        assertThat(visibility.canRender()).isFalse();
      }
    }

    @Nested
    @DisplayName("gameModeが存在しない場合")
    class MissingGameMode {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var visibility = new Visibility(true, true, false, false, true, false);

        assertThat(visibility.canRender()).isFalse();
      }
    }

    @Nested
    @DisplayName("GUIを非表示にしている場合")
    class HiddenGui {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var visibility = new Visibility(true, true, true, true, true, false);

        assertThat(visibility.canRender()).isFalse();
      }
    }

    @Nested
    @DisplayName("プレイヤーにダメージが入らないゲームモードの場合")
    class InvulnerableGameMode {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var visibility = new Visibility(true, true, true, false, false, false);

        assertThat(visibility.canRender()).isFalse();
      }
    }

    @Nested
    @DisplayName("乗り物の体力が表示される場合")
    class VehicleHealth {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var visibility = new Visibility(true, true, true, false, true, true);

        assertThat(visibility.canRender()).isFalse();
      }
    }
  }
}
