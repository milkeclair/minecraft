package com.milkeclair.glacage.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Priority")
class PriorityTest {
  @Nested
  @DisplayName(".values")
  class Values {
    @Test
    @DisplayName("クライアント優先、サーバー優先を持つ")
    void hasPriorityOptions() {
      assertThat(Priority.values()).containsExactly(Priority.CLIENT, Priority.SERVER);
    }
  }
}
