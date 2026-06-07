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

@DisplayName("Features")
class FeaturesTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    Features.clear();
    PlayerSettings.clear();
    Config.setEnabled(Feature.LUMBERJACK, true);
  }

  @Nested
  @DisplayName(".forceEnable")
  class ForceEnable {
    @Test
    @DisplayName("開発者設定を強制有効にする")
    void setsDeveloperOverrideToEnabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Features.forceEnable(Feature.LUMBERJACK);
      Config.setEnabled(Feature.LUMBERJACK, false);
      PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);

      assertThat(Features.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
    }
  }

  @Nested
  @DisplayName(".forceDisable")
  class ForceDisable {
    @Test
    @DisplayName("開発者設定を強制無効にする")
    void setsDeveloperOverrideToDisabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Features.forceDisable(Feature.LUMBERJACK);
      Config.setEnabled(Feature.LUMBERJACK, true);
      PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);

      assertThat(Features.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
    }
  }

  @Nested
  @DisplayName(".useUserSetting")
  class UseUserSetting {
    @Test
    @DisplayName("強制有効を解除してユーザー設定を使う")
    void usesUserSettingAfterForceEnable() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK, true);
      PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
      Features.forceEnable(Feature.LUMBERJACK);

      Features.useUserSetting(Feature.LUMBERJACK);

      assertThat(Features.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
    }

    @Test
    @DisplayName("強制無効を解除してユーザー設定を使う")
    void usesUserSettingAfterForceDisable() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK, false);
      PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);
      Features.forceDisable(Feature.LUMBERJACK);

      Features.useUserSetting(Feature.LUMBERJACK);

      assertThat(Features.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
    }
  }

  @Nested
  @DisplayName(".clear")
  class Clear {
    @Test
    @DisplayName("開発者設定をすべて解除する")
    void clearsDeveloperOverrides() {
      Config.setEnabled(Feature.LUMBERJACK, true);
      Features.forceDisable(Feature.LUMBERJACK);

      Features.clear();

      assertThat(Features.enabled(Feature.LUMBERJACK)).isTrue();
    }
  }

  @Nested
  @DisplayName(".enabled")
  class Enabled {
    @Nested
    @DisplayName("開発者設定がUSERの場合")
    class User {
      @Test
      @DisplayName("ユーザー設定を返す")
      void returnsUserSetting() {
        Features.useUserSetting(Feature.LUMBERJACK);
        Config.setEnabled(Feature.LUMBERJACK, false);

        assertThat(Features.enabled(Feature.LUMBERJACK)).isFalse();

        Config.setEnabled(Feature.LUMBERJACK, true);

        assertThat(Features.enabled(Feature.LUMBERJACK)).isTrue();
      }

      @Test
      @DisplayName("プレイヤー別設定がある場合はその設定を返す")
      void returnsPlayerSetting() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK, true);
        PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        Features.useUserSetting(Feature.LUMBERJACK);

        assertThat(Features.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
      }
    }

    @Nested
    @DisplayName("開発者設定がENABLEDの場合")
    class DeveloperEnabled {
      @Test
      @DisplayName("ユーザー設定に関係なくtrueを返す")
      void returnsTrue() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK, false);
        PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        Features.forceEnable(Feature.LUMBERJACK);

        assertThat(Features.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
      }
    }

    @Nested
    @DisplayName("開発者設定がDISABLEDの場合")
    class DeveloperDisabled {
      @Test
      @DisplayName("ユーザー設定に関係なくfalseを返す")
      void returnsFalse() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK, true);
        PlayerSettings.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);
        Features.forceDisable(Feature.LUMBERJACK);

        assertThat(Features.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
      }
    }
  }
}
