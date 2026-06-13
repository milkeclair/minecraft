package com.milkeclair.glacage.usecases.parkour.doubleJump;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.Priority;
import com.milkeclair.glacage.config.ServerConfig;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.PlayerPreference;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.usecases.parkour.InputContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Client")
class ClientTest {
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
  @DisplayName("#doubleJump")
  class DoubleJumpEvent {
    @Nested
    @DisplayName("client tickを受け取った場合")
    class ClientTick {
      @Test
      @DisplayName("入力環境から二段ジャンプ入力を処理する")
      void handlesInputContext() {
        try (var contexts = mockConstruction(InputContext.class);
            var packets = mockStatic(ClientPacketDistributor.class)) {
          new Client().doubleJump(new ClientTickEvent.Post());

          Assertions.assertThat(contexts.constructed()).hasSize(1);
          packets.verifyNoInteractions();
        }
      }
    }
  }

  @Nested
  @DisplayName("#tick")
  class Tick {
    @Nested
    @DisplayName("入力環境が揃っていない場合")
    class UnavailableContext {
      @Test
      @DisplayName("requestを送信しない")
      void doesNotSendRequest() {
        var jumpKey = mock(KeyMapping.class);
        var connection = mock(ClientPacketListener.class);
        var context = new InputContext(null, jumpKey, connection);

        try (var packets = mockStatic(ClientPacketDistributor.class)) {
          new Client().tick(context);

          packets.verifyNoInteractions();
        }
      }
    }

    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledFeature {
      @Test
      @DisplayName("二段ジャンプを作成せずrequestを送信しない")
      void doesNotConstructActionOrSendRequest() {
        var player = mock(Player.class);
        var jumpKey = mock(KeyMapping.class);
        var connection = mock(ClientPacketListener.class);
        var context = new InputContext(player, jumpKey, connection);
        Feature.forceDisable(Feature.PARKOUR.DOUBLE_JUMP);

        try (var actions = mockConstruction(Action.class);
            var packets = mockStatic(ClientPacketDistributor.class)) {
          new Client().tick(context);

          Assertions.assertThat(actions.constructed()).isEmpty();
          packets.verifyNoInteractions();
        }
      }
    }

    @Nested
    @DisplayName("通常ジャンプ後にジャンプキーを押し直した場合")
    class JumpPressedAfterNormalJump {
      @Test
      @DisplayName("二段ジャンプしてrequestを送信する")
      void sendsRequest() {
        var player = mock(Player.class);
        var jumpKey = mock(KeyMapping.class);
        var connection = mock(ClientPacketListener.class);
        var context = new InputContext(player, jumpKey, connection);
        var abilities = new Abilities();
        var client = new Client();

        when(player.getAbilities()).thenReturn(abilities);
        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        when(jumpKey.isDown()).thenReturn(false);

        try (var packets = mockStatic(ClientPacketDistributor.class)) {
          client.tick(context);

          when(player.onGround()).thenReturn(false);
          when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
          client.tick(context);

          when(jumpKey.isDown()).thenReturn(true);
          client.tick(context);

          packets.verify(() -> ClientPacketDistributor.sendToServer(isA(Request.class)));
          verify(player).jumpFromGround();
        }
      }
    }

    @Nested
    @DisplayName("二段ジャンプできない場合")
    class CannotDoubleJump {
      @Test
      @DisplayName("requestを送信しない")
      void doesNotSendRequest() {
        var player = mock(Player.class);
        var jumpKey = mock(KeyMapping.class);
        var connection = mock(ClientPacketListener.class);
        var context = new InputContext(player, jumpKey, connection);
        var abilities = new Abilities();
        abilities.apply(new Abilities.Packed(false, false, true, false, true, 0.05f, 0.1f));
        var client = new Client();

        when(player.getAbilities()).thenReturn(abilities);
        when(player.onGround()).thenReturn(true);
        when(player.getDeltaMovement()).thenReturn(Vec3.ZERO);
        when(jumpKey.isDown()).thenReturn(false);

        try (var packets = mockStatic(ClientPacketDistributor.class)) {
          client.tick(context);

          when(player.onGround()).thenReturn(false);
          when(player.getDeltaMovement()).thenReturn(new Vec3(0, 0.3332, 0));
          client.tick(context);

          when(jumpKey.isDown()).thenReturn(true);
          client.tick(context);

          packets.verifyNoInteractions();
          verify(player, never()).jumpFromGround();
        }
      }
    }
  }
}
