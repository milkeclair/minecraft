package com.milkeclair.glacage.config.feature;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Group")
class GroupTest {
  @Nested
  @DisplayName("#key")
  class Key {
    @Test
    @DisplayName("config階層用のkeyを返す")
    void returnsKey() {
      assertThat(Group.LUMBERJACK.key()).isEqualTo("lumberjack");
      assertThat(Group.FOODIE.key()).isEqualTo("foodie");
    }
  }

  @Nested
  @DisplayName("#translationKey")
  class TranslationKey {
    @Test
    @DisplayName("configの翻訳keyを返す")
    void returnsTranslationKey() {
      assertThat(Group.LUMBERJACK.translationKey())
          .isEqualTo("glacage.configuration.features.lumberjack");
      assertThat(Group.FOODIE.translationKey()).isEqualTo("glacage.configuration.features.foodie");
    }
  }
}
