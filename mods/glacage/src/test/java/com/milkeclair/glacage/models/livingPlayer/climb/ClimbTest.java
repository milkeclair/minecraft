package com.milkeclair.glacage.models.livingPlayer.climb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Climb")
class ClimbTest {
  @Nested
  @DisplayName("#canMoveFast")
  class CanMoveFast {
    @Nested
    @DisplayName("登れるブロック上にいる場合")
    class OnClimbable {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Climb(player).canMoveFast()).isTrue();
      }
    }

    @Nested
    @DisplayName("登れるブロック上にいない場合")
    class NotOnClimbable {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.onClimbable()).thenReturn(false);

        assertThat(new Climb(player).canMoveFast()).isFalse();
      }
    }

    @Nested
    @DisplayName("スニークしている場合")
    class Sneaking {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.isShiftKeyDown()).thenReturn(true);

        assertThat(new Climb(player).canMoveFast()).isFalse();
      }
    }

    @Nested
    @DisplayName("飛行している場合")
    class Flying {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        abilities.flying = true;

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Climb(player).canMoveFast()).isFalse();
      }
    }

    @Nested
    @DisplayName("水中にいる場合")
    class InWater {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.isInWater()).thenReturn(true);

        assertThat(new Climb(player).canMoveFast()).isFalse();
      }
    }

    @Nested
    @DisplayName("スペクテイターの場合")
    class Spectator {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.isSpectator()).thenReturn(true);

        assertThat(new Climb(player).canMoveFast()).isFalse();
      }
    }
  }
}
