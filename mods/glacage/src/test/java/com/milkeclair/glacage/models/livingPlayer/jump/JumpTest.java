package com.milkeclair.glacage.models.livingPlayer.jump;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Jump")
class JumpTest {
  @Nested
  @DisplayName("#can")
  class Can {
    @Nested
    @DisplayName("通常状態の場合")
    class Normal {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Jump(player).can()).isTrue();
      }
    }

    @Nested
    @DisplayName("安定接地している場合")
    class StableGround {
      @Test
      @DisplayName("接地状態に関係なくtrueを返す")
      void returnsTrue() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Jump(player).can()).isTrue();
      }
    }

    @Nested
    @DisplayName("登れるブロック上にいる場合")
    class OnClimbable {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.onClimbable()).thenReturn(true);

        assertThat(new Jump(player).can()).isFalse();
      }
    }

    @Nested
    @DisplayName("水中にいる場合")
    class InWater {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.isInWater()).thenReturn(true);

        assertThat(new Jump(player).can()).isFalse();
      }
    }

    @Nested
    @DisplayName("溶岩の中にいる場合")
    class InLava {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.isInLava()).thenReturn(true);

        assertThat(new Jump(player).can()).isFalse();
      }
    }

    @Nested
    @DisplayName("滑空中の場合")
    class FallFlying {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.isFallFlying()).thenReturn(true);

        assertThat(new Jump(player).can()).isFalse();
      }
    }

    @Nested
    @DisplayName("乗り物に乗っている場合")
    class Passenger {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.isPassenger()).thenReturn(true);

        assertThat(new Jump(player).can()).isFalse();
      }
    }

    @Nested
    @DisplayName("スペクテイターの場合")
    class Spectator {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.isSpectator()).thenReturn(true);

        assertThat(new Jump(player).can()).isFalse();
      }
    }

    @Nested
    @DisplayName("飛行中の場合")
    class Flying {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        abilities.flying = true;

        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Jump(player).can()).isFalse();
      }
    }

    @Nested
    @DisplayName("飛行できる場合")
    class MayFly {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        abilities.apply(new Abilities.Packed(false, false, true, false, true, 0.05f, 0.1f));

        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Jump(player).can()).isFalse();
      }
    }
  }
}
