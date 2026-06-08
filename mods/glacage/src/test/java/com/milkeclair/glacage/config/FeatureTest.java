package com.milkeclair.glacage.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Feature")
class FeatureTest {
  @Nested
  @DisplayName("CODEC")
  class Codec {
    @Test
    @DisplayName("key文字列としてencode/decodeする")
    void encodesAndDecodesFeatureKey() {
      var buffer = Unpooled.buffer();

      try {
        Feature.CODEC.encode(buffer, Feature.LUMBERJACK);

        assertThat(Feature.CODEC.decode(buffer)).isEqualTo(Feature.LUMBERJACK);
        Feature.CODEC.encode(buffer, Feature.FOODIE);

        assertThat(Feature.CODEC.decode(buffer)).isEqualTo(Feature.FOODIE);
      } finally {
        buffer.release();
      }
    }
  }

  @Nested
  @DisplayName(".fromKey")
  class FromKey {
    @Nested
    @DisplayName("存在するkeyの場合")
    class ExistingKey {
      @Test
      @DisplayName("Featureを返す")
      void returnsFeature() {
        assertThat(Feature.fromKey("lumberjack")).isEqualTo(Feature.LUMBERJACK);
        assertThat(Feature.fromKey("foodie")).isEqualTo(Feature.FOODIE);
      }
    }

    @Nested
    @DisplayName("存在しないkeyの場合")
    class MissingKey {
      @Test
      @DisplayName("例外を投げる")
      void throwsException() {
        assertThatThrownBy(() -> Feature.fromKey("unknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown feature: unknown");
      }
    }
  }

  @Nested
  @DisplayName("#key")
  class Key {
    @Test
    @DisplayName("configとpayload用のkeyを返す")
    void returnsKey() {
      assertThat(Feature.LUMBERJACK.key()).isEqualTo("lumberjack");
      assertThat(Feature.FOODIE.key()).isEqualTo("foodie");
    }
  }

  @Nested
  @DisplayName("#comment")
  class Comment {
    @Test
    @DisplayName("config fallback用の説明を返す")
    void returnsComment() {
      assertThat(Feature.LUMBERJACK.comment()).isEqualTo("Do you have three axes?");
      assertThat(Feature.FOODIE.comment()).isEqualTo("Can't you tell if you're full or not?");
    }
  }

  @Nested
  @DisplayName("#defaultEnabled")
  class DefaultEnabled {
    @Test
    @DisplayName("デフォルトの有効状態を返す")
    void returnsDefaultEnabled() {
      assertThat(Feature.LUMBERJACK.defaultEnabled()).isTrue();
      assertThat(Feature.FOODIE.defaultEnabled()).isTrue();
    }
  }

  @Nested
  @DisplayName("#translationKey")
  class TranslationKey {
    @Test
    @DisplayName("configの翻訳keyを返す")
    void returnsTranslationKey() {
      assertThat(Feature.LUMBERJACK.translationKey())
          .isEqualTo("glacage.configuration.features.lumberjack");
      assertThat(Feature.FOODIE.translationKey())
          .isEqualTo("glacage.configuration.features.foodie");
    }
  }
}
