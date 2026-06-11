package com.milkeclair.glacage.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.Group;
import com.milkeclair.glacage.helpers.FakeConfig;
import java.util.List;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ServerConfig")
class ServerConfigTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.loadServer();
  }

  @AfterEach
  void reset() {
    ServerConfig.setPriority(Priority.CLIENT);
    for (var flag : Flag.values()) {
      ServerConfig.setEnabled(flag, flag.defaultEnabled());
    }
  }

  @Nested
  @DisplayName("SPEC")
  class Spec {
    @Test
    @DisplayName("priorityをfeatures配下のenum設定として定義する")
    void definesPriorityAsEnumConfigUnderFeatures() {
      var value = ServerConfig.SPEC.getSpec().get(List.of("features", "priority"));

      assertThat(value).isInstanceOf(ModConfigSpec.ValueSpec.class);

      var valueSpec = (ModConfigSpec.ValueSpec) value;

      assertThat(valueSpec.getDefault()).isEqualTo(Priority.CLIENT);
      assertThat(valueSpec.getComment())
          .contains("Choose whether client or server feature settings take priority.")
          .contains("Allowed Values: CLIENT, SERVER");
      assertThat(valueSpec.getTranslationKey())
          .isEqualTo("glacage.configuration.features.priority");
    }

    @Test
    @DisplayName("Flagをfeatures配下のboolean設定として定義する")
    void definesFlagsAsBooleanConfigUnderFeatures() {
      assertThat(ServerConfig.SPEC.getLevelComment(List.of("features")))
          .isEqualTo("Server config of glacage features");
      assertThat(ServerConfig.SPEC.getLevelTranslationKey(List.of("features")))
          .isEqualTo("glacage.configuration.features");

      for (var group : Group.values()) {
        var groupFlag = Flag.fromGroup(group);

        assertThat(ServerConfig.SPEC.getLevelComment(List.of("features", group.key())))
            .isEqualTo(groupFlag.comment());
        assertThat(ServerConfig.SPEC.getLevelTranslationKey(List.of("features", group.key())))
            .isEqualTo(group.translationKey());
      }

      for (var flag : Flag.values()) {
        var path = List.of("features", flag.group().key(), flag.configKey());
        var value = ServerConfig.SPEC.getSpec().get(path);

        assertThat(value).isInstanceOf(ModConfigSpec.ValueSpec.class);

        var valueSpec = (ModConfigSpec.ValueSpec) value;

        assertThat(valueSpec.getDefault()).isEqualTo(flag.defaultEnabled());
        assertThat(valueSpec.getComment()).isEqualTo(flag.comment());
        assertThat(valueSpec.getTranslationKey()).isEqualTo(flag.translationKey());
        assertThat(valueSpec.getClazz()).isEqualTo(Boolean.class);
      }
    }
  }

  @Nested
  @DisplayName(".register")
  class Register {
    @Test
    @DisplayName("server configを登録する")
    void registersServerConfig() {
      var container = mock(ModContainer.class);

      ServerConfig.register(container);

      verify(container).registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }
  }

  @Nested
  @DisplayName(".priority")
  class PriorityValue {
    @Nested
    @DisplayName("設定が更新されていない場合")
    class DefaultValue {
      @Test
      @DisplayName("CLIENTを返す")
      void returnsClient() {
        assertThat(ServerConfig.priority()).isEqualTo(Priority.CLIENT);
      }
    }

    @Nested
    @DisplayName("設定が更新された場合")
    class Updated {
      @Test
      @DisplayName("更新後の値を返す")
      void returnsUpdatedValue() {
        ServerConfig.setPriority(Priority.SERVER);

        assertThat(ServerConfig.priority()).isEqualTo(Priority.SERVER);
      }
    }
  }

  @Nested
  @DisplayName(".enabled")
  class Enabled {
    @Nested
    @DisplayName("設定が更新されていない場合")
    class DefaultValue {
      @Test
      @DisplayName("Flagのデフォルト値を返す")
      void returnsGroupDefaultValue() {
        for (var flag : Flag.values()) {
          assertThat(ServerConfig.enabled(flag)).isEqualTo(flag.defaultEnabled());
        }
      }
    }

    @Nested
    @DisplayName("設定が更新された場合")
    class Updated {
      @Test
      @DisplayName("更新後の値を返す")
      void returnsUpdatedValue() {
        ServerConfig.setEnabled(Feature.LUMBERJACK.CHOP, false);

        assertThat(ServerConfig.enabled(Feature.LUMBERJACK.CHOP)).isFalse();
      }
    }
  }

  @Nested
  @DisplayName(".setEnabled")
  class SetEnabled {
    @Test
    @DisplayName("設定を無効にする")
    void setsConfigToDisabled() {
      ServerConfig.setEnabled(Feature.LUMBERJACK.CHOP, false);

      assertThat(ServerConfig.enabled(Feature.LUMBERJACK.CHOP)).isFalse();
    }

    @Test
    @DisplayName("設定を有効にする")
    void setsConfigToEnabled() {
      ServerConfig.setEnabled(Feature.LUMBERJACK.CHOP, false);

      ServerConfig.setEnabled(Feature.LUMBERJACK.CHOP, true);

      assertThat(ServerConfig.enabled(Feature.LUMBERJACK.CHOP)).isTrue();
    }
  }
}
