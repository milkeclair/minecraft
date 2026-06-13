package com.milkeclair.glacage.usecases.parkour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("InputContext")
class InputContextTest {
  @Nested
  @DisplayName("#available")
  class Available {
    @Nested
    @DisplayName("playerとjumpKeyとconnectionがある場合")
    class Complete {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var player = mock(Player.class);
        var jumpKey = mock(KeyMapping.class);
        var connection = mock(ClientPacketListener.class);

        assertThat(new InputContext(player, jumpKey, connection).available()).isTrue();
      }
    }

    @Nested
    @DisplayName("playerがない場合")
    class MissingPlayer {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var jumpKey = mock(KeyMapping.class);
        var connection = mock(ClientPacketListener.class);

        assertThat(new InputContext(null, jumpKey, connection).available()).isFalse();
      }
    }

    @Nested
    @DisplayName("jumpKeyがない場合")
    class MissingJumpKey {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);
        var connection = mock(ClientPacketListener.class);

        assertThat(new InputContext(player, null, connection).available()).isFalse();
      }
    }

    @Nested
    @DisplayName("connectionがない場合")
    class MissingConnection {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);
        var jumpKey = mock(KeyMapping.class);

        assertThat(new InputContext(player, jumpKey, null).available()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#player")
  class PlayerContext {
    @Test
    @DisplayName("playerを返す")
    void returnsPlayer() {
      var player = mock(Player.class);
      var jumpKey = mock(KeyMapping.class);
      var connection = mock(ClientPacketListener.class);

      assertThat(new InputContext(player, jumpKey, connection).player()).isSameAs(player);
    }
  }

  @Nested
  @DisplayName("#jumpKeyDown")
  class JumpKeyDown {
    @Test
    @DisplayName("ジャンプキーが押されている場合trueを返す")
    void returnsTrue() {
      var player = mock(Player.class);
      var jumpKey = mock(KeyMapping.class);
      var connection = mock(ClientPacketListener.class);

      when(jumpKey.isDown()).thenReturn(true);

      assertThat(new InputContext(player, jumpKey, connection).jumpKeyDown()).isTrue();
    }

    @Test
    @DisplayName("ジャンプキーがない場合falseを返す")
    void returnsFalse() {
      var player = mock(Player.class);
      var connection = mock(ClientPacketListener.class);

      assertThat(new InputContext(player, null, connection).jumpKeyDown()).isFalse();
    }
  }
}
