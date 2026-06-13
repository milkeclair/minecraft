package com.milkeclair.glacage.usecases.parkour.doubleJump;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Server")
class ServerTest {
  @Nested
  @DisplayName("#resetDoubleJump")
  class ResetDoubleJumpEvent {
    @Nested
    @DisplayName("サーバー側で着地している場合")
    class ServerSideOnGround {
      @Test
      @DisplayName("二段ジャンプ状態をresetする")
      void resetsDoubleJump() {
        var level = mock(Level.class);
        var player = mock(Player.class);
        var abilities = new Abilities();
        var event = new PlayerTickEvent.Post(player);

        when(level.isClientSide()).thenReturn(false);
        when(player.level()).thenReturn(level);
        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000201"));
        when(player.getAbilities()).thenReturn(abilities);
        when(player.onGround()).thenReturn(false, true, false);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);

        Server.call(player);
        new Server().resetDoubleJump(event);
        Server.call(player);

        verify(player, times(2)).jumpFromGround();
      }
    }

    @Nested
    @DisplayName("サーバー側で接地扱いだが上昇中の場合")
    class ServerSideGroundedButMovingUp {
      @Test
      @DisplayName("二段ジャンプ状態をresetしない")
      void doesNotResetDoubleJump() {
        var level = mock(Level.class);
        var player = mock(Player.class);
        var abilities = new Abilities();
        var event = new PlayerTickEvent.Post(player);

        when(level.isClientSide()).thenReturn(false);
        when(player.level()).thenReturn(level);
        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000202"));
        when(player.getAbilities()).thenReturn(abilities);
        when(player.onGround()).thenReturn(false, true);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.42, 0));

        Server.call(player);
        new Server().resetDoubleJump(event);
        Server.call(player);

        verify(player).jumpFromGround();
      }
    }

    @Nested
    @DisplayName("クライアント側の場合")
    class ClientSide {
      @Test
      @DisplayName("二段ジャンプ状態をresetしない")
      void doesNotResetDoubleJump() {
        var level = mock(Level.class);
        var player = mock(Player.class);
        var abilities = new Abilities();
        var event = new PlayerTickEvent.Post(player);

        when(level.isClientSide()).thenReturn(true);
        when(player.level()).thenReturn(level);
        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000203"));
        when(player.getAbilities()).thenReturn(abilities);
        when(player.onGround()).thenReturn(false);

        Server.call(player);
        new Server().resetDoubleJump(event);
        Server.call(player);

        verify(player).jumpFromGround();
      }
    }

    @Nested
    @DisplayName("空中の場合")
    class Airborne {
      @Test
      @DisplayName("二段ジャンプ状態をresetしない")
      void doesNotResetDoubleJump() {
        var level = mock(Level.class);
        var player = mock(Player.class);
        var abilities = new Abilities();
        var event = new PlayerTickEvent.Post(player);

        when(level.isClientSide()).thenReturn(false);
        when(player.level()).thenReturn(level);
        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000204"));
        when(player.getAbilities()).thenReturn(abilities);
        when(player.onGround()).thenReturn(false);

        Server.call(player);
        new Server().resetDoubleJump(event);
        Server.call(player);

        verify(player).jumpFromGround();
      }
    }
  }

  @Nested
  @DisplayName(".call")
  class Call {
    @Nested
    @DisplayName("同じ空中で二回呼ばれた場合")
    class CalledTwiceInAir {
      @Test
      @DisplayName("一度だけジャンプする")
      void jumpsOnce() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000101"));
        when(player.onGround()).thenReturn(false);
        when(player.getAbilities()).thenReturn(abilities);

        Server.call(player);
        Server.call(player);

        verify(player).jumpFromGround();
      }
    }
  }

  @Nested
  @DisplayName(".reset")
  class Reset {
    @Nested
    @DisplayName("一度使用した後にresetした場合")
    class Used {
      @Test
      @DisplayName("再度ジャンプできる")
      void makesAvailable() {
        var player = mock(Player.class);
        var abilities = new Abilities();

        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000102"));
        when(player.onGround()).thenReturn(false);
        when(player.getAbilities()).thenReturn(abilities);

        Server.call(player);
        Server.reset(player);
        Server.call(player);

        verify(player, times(2)).jumpFromGround();
      }
    }
  }
}
