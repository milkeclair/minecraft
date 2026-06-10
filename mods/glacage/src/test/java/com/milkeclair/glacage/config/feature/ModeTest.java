package com.milkeclair.glacage.config.feature;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Mode")
class ModeTest {
  @Nested
  @DisplayName(".values")
  class Values {
    @Test
    @DisplayName("ユーザー設定、強制有効、強制無効を持つ")
    void hasOverrideOptions() {
      assertThat(Mode.values()).containsExactly(Mode.USER, Mode.ENABLED, Mode.DISABLED);
    }
  }
}
