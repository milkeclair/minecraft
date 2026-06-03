package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;

import net.neoforged.fml.common.Mod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Glacage")
class GlacageTest {
  @Nested
  @DisplayName("MOD_ID")
  class ModId {
    @Test
    @DisplayName("modのIDを返す")
    void returnsModId() {
      assertThat(Glacage.MOD_ID).isEqualTo("glacage");
    }
  }

  @Nested
  @DisplayName("@Mod")
  class ModAnnotation {
    @Test
    @DisplayName("MOD_IDと同じ値を指定する")
    void usesModId() {
      var annotation = Glacage.class.getAnnotation(Mod.class);

      assertThat(annotation).isNotNull();
      assertThat(annotation.value()).isEqualTo(Glacage.MOD_ID);
    }
  }
}
