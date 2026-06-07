package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.config.Feature;
import com.milkeclair.glacage.helpers.FakeConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Config")
class ConfigTest {
  @BeforeEach
  void loadConfig() {
    FakeConfig.load();
  }

  @AfterEach
  void reset() {
    Config.setEnabled(Feature.LUMBERJACK, true);
    Config.setSyncer(feature -> {});
  }

  @Nested
  @DisplayName("SPEC")
  class Spec {
    @Test
    @DisplayName("Featureをfeatures配下のboolean設定として定義する")
    void definesFeatureAsBooleanConfigUnderFeatures() {
      var path = List.of("features", Feature.LUMBERJACK.key());
      var value = Config.SPEC.getSpec().get(path);

      assertThat(Config.SPEC.getLevelComment(List.of("features")))
          .isEqualTo("Config of glacage features");
      assertThat(Config.SPEC.getLevelTranslationKey(List.of("features")))
          .isEqualTo("glacage.configuration.features");
      assertThat(value).isInstanceOf(ModConfigSpec.ValueSpec.class);

      var valueSpec = (ModConfigSpec.ValueSpec) value;

      assertThat(valueSpec.getDefault()).isEqualTo(Feature.LUMBERJACK.defaultEnabled());
      assertThat(valueSpec.getComment()).isEqualTo(Feature.LUMBERJACK.comment());
      assertThat(valueSpec.getTranslationKey()).isEqualTo(Feature.LUMBERJACK.translationKey());
      assertThat(valueSpec.getClazz()).isEqualTo(Boolean.class);
      assertThat(valueSpec.test(true)).isTrue();
      assertThat(valueSpec.test(false)).isTrue();
      assertThat(valueSpec.test("true")).isTrue();
      assertThat(valueSpec.test("unknown")).isFalse();
    }
  }

  @Nested
  @DisplayName(".register")
  class Register {
    @Test
    @DisplayName("client configと読み込みイベントを登録する")
    void registersClientConfigAndLoadingEvents() {
      var container = mock(ModContainer.class);
      var modEventBus = mock(IEventBus.class);

      Config.register(container, modEventBus);

      verify(container).registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
      verify(modEventBus).addListener(eq(ModConfigEvent.Loading.class), any());
      verify(modEventBus).addListener(eq(ModConfigEvent.Reloading.class), any());
    }

    @Nested
    @DisplayName("Loadingイベントが発火した場合")
    class LoadingEvent {
      @Test
      @DisplayName("同じspecならすべてのFeatureを同期する")
      void syncsAllFeaturesForSameSpec() {
        var container = mock(ModContainer.class);
        var modEventBus = BusBuilder.builder().build();
        var synced = new ArrayList<Feature>();

        Config.setSyncer(synced::add);
        Config.register(container, modEventBus);

        modEventBus.post(new ModConfigEvent.Loading(modConfig(Config.SPEC)));

        assertThat(synced).containsExactly(Feature.values());
      }

      @Test
      @DisplayName("別のspecなら同期しない")
      void ignoresDifferentSpec() {
        var container = mock(ModContainer.class);
        var modEventBus = BusBuilder.builder().build();
        var synced = new ArrayList<Feature>();
        var otherSpec = new ModConfigSpec.Builder().build();

        Config.setSyncer(synced::add);
        Config.register(container, modEventBus);

        modEventBus.post(new ModConfigEvent.Loading(modConfig(otherSpec)));

        assertThat(synced).isEmpty();
      }
    }

    @Nested
    @DisplayName("Reloadingイベントが発火した場合")
    class ReloadingEvent {
      @Test
      @DisplayName("同じspecならすべてのFeatureを同期する")
      void syncsAllFeaturesForSameSpec() {
        var container = mock(ModContainer.class);
        var modEventBus = BusBuilder.builder().build();
        var synced = new ArrayList<Feature>();

        Config.setSyncer(synced::add);
        Config.register(container, modEventBus);

        modEventBus.post(new ModConfigEvent.Reloading(modConfig(Config.SPEC)));

        assertThat(synced).containsExactly(Feature.values());
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
      @DisplayName("Featureのデフォルト値を返す")
      void returnsFeatureDefaultValue() {
        assertThat(Config.enabled(Feature.LUMBERJACK))
            .isEqualTo(Feature.LUMBERJACK.defaultEnabled());
      }
    }

    @Nested
    @DisplayName("設定が更新された場合")
    class Updated {
      @Test
      @DisplayName("更新後の値を返す")
      void returnsUpdatedValue() {
        Config.setEnabled(Feature.LUMBERJACK, false);

        assertThat(Config.enabled(Feature.LUMBERJACK)).isFalse();
      }
    }
  }

  @Nested
  @DisplayName(".setEnabled")
  class SetEnabled {
    @Test
    @DisplayName("設定を無効にする")
    void setsConfigToDisabled() {
      Config.setEnabled(Feature.LUMBERJACK, false);

      assertThat(Config.enabled(Feature.LUMBERJACK)).isFalse();
    }

    @Test
    @DisplayName("設定を有効にする")
    void setsConfigToEnabled() {
      Config.setEnabled(Feature.LUMBERJACK, false);

      Config.setEnabled(Feature.LUMBERJACK, true);

      assertThat(Config.enabled(Feature.LUMBERJACK)).isTrue();
    }
  }

  @Nested
  @DisplayName(".setSyncer")
  class SetSyncer {
    @Test
    @DisplayName("同期先を上書きする")
    void replacesSyncer() {
      var firstSynced = new ArrayList<Feature>();
      var secondSynced = new ArrayList<Feature>();

      Config.setSyncer(firstSynced::add);
      Config.setSyncer(secondSynced::add);

      Config.sync(Feature.LUMBERJACK);

      assertThat(firstSynced).isEmpty();
      assertThat(secondSynced).containsExactly(Feature.LUMBERJACK);
    }
  }

  @Nested
  @DisplayName(".sync")
  class Sync {
    @Nested
    @DisplayName("同期先が設定されている場合")
    class Syncer {
      @Test
      @DisplayName("同期対象のFeatureを渡す")
      void passesFeature() {
        var synced = new AtomicReference<Feature>();

        Config.setSyncer(synced::set);

        Config.sync(Feature.LUMBERJACK);

        assertThat(synced.get()).isEqualTo(Feature.LUMBERJACK);
      }
    }
  }

  @Nested
  @DisplayName(".syncAll")
  class SyncAll {
    @Nested
    @DisplayName("同期先が設定されている場合")
    class Syncer {
      @Test
      @DisplayName("すべてのFeatureを渡す")
      void passesAllFeatures() {
        var synced = new ArrayList<Feature>();

        Config.setSyncer(synced::add);

        Config.syncAll();

        assertThat(synced).containsExactly(Feature.values());
      }
    }
  }

  static ModConfig modConfig(IConfigSpec spec) {
    var config = mock(ModConfig.class);
    when(config.getSpec()).thenReturn(spec);

    return config;
  }
}
