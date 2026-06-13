package com.milkeclair.glacage.models.livingPlayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.UUID;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LivingPlayer")
class LivingPlayerTest {
  @Nested
  @DisplayName("#contact")
  class Contact {
    @Test
    @DisplayName("地面との接触状態を返す")
    void returnsContact() {
      var player = mock(Player.class);

      when(player.onGround()).thenReturn(true);
      when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);

      assertThat(new LivingPlayer(player).contact().stable()).isTrue();
    }
  }

  @Nested
  @DisplayName("#climb")
  class Climb {
    @Test
    @DisplayName("登れる状態を返す")
    void returnsClimb() {
      var player = mock(Player.class);
      var abilities = new Abilities();

      when(player.onClimbable()).thenReturn(true);
      when(player.getAbilities()).thenReturn(abilities);

      assertThat(new LivingPlayer(player).climb().canMoveFast()).isTrue();
    }
  }

  @Nested
  @DisplayName("#jump")
  class Jump {
    @Test
    @DisplayName("ジャンプ状態を返す")
    void returnsJump() {
      var player = mock(Player.class);
      var abilities = new Abilities();

      when(player.getAbilities()).thenReturn(abilities);

      assertThat(new LivingPlayer(player).jump().can()).isTrue();
    }
  }

  @Nested
  @DisplayName("#markMotionChanged")
  class MarkMotionChanged {
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

        new LivingPlayer(player).markMotionChanged();

        assertThat(player.hurtMarked).isTrue();
      }
    }

    @Nested
    @DisplayName("server playerではない場合")
    class NotServerPlayer {
      @Test
      @DisplayName("何もしない")
      void doesNothing() {
        var player = mock(Player.class);

        new LivingPlayer(player).markMotionChanged();

        verifyNoInteractions(player);
      }
    }
  }

  @Nested
  @DisplayName("#satiety")
  class Satiety {
    @Test
    @DisplayName("満腹度を返す")
    void returnsSatiety() {
      var player = mock(Player.class);
      var foodData = mock(FoodData.class);

      when(player.getFoodData()).thenReturn(foodData);
      when(player.hasEffect(MobEffects.HUNGER)).thenReturn(true);
      when(foodData.getFoodLevel()).thenReturn(20);
      when(foodData.getSaturationLevel()).thenReturn(5.0F);

      var satiety = new LivingPlayer(player).satiety();

      assertThat(satiety.isHungry()).isTrue();
      assertThat(satiety.saturationPoints()).isEqualTo(5);
    }
  }
}
