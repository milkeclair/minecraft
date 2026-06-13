package com.milkeclair.glacage.usecases.parkour.fastClimb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.minecraft.world.entity.MoverType;
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
    @DisplayName("45度以上上を向いている場合")
    class LookingUp {
      @Test
      @DisplayName("上方向の速度を固定値にする")
      void changesUpwardSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        var changedMovement = ArgumentCaptor.forClass(Vec3.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.getXRot()).thenReturn(-45f);

        new FastClimb(player).call();

        verify(player).move(eq(MoverType.SELF), changedMovement.capture());
        verify(player).resetFallDistance();
        assertThat(changedMovement.getValue().x).isZero();
        assertThat(changedMovement.getValue().y).isEqualTo(FastClimb.CLIMB_SPEED);
        assertThat(changedMovement.getValue().z).isZero();
      }
    }

    @Nested
    @DisplayName("45度以上下を向いている場合")
    class LookingDown {
      @Test
      @DisplayName("下方向の速度を固定値にする")
      void changesDownwardSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();
        var changedMovement = ArgumentCaptor.forClass(Vec3.class);

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.getXRot()).thenReturn(45f);

        new FastClimb(player).call();

        verify(player).move(eq(MoverType.SELF), changedMovement.capture());
        verify(player).resetFallDistance();
        assertThat(changedMovement.getValue().x).isZero();
        assertThat(changedMovement.getValue().y).isEqualTo(-FastClimb.CLIMB_SPEED);
        assertThat(changedMovement.getValue().z).isZero();
      }
    }

    @Nested
    @DisplayName("中央を向いている場合")
    class LookingCenter {
      @Test
      @DisplayName("速度を変更しない")
      void doesNotChangeSpeed() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.onClimbable()).thenReturn(true);
        when(player.getAbilities()).thenReturn(abilities);
        when(player.getXRot()).thenReturn(0f);

        new FastClimb(player).call();

        verify(player, never()).move(any(MoverType.class), any(Vec3.class));
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

        verify(player, never()).move(any(MoverType.class), any(Vec3.class));
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

        verify(player, never()).move(any(MoverType.class), any(Vec3.class));
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

        verify(player, never()).move(any(MoverType.class), any(Vec3.class));
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

        verify(player, never()).move(any(MoverType.class), any(Vec3.class));
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

        verify(player, never()).move(any(MoverType.class), any(Vec3.class));
      }
    }
  }
}
