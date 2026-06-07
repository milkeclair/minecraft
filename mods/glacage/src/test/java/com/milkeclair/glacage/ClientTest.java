package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.milkeclair.glacage.config.Feature;
import net.neoforged.api.distmarker.Dist;
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
    Config.setSyncer(feature -> {});
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
      var container = mock(ModContainer.class);

      new Client(container);

      verify(container)
          .registerExtensionPoint(eq(IConfigScreenFactory.class), any(IConfigScreenFactory.class));
    }

    @Test
    @DisplayName("Minecraft初期化前のconfig同期を無視する")
    void ignoresConfigSyncBeforeMinecraftInitialization() {
      var container = mock(ModContainer.class);

      new Client(container);

      assertThatCode(() -> Config.sync(Feature.LUMBERJACK)).doesNotThrowAnyException();
    }
  }
}
