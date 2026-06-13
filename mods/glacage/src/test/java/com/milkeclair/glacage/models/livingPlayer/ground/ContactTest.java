package com.milkeclair.glacage.models.livingPlayer.ground;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Contact")
class ContactTest {
  @Nested
  @DisplayName("#stable")
  class Stable {
    @Nested
    @DisplayName("接地していて上昇していない場合")
    class OnGround {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var player = mock(Player.class);

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);

        assertThat(new Contact(player).stable()).isTrue();
      }
    }

    @Nested
    @DisplayName("接地扱いだが上昇中の場合")
    class MovingUp {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.42, 0));

        assertThat(new Contact(player).stable()).isFalse();
      }
    }

    @Nested
    @DisplayName("接地していない場合")
    class Airborne {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);

        when(player.onGround()).thenReturn(false);

        assertThat(new Contact(player).stable()).isFalse();
      }
    }
  }
}
