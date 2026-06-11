package com.milkeclair.glacage.usecases.mobility.fastClimb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@DisplayName("FastClimb")
class FastClimbTest {
  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("上方向へ移動している場合")
    class MovingUp {
      @Test
      @DisplayName("上方向の速度を固定値にする")
      void changesUpwardSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        var movement = new Vec3(0.1, 0.02, 0.3);
        var changedMovement = ArgumentCaptor.forClass(Vec3.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.getDeltaMovement()).thenReturn(movement);

        new FastClimb(player).call();

        verify(player).setDeltaMovement(changedMovement.capture());
        assertThat(changedMovement.getValue().x).isEqualTo(0.1);
        assertThat(changedMovement.getValue().y).isEqualTo(FastClimb.CLIMB_SPEED);
        assertThat(changedMovement.getValue().z).isEqualTo(0.3);
      }
    }

    @Nested
    @DisplayName("下方向へ移動している場合")
    class MovingDown {
      @Test
      @DisplayName("下方向の速度を固定値にする")
      void changesDownwardSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        var movement = new Vec3(0.1, -0.02, 0.3);
        var changedMovement = ArgumentCaptor.forClass(Vec3.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.getDeltaMovement()).thenReturn(movement);

        new FastClimb(player).call();

        verify(player).setDeltaMovement(changedMovement.capture());
        assertThat(changedMovement.getValue().x).isEqualTo(0.1);
        assertThat(changedMovement.getValue().y).isEqualTo(-FastClimb.CLIMB_SPEED);
        assertThat(changedMovement.getValue().z).isEqualTo(0.3);
      }
    }

    @Nested
    @DisplayName("上下方向へ移動していない場合")
    class NotMovingVertically {
      @Test
      @DisplayName("速度を変更しない")
      void doesNotChangeSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        var movement = new Vec3(0.1, 0.0, 0.3);

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.getDeltaMovement()).thenReturn(movement);

        new FastClimb(player).call();

        verify(player, never()).setDeltaMovement(any(Vec3.class));
      }
    }

    @Nested
    @DisplayName("climbable上にいない場合")
    class NotOnClimbable {
      @Test
      @DisplayName("速度を変更しない")
      void doesNotChangeSpeed() {
        var player = mock(Player.class);

        when(player.onClimbable()).thenReturn(false);

        new FastClimb(player).call();

        verify(player, never()).setDeltaMovement(any(Vec3.class));
      }
    }

    @Nested
    @DisplayName("スニークしている場合")
    class Sneaking {
      @Test
      @DisplayName("速度を変更しない")
      void doesNotChangeSpeed() {
        var player = mock(Player.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.isShiftKeyDown()).thenReturn(true);

        new FastClimb(player).call();

        verify(player, never()).setDeltaMovement(any(Vec3.class));
      }
    }

    @Nested
    @DisplayName("飛行している場合")
    class Flying {
      @Test
      @DisplayName("速度を変更しない")
      void doesNotChangeSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        abilities.flying = true;

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);

        new FastClimb(player).call();

        verify(player, never()).setDeltaMovement(any(Vec3.class));
      }
    }

    @Nested
    @DisplayName("水中にいる場合")
    class InWater {
      @Test
      @DisplayName("速度を変更しない")
      void doesNotChangeSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.isInWater()).thenReturn(true);

        new FastClimb(player).call();

        verify(player, never()).setDeltaMovement(any(Vec3.class));
      }
    }

    @Nested
    @DisplayName("スペクテイターの場合")
    class Spectator {
      @Test
      @DisplayName("速度を変更しない")
      void doesNotChangeSpeed() {
        var player = mock(Player.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.isSpectator()).thenReturn(true);

        new FastClimb(player).call();

        verify(player, never()).setDeltaMovement(any(Vec3.class));
      }
    }
  }
}
