package com.milkeclair.glacage.config.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.config.ClientConfig;
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

@DisplayName("Payload")
class PayloadTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    PlayerPreference.clear();
    for (var flag : Flag.values()) {
      ClientConfig.setEnabled(flag, flag.defaultEnabled());
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
      var payload = new Payload(Feature.LUMBERJACK.CHOP, false);

      try {
        Payload.CODEC.encode(buffer, payload);

        assertThat(Payload.CODEC.decode(buffer)).isEqualTo(payload);
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
      var payload = new Payload(Feature.LUMBERJACK.CHOP, true);

      assertThat(payload.type()).isSameAs(Payload.TYPE);
      assertThat(payload.type().id().toString()).isEqualTo("glacage:sync_feature_config");
    }
  }

  @Nested
  @DisplayName(".handle")
  class Handle {
    @Nested
    @DisplayName("server playerから受信した場合")
    class ServerPlayerContext {
      @Test
      @DisplayName("プレイヤー別設定を更新する")
      void updatesPlayerSetting() {
        var context = mock(IPayloadContext.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        when(context.player()).thenReturn(player.serverPlayer());

        Payload.handle(new Payload(Feature.LUMBERJACK.CHOP, false), context);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer()))
            .isFalse();
      }
    }

    @Nested
    @DisplayName("server player以外から受信した場合")
    class NonServerPlayerContext {
      @Test
      @DisplayName("プレイヤー別設定を更新しない")
      void doesNotUpdatePlayerSetting() {
        var context = mock(IPayloadContext.class);
        var contextPlayer = mock(Player.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        ClientConfig.setEnabled(Feature.LUMBERJACK.CHOP, true);
        when(context.player()).thenReturn(contextPlayer);

        Payload.handle(new Payload(Feature.LUMBERJACK.CHOP, false), context);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer()))
            .isTrue();
      }
    }
  }
}
