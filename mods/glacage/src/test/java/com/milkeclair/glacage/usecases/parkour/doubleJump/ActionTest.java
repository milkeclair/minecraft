package com.milkeclair.glacage.usecases.parkour.doubleJump;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.UUID;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Action")
class ActionTest {
  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("空中の場合")
    class Airborne {
      @Test
      @DisplayName("ジャンプしてtrueを返す")
      void jumpsAndReturnsTrue() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.onGround()).thenReturn(false);
        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Action(player).call()).isTrue();

        verify(player).jumpFromGround();
        verify(player).resetFallDistance();
      }
    }

    @Nested
    @DisplayName("接地扱いだが上昇中の場合")
    class GroundedButMovingUp {
      @Test
      @DisplayName("ジャンプしてtrueを返す")
      void jumpsAndReturnsTrue() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.42, 0));
        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Action(player).call()).isTrue();

        verify(player).jumpFromGround();
        verify(player).resetFallDistance();
      }
    }

    @Nested
    @DisplayName("server playerの場合")
    class ServerPlayerContext {
      @Test
      @DisplayName("移動同期対象にする")
      void marksHurt() {
        var player =
            new FakePlayer()
                .setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .serverPlayer();
        var abilities = new Abilities();

        when(player.onGround()).thenReturn(false);
        when(player.getAbilities()).thenReturn(abilities);

        new Action(player).call();

        assertThat(player.hurtMarked).isTrue();
      }
    }

    @Nested
    @DisplayName("地面にいる場合")
    class OnGround {
      @Test
      @DisplayName("ジャンプせずfalseを返す")
      void doesNotJump() {
        var player = mock(Player.class);

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);

        assertThat(new Action(player).call()).isFalse();

        verify(player, never()).jumpFromGround();
        verify(player, never()).resetFallDistance();
      }
    }

    @Nested
    @DisplayName("飛行できる場合")
    class MayFly {
      @Test
      @DisplayName("ジャンプせずfalseを返す")
      void doesNotJump() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        abilities.apply(new Abilities.Packed(false, false, true, false, true, 0.05f, 0.1f));

        when(player.onGround()).thenReturn(false);
        when(player.getAbilities()).thenReturn(abilities);

        assertThat(new Action(player).call()).isFalse();

        verify(player, never()).jumpFromGround();
        verify(player, never()).resetFallDistance();
      }
    }
  }
}
