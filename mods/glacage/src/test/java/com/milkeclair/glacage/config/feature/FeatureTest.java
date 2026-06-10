package com.milkeclair.glacage.config.feature;

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

@DisplayName("Feature")
class FeatureTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    Feature.clear();
    PlayerPreference.clear();
    for (var flag : Flag.values()) {
      Config.setEnabled(flag, flag.defaultEnabled());
    }
  }

  @Nested
  @DisplayName(".forceEnable")
  class ForceEnable {
    @Test
    @DisplayName("開発者設定を強制有効にする")
    void setsDeveloperOverrideToEnabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Feature.forceEnable(Feature.LUMBERJACK);
      Config.setEnabled(Feature.LUMBERJACK, false);
      PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);

      assertThat(Feature.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
    }

    @Test
    @DisplayName("子設定を強制有効にする")
    void setsChildDeveloperOverrideToEnabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Feature.forceEnable(Feature.LUMBERJACK.CHOP);
      Config.setEnabled(Feature.LUMBERJACK.CHOP, false);
      PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), false);

      assertThat(Feature.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer())).isTrue();
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

      Feature.forceDisable(Feature.LUMBERJACK);
      Config.setEnabled(Feature.LUMBERJACK, true);
      PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);

      assertThat(Feature.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
    }

    @Test
    @DisplayName("子設定を強制無効にする")
    void setsChildDeveloperOverrideToDisabled() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Feature.forceDisable(Feature.LUMBERJACK.CHOP);
      Config.setEnabled(Feature.LUMBERJACK.CHOP, true);
      PlayerPreference.setEnabled(Feature.LUMBERJACK.CHOP, player.serverPlayer(), true);

      assertThat(Feature.enabled(Feature.LUMBERJACK.CHOP, player.serverPlayer())).isFalse();
    }
  }

  @Nested
  @DisplayName(".usePlayerPreference")
  class UsePlayerPreference {
    @Test
    @DisplayName("強制有効を解除してユーザー設定を使う")
    void usesUserSettingAfterForceEnable() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK, true);
      PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
      Feature.forceEnable(Feature.LUMBERJACK);

      Feature.usePlayerPreference(Feature.LUMBERJACK);

      assertThat(Feature.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
    }

    @Test
    @DisplayName("強制無効を解除してユーザー設定を使う")
    void usesUserSettingAfterForceDisable() {
      var player =
          new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

      Config.setEnabled(Feature.LUMBERJACK, false);
      PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);
      Feature.forceDisable(Feature.LUMBERJACK);

      Feature.usePlayerPreference(Feature.LUMBERJACK);

      assertThat(Feature.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
    }
  }

  @Nested
  @DisplayName(".clear")
  class Clear {
    @Test
    @DisplayName("開発者設定をすべて解除する")
    void clearsDeveloperOverrides() {
      Config.setEnabled(Feature.LUMBERJACK, true);
      Feature.forceDisable(Feature.LUMBERJACK);

      Feature.clear();

      assertThat(Feature.enabled(Feature.LUMBERJACK)).isTrue();
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
        Feature.usePlayerPreference(Feature.LUMBERJACK);
        Config.setEnabled(Feature.LUMBERJACK, false);

        assertThat(Feature.enabled(Feature.LUMBERJACK)).isFalse();

        Config.setEnabled(Feature.LUMBERJACK, true);

        assertThat(Feature.enabled(Feature.LUMBERJACK)).isTrue();
      }

      @Test
      @DisplayName("プレイヤー別設定がある場合はその設定を返す")
      void returnsPlayerSetting() {
        var player =
            new FakePlayer().setUuid(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        Config.setEnabled(Feature.LUMBERJACK, true);
        PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        Feature.usePlayerPreference(Feature.LUMBERJACK);

        assertThat(Feature.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
      }

      @Test
      @DisplayName("親設定が無効の場合は子設定をfalseとして扱う")
      void returnsFalseForChildSettingWhenParentSettingIsDisabled() {
        Config.setEnabled(Feature.LUMBERJACK, false);
        Config.setEnabled(Feature.LUMBERJACK.CHOP, true);
        Feature.usePlayerPreference(Feature.LUMBERJACK);
        Feature.usePlayerPreference(Feature.LUMBERJACK.CHOP);

        assertThat(Feature.enabled(Feature.LUMBERJACK.CHOP)).isFalse();
      }

      @Test
      @DisplayName("親設定が有効の場合は子設定を返す")
      void returnsChildSettingWhenParentSettingIsEnabled() {
        Config.setEnabled(Feature.LUMBERJACK, true);
        Config.setEnabled(Feature.LUMBERJACK.CHOP, false);
        Feature.usePlayerPreference(Feature.LUMBERJACK);
        Feature.usePlayerPreference(Feature.LUMBERJACK.CHOP);

        assertThat(Feature.enabled(Feature.LUMBERJACK.CHOP)).isFalse();

        Config.setEnabled(Feature.LUMBERJACK.CHOP, true);

        assertThat(Feature.enabled(Feature.LUMBERJACK.CHOP)).isTrue();
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
        PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), false);
        Feature.forceEnable(Feature.LUMBERJACK);

        assertThat(Feature.enabled(Feature.LUMBERJACK, player.serverPlayer())).isTrue();
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
        PlayerPreference.setEnabled(Feature.LUMBERJACK, player.serverPlayer(), true);
        Feature.forceDisable(Feature.LUMBERJACK);

        assertThat(Feature.enabled(Feature.LUMBERJACK, player.serverPlayer())).isFalse();
      }
    }
  }
}
