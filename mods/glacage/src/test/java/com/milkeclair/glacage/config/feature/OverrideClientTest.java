package com.milkeclair.glacage.config.feature;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("OverrideClient")
class OverrideClientTest {
  @Nested
  @DisplayName(".values")
  class Values {
    @Test
    @DisplayName("ユーザー設定、強制有効、強制無効を持つ")
    void hasOverrideOptions() {
      assertThat(OverrideClient.values())
          .containsExactly(OverrideClient.USER, OverrideClient.ENABLED, OverrideClient.DISABLED);
    }
  }
}
