package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.milkeclair.glacage.config.Config;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.usecases.foodie.Foodie;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Client")
class ClientTest {
  @AfterEach
  void reset() {
    Config.setSyncer(flag -> {});
  }

  @Nested
  @DisplayName("@Mod")
  class ModAnnotation {
    @Test
    @DisplayName("client専用の同じmod IDを指定する")
    void usesClientModId() {
      var annotation = Client.class.getAnnotation(Mod.class);

      assertThat(annotation).isNotNull();
      assertThat(annotation.value()).isEqualTo(Glacage.MOD_ID);
      assertThat(annotation.dist()).containsExactly(Dist.CLIENT);
    }
  }

  @Nested
  @DisplayName("new")
  class New {
    @Test
    @DisplayName("config screenを登録する")
    void registersConfigScreen() {
      var modEventBus = mock(IEventBus.class);
      var container = mock(ModContainer.class);

      new Client(modEventBus, container);

      verify(container)
          .registerExtensionPoint(eq(IConfigScreenFactory.class), any(IConfigScreenFactory.class));
    }

    @Test
    @DisplayName("Foodieを登録する")
    void registersFoodie() {
      var modEventBus = mock(IEventBus.class);
      var container = mock(ModContainer.class);

      new Client(modEventBus, container);

      verify(modEventBus).register(isA(Foodie.class));
    }

    @Nested
    @DisplayName("Minecraft初期化前の場合")
    class BeforeMinecraftInitialization {
      @Test
      @DisplayName("config同期で例外を投げない")
      void doesNotThrowOnConfigSync() {
        var modEventBus = mock(IEventBus.class);
        var container = mock(ModContainer.class);

        new Client(modEventBus, container);

        assertThatCode(() -> Config.sync(Feature.LUMBERJACK)).doesNotThrowAnyException();
      }
    }
  }
}
