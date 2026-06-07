package com.milkeclair.glacage.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.Config;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PlayerSettings")
class PlayerSettingsTest {
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
  @DisplayName(".enabled")
  class Enabled {
    @Nested
    @DisplayName("プレイヤー別設定がない場合")
    class MissingPlayerSetting {
      @Test
      @DisplayName("configの設定を返す")
      void returnsConfigSetting() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK, false);

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();

        Config.setEnabled(Feature.LUMBERJACK, true);

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
      }
    }

    @Nested
    @DisplayName("プレイヤー別設定がある場合")
    class PlayerSetting {
      @Test
      @DisplayName("同じUUIDの設定を返す")
      void returnsSettingForSameUuid() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
      }

      @Test
      @DisplayName("別UUIDのプレイヤーには影響しない")
      void doesNotAffectDifferentUuid() {
        var disabledPlayer =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var otherPlayer =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        Config.setEnabled(Feature.LUMBERJACK, true);
        PlayerSettings.setEnabled(Feature.LUMBERJACK, disabledPlayer.serverPlayer(), false);

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, disabledPlayer.serverPlayer()))
            .isFalse();
        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, otherPlayer.serverPlayer())).isTrue();
      }
    }
  }

  @Nested
  @DisplayName(".setEnabled")
  class SetEnabled {
    @Test
    @DisplayName("プレイヤー別設定を無効にする")
    void setsPlayerSettingToDisabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK, true);

      PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);

      assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
    }

    @Test
    @DisplayName("プレイヤー別設定を有効にする")
    void setsPlayerSettingToEnabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK, false);

      PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);

      assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
    }
  }

  @Nested
  @DisplayName(".remove")
  class Remove {
    @Nested
    @DisplayName("プレイヤー別設定がある場合")
    class PlayerSetting {
      @Test
      @DisplayName("設定を削除する")
      void removesSetting() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK, true);
        PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        PlayerSettings.remove(player.serverPlayer());

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
      }
    }
  }

  @Nested
  @DisplayName(".clear")
  class Clear {
    @Nested
    @DisplayName("プレイヤー別設定がある場合")
    class PlayerSetting {
      @Test
      @DisplayName("すべての設定を削除する")
      void removesAllSettings() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK, true);
        PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        PlayerSettings.clear();

        assertThat(PlayerSettings.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
      }
    }
  }
}
