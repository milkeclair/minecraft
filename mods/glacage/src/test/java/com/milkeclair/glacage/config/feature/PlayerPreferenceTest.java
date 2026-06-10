package com.milkeclair.glacage.config.feature;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.config.Config;
import com.milkeclair.glacage.helpers.FakeConfig;
import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PlayerPreference")
class PlayerPreferenceTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    PlayerPreference.clear();
    for (var flag : Flag.values()) {
      Config.setEnabled(flag, flag.defaultEnabled());
    }
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

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();

        Config.setEnabled(Feature.LUMBERJACK, true);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
      }

      @Test
      @DisplayName("子設定のconfig設定を返す")
      void returnsChildConfigSetting() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK.CHOP, false);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer()))
            .isFalse();

        Config.setEnabled(Feature.LUMBERJACK.CHOP, true);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer()))
            .isTrue();
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

        PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
      }

      @Test
      @DisplayName("子設定の同じUUIDの設定を返す")
      void returnsChildSettingForSameUuid() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), false);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer()))
            .isFalse();
      }

      @Test
      @DisplayName("別UUIDのプレイヤーには影響しない")
      void doesNotAffectDifferentUuid() {
        var disabledPlayer =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var otherPlayer =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        Config.setEnabled(Feature.LUMBERJACK, true);
        PlayerPreference.setEnabled(Feature.LUMBERJACK, disabledPlayer.serverPlayer(), false);

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, disabledPlayer.serverPlayer()))
            .isFalse();
        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, otherPlayer.serverPlayer()))
            .isTrue();
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

      PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);

      assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
    }

    @Test
    @DisplayName("子設定のプレイヤー別設定を無効にする")
    void setsChildPlayerSettingToDisabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK.CHOP, true);

      PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), false);

      assertThat(PlayerPreference.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer()))
          .isFalse();
    }

    @Test
    @DisplayName("プレイヤー別設定を有効にする")
    void setsPlayerSettingToEnabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK, false);

      PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);

      assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
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
        PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        PlayerPreference.remove(player.serverPlayer());

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
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
        PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        PlayerPreference.clear();

        assertThat(PlayerPreference.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
      }
    }
  }
}
