package com.milkeclair.glacage.usecases.parkour.doubleJump;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Trigger")
class TriggerTest {
  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("通常ジャンプ後にジャンプキーを押し直した場合")
    class JumpPressedAfterNormalJump {
      @Test
      @DisplayName("二段ジャンプ要求を送る")
      void sendsRequest() {
        var player = mock(Player.class);
        var sent = new AtomicInteger();
        var trigger = new Trigger();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        trigger.call(player, false, () -> send(sent));

        when(player.onGround()).thenReturn(false);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
        trigger.call(player, true, () -> send(sent));
        trigger.call(player, false, () -> send(sent));

        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.08309, 0));
        trigger.call(player, true, () -> send(sent));

        assertThat(sent).hasValue(1);
      }
    }

    @Nested
    @DisplayName("通常ジャンプ直後の場合")
    class JustAfterNormalJump {
      @Test
      @DisplayName("二段ジャンプ要求を送らない")
      void doesNotSendRequest() {
        var player = mock(Player.class);
        var sent = new AtomicInteger();
        var trigger = new Trigger();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        trigger.call(player, false, () -> send(sent));

        when(player.onGround()).thenReturn(false);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
        trigger.call(player, true, () -> send(sent));

        assertThat(sent).hasValue(0);
      }
    }

    @Nested
    @DisplayName("接地扱いだが上昇中の場合")
    class GroundedButMovingUp {
      @Test
      @DisplayName("二段ジャンプ要求を送らない")
      void doesNotSendRequest() {
        var player = mock(Player.class);
        var sent = new AtomicInteger();
        var trigger = new Trigger();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);

        trigger.call(player, false, () -> send(sent));

        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.42, 0));

        trigger.call(player, true, () -> send(sent));

        assertThat(sent).hasValue(0);
      }
    }

    @Nested
    @DisplayName("ジャンプキーを押したまま空中に出た場合")
    class HoldingJump {
      @Test
      @DisplayName("二段ジャンプ要求を送らない")
      void doesNotSendRequest() {
        var player = mock(Player.class);
        var sent = new AtomicInteger();
        var trigger = new Trigger();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        trigger.call(player, true, () -> send(sent));

        when(player.onGround()).thenReturn(false);
        trigger.call(player, true, () -> send(sent));

        assertThat(sent).hasValue(0);
      }
    }

    @Nested
    @DisplayName("同じ空中で既に送信している場合")
    class AlreadySent {
      @Test
      @DisplayName("再送信しない")
      void doesNotSendAgain() {
        var player = mock(Player.class);
        var sent = new AtomicInteger();
        var trigger = new Trigger();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        trigger.call(player, false, () -> send(sent));

        when(player.onGround()).thenReturn(false);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
        trigger.call(player, true, () -> send(sent));
        trigger.call(player, false, () -> send(sent));

        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.08309, 0));
        trigger.call(player, true, () -> send(sent));
        trigger.call(player, false, () -> send(sent));
        trigger.call(player, true, () -> send(sent));

        assertThat(sent).hasValue(1);
      }
    }

    @Nested
    @DisplayName("一度着地した場合")
    class LandedAgain {
      @Test
      @DisplayName("次の空中で再度送信する")
      void sendsAgain() {
        var player = mock(Player.class);
        var sent = new AtomicInteger();
        var trigger = new Trigger();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        trigger.call(player, false, () -> send(sent));

        when(player.onGround()).thenReturn(false);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
        trigger.call(player, true, () -> send(sent));
        trigger.call(player, false, () -> send(sent));

        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.08309, 0));
        trigger.call(player, true, () -> send(sent));

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        trigger.call(player, false, () -> send(sent));

        when(player.onGround()).thenReturn(false);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
        trigger.call(player, true, () -> send(sent));
        trigger.call(player, false, () -> send(sent));

        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.08309, 0));
        trigger.call(player, true, () -> send(sent));

        assertThat(sent).hasValue(2);
      }
    }

    @Nested
    @DisplayName("二段ジャンプ処理が失敗した場合")
    class FailedAction {
      @Test
      @DisplayName("再度送信できる")
      void canRetry() {
        var player = mock(Player.class);
        var sent = new AtomicInteger();
        var trigger = new Trigger();

        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        trigger.call(player, false, () -> send(sent));

        when(player.onGround()).thenReturn(false);
        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
        trigger.call(player, true, () -> false);
        trigger.call(player, false, () -> send(sent));

        when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.08309, 0));
        trigger.call(player, true, () -> false);
        trigger.call(player, false, () -> send(sent));
        trigger.call(player, true, () -> send(sent));

        assertThat(sent).hasValue(1);
      }
    }
  }

  private static boolean send(AtomicInteger sent) {
    sent.incrementAndGet();

    return true;
  }
}
