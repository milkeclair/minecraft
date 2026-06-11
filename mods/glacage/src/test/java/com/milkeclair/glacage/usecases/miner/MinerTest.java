package com.milkeclair.glacage.usecases.miner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.PlayerPreference;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakePlayer;
import com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak.ObstructiveBlockBreak;
import java.util.UUID;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Miner")
class MinerTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    Feature.clear();
    PlayerPreference.clear();
    for (var flag : Flag.values()) {
      ClientConfig.setEnabled(flag, flag.defaultEnabled());
    }
  }

  @Nested
  @DisplayName("#breakObstructiveBlock")
  class BreakObstructiveBlock {
    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledSetting {
      @Test
      @DisplayName("ObstructiveBlockBreakを作成しない")
      void doesNotConstructObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var miner = new Miner();
        Feature.forceDisable(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

        try (var mockedObstructiveBlockBreaks = mockConstruction(ObstructiveBlockBreak.class)) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("プレイヤー設定で機能が無効の場合")
    class DisabledPlayerSetting {
      @Test
      @DisplayName("ObstructiveBlockBreakを作成しない")
      void doesNotConstructObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var miner = new Miner();

        when(event.getPlayer()).thenReturn(player.serverPlayer());
        ClientConfig.setEnabled(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, true);
        PlayerPreference.setEnabled(
            Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK, player.serverPlayer(), false);

        try (var mockedObstructiveBlockBreaks = mockConstruction(ObstructiveBlockBreak.class)) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).isEmpty();
        }
      }
    }

    @Nested
    @DisplayName("イベントを渡した場合")
    class GivenEvent {
      @Test
      @DisplayName("ObstructiveBlockBreakを作成して実行する")
      void constructsAndCallsObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var miner = new Miner();

        try (var mockedObstructiveBlockBreaks = mockConstruction(ObstructiveBlockBreak.class)) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(mockedObstructiveBlockBreaks.constructed().getFirst()).call();
        }
      }
    }

    @Nested
    @DisplayName("現在実行中の場合")
    class AlreadyBreaking {
      @Test
      @DisplayName("再帰的なObstructiveBlockBreakを作成しない")
      void doesNotConstructRecursiveObstructiveBlockBreak() {
        var event = mock(BlockEvent.BreakEvent.class);
        var miner = new Miner();

        try (var mockedObstructiveBlockBreaks =
            mockConstruction(
                ObstructiveBlockBreak.class,
                (mock, context) ->
                    doAnswer(
                            invocation -> {
                              miner.breakObstructiveBlock(event);

                              return null;
                            })
                        .when(mock)
                        .call())) {
          miner.breakObstructiveBlock(event);

          assertThat(mockedObstructiveBlockBreaks.constructed()).hasSize(1);
          verify(mockedObstructiveBlockBreaks.constructed().getFirst()).call();
        }
      }
    }
  }
}
