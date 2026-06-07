package com.milkeclair.glacage.actions.search.breadthFirst;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("OverflowPolicy")
class OverflowPolicyTest {
  @Nested
  @DisplayName(".values")
  class Values {
    @Test
    @DisplayName("上限超過時の扱いを持つ")
    void hasOverflowPolicies() {
      assertThat(OverflowPolicy.values())
          .containsExactly(OverflowPolicy.ELLIPSIS, OverflowPolicy.EMPTY);
    }
  }
}
