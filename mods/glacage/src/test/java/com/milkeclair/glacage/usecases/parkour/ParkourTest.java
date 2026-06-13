package com.milkeclair.glacage.usecases.parkour;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.Priority;
import com.milkeclair.glacage.config.ServerConfig;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.PlayerPreference;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakeLevel;
import com.milkeclair.glacage.helpers.FakePlayer;
import com.milkeclair.glacage.usecases.parkour.fastClimb.FastClimb;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Parkour")
class ParkourTest {
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
  @DisplayName("#fastClimb")
  class FastClimbEvent {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("FastClimbを作成しない")
      void doesNotConstructFastClimb() {
        var level = new FakeLevel();
        var player = new FakePlayer().setLevel(level.serverLevel());
        var event = new PlayerTickEvent.Post(player.serverPlayer());
        var parkour = new Parkour();
        Feature.forceDisable(Feature.PARKOUR.FAST_CLIMB);

        try (var mockedFastClimbs = mockConstruction(FastClimb.class)) {
          parkour.fastClimb(event);

          Assertions.assertThat(mockedFastClimbs.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("FastClimbを作成しない")
      void doesNotConstructFastClimb() {
        var level = new FakeLevel();
        var player =
            new FakePlayer()
                .setLevel(level.serverLevel())
                .setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var event = new PlayerTickEvent.Post(player.serverPlayer());
        var parkour = new Parkour();

        ClientConfig.setEnabled(Feature.PARKOUR.FAST_CLIMB, true);
        PlayerPreference.setEnabled(Feature.PARKOUR.FAST_CLIMB, player.serverPlayer(), false);

        try (var mockedFastClimbs = mockConstruction(FastClimb.class)) {
          parkour.fastClimb(event);

          Assertions.assertThat(mockedFastClimbs.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("クライアント側tickの場合")
    class ClientSideTick {
      @Test
      @DisplayName("FastClimbを作成して実行する")
      void constructsAndCallsFastClimb() {
        var level = mock(Level.class);
        var player = mock(Player.class);
        var event = new PlayerTickEvent.Post(player);
        var parkour = new Parkour();

        when(level.isClientSide()).thenReturn(true);
        when(player.level()).thenReturn(level);

        try (var mockedFastClimbs = mockConstruction(FastClimb.class)) {
          parkour.fastClimb(event);

          Assertions.assertThat(mockedFastClimbs.constructed()).hasSize(1);
          verify(mockedFastClimbs.constructed().getFirst()).call();
        }
      }
    }

    @Nested
    @DisplayName("イベントを渡した場合")
    class GivenEvent {
      @Test
      @DisplayName("FastClimbを作成して実行する")
      void constructsAndCallsFastClimb() {
        var level = new FakeLevel();
        var player = new FakePlayer().setLevel(level.serverLevel());
        var event = new PlayerTickEvent.Post(player.serverPlayer());
        var parkour = new Parkour();

        try (var mockedFastClimbs = mockConstruction(FastClimb.class)) {
          parkour.fastClimb(event);

          Assertions.assertThat(mockedFastClimbs.constructed()).hasSize(1);
          verify(mockedFastClimbs.constructed().getFirst()).call();
        }
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyClimbing {
      @Test
      @DisplayName("再帰的なFastClimbを作成しない")
      void doesNotConstructRecursiveFastClimb() {
        var level = new FakeLevel();
        var player = new FakePlayer().setLevel(level.serverLevel());
        var event = new PlayerTickEvent.Post(player.serverPlayer());
        var parkour = new Parkour();

        try (var mockedFastClimbs =
            mockConstruction(
                FastClimb.class,
                (mock, context) ->
                    doAnswer(
                            invocation -> {
                              parkour.fastClimb(event);

                              return null;
                            })
                        .when(mock)
                        .call())) {
          parkour.fastClimb(event);

          Assertions.assertThat(mockedFastClimbs.constructed()).hasSize(1);
          verify(mockedFastClimbs.constructed().getFirst()).call();
        }
      }
    }
  }
}
