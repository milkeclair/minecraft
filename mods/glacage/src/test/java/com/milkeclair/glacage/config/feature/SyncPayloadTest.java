package com.milkeclair.glacage.config.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.Config;
import com.milkeclair.glacage.config.Feature;
import com.milkeclair.glacage.config.PlayerSettings;
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

@DisplayName("SyncPayload")
class SyncPayloadTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    PlayerSettings.clear();
    Config.setEnabled(Feature.LUMBERJACK, true);
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
      var payload = new SyncPayload(Feature.LUMBERJACK, false);

      try {
        SyncPayload.CODEC.encode(buffer, payload);

        assertThat(SyncPayload.CODEC.decode(buffer)).isEqualTo(payload);
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
      var payload = new SyncPayload(Feature.LUMBERJACK, true);

      assertThat(payload.type()).isSameAs(SyncPayload.TYPE);
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

        SyncPayload.handle(new SyncPayload(Feature.LUMBERJACK, false), context);

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
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

        Config.setEnabled(Feature.LUMBERJACK, true);
        when(context.player()).thenReturn(contextPlayer);

        SyncPayload.handle(new SyncPayload(Feature.LUMBERJACK, false), context);

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
      }
    }
  }
}
