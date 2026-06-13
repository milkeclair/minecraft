package com.milkeclair.glacage.usecases.parkour.doubleJump;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.Priority;
import com.milkeclair.glacage.config.ServerConfig;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.PlayerPreference;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakePlayer;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Request")
class RequestTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    Feature.clear();
    PlayerPreference.clear();
    ServerConfig.setPriority(Priority.CLIENT);
    for (var flag : Flag.values()) {
      ClientConfig.setEnabled(flag, flag.defaultEnabled());
      ServerConfig.setEnabled(flag, flag.defaultEnabled());
    }
  }

  @Nested
  @DisplayName("CODEC")
  class Codec {
    @Test
    @DisplayName("payloadをencode/decodeする")
    void encodesAndDecodesPayload() {
      var buffer =
          new RegistryFriendlyByteBuf(
              Unpooled.buffer(), RegistryAccess.EMPTY, ConnectionType.NEOFORGE);
      var request = new Request();

      try {
        Request.CODEC.encode(buffer, request);

        assertThat(Request.CODEC.decode(buffer)).isEqualTo(request);
      } finally {
        buffer.release();
      }
    }
  }

  @Nested
  @DisplayName("#type")
  class Type {
    @Test
    @DisplayName("payload typeを返す")
    void returnsPayloadType() {
      var request = new Request();

      assertThat(request.type()).isSameAs(Request.TYPE);
      assertThat(request.type().id().toString()).isEqualTo("glacage:double_jump");
    }
  }

  @Nested
  @DisplayName(".handle")
  class Handle {
    @Nested
    @DisplayName("server playerから受信した場合")
    class ServerPlayerContext {
      @Test
      @DisplayName("二段ジャンプを実行する")
      void runsDoubleJump() {
        var context = mock(IPayloadContext.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        when(context.player()).thenReturn(player.serverPlayer());

        try (var server = mockStatic(Server.class)) {
          Request.handle(new Request(), context);

          server.verify(() -> Server.call(player.serverPlayer()));
        }
      }
    }

    @Nested
    @DisplayName("機能が無効の場合")
    class Disabled {
      @Test
      @DisplayName("二段ジャンプを実行しない")
      void doesNotRunDoubleJump() {
        var context = mock(IPayloadContext.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Feature.forceDisable(Feature.PARKOUR.DOUBLE_JUMP);
        when(context.player()).thenReturn(player.serverPlayer());

        try (var server = mockStatic(Server.class)) {
          Request.handle(new Request(), context);

          server.verifyNoInteractions();
        }
      }
    }

    @Nested
    @DisplayName("server player以外から受信した場合")
    class NonServerPlayerContext {
      @Test
      @DisplayName("二段ジャンプを実行しない")
      void doesNotRunDoubleJump() {
        var context = mock(IPayloadContext.class);
        var player = mock(Player.class);

        when(context.player()).thenReturn(player);

        try (var server = mockStatic(Server.class)) {
          Request.handle(new Request(), context);

          server.verifyNoInteractions();
        }
      }
    }
  }
}
