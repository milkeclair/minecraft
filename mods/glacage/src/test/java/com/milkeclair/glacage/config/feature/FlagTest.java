package com.milkeclair.glacage.config.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Flag")
class FlagTest {
  @Nested
  @DisplayName("CODEC")
  class Codec {
    @Test
    @DisplayName("key文字列としてencode/decodeする")
    void encodesAndDecodesFlagKey() {
      var buffer = Unpooled.buffer();

      try {
        Flag.CODEC.encode(buffer, Feature.LUMBERJACK.CHOP);

        assertThat(Flag.CODEC.decode(buffer)).isEqualTo(Feature.LUMBERJACK.CHOP);
        Flag.CODEC.encode(buffer, Feature.FOODIE.SATURATION);

        assertThat(Flag.CODEC.decode(buffer)).isEqualTo(Feature.FOODIE.SATURATION);
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
      @DisplayName("Flagを返す")
      void returnsGroupSetting() {
        assertThat(Flag.fromKey("lumberjack.enabled")).isEqualTo(Feature.LUMBERJACK);
        assertThat(Flag.fromKey("lumberjack.chop")).isEqualTo(Feature.LUMBERJACK.CHOP);
        assertThat(Flag.fromKey("foodie.enabled")).isEqualTo(Feature.FOODIE);
        assertThat(Flag.fromKey("foodie.saturation")).isEqualTo(Feature.FOODIE.SATURATION);
      }
    }

    @Nested
    @DisplayName("存在しないkeyの場合")
    class MissingKey {
      @Test
      @DisplayName("例外を投げる")
      void throwsException() {
        assertThatThrownBy(() -> Flag.fromKey("unknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown feature flag: unknown");
      }
    }
  }

  @Nested
  @DisplayName(".fromGroup")
  class FromGroup {
    @Test
    @DisplayName("大分類のFlagを返す")
    void returnsParentSetting() {
      assertThat(Flag.fromGroup(Group.LUMBERJACK)).isEqualTo(Feature.LUMBERJACK);
      assertThat(Flag.fromGroup(Group.FOODIE)).isEqualTo(Feature.FOODIE);
    }
  }

  @Nested
  @DisplayName(".forGroup")
  class ForGroup {
    @Test
    @DisplayName("大分類配下の設定を返す")
    void returnsFlagsForGroup() {
      assertThat(Flag.forGroup(Group.LUMBERJACK))
          .containsExactly(Feature.LUMBERJACK, Feature.LUMBERJACK.CHOP);
      assertThat(Flag.forGroup(Group.FOODIE))
          .containsExactly(Feature.FOODIE, Feature.FOODIE.SATURATION);
    }
  }

  @Nested
  @DisplayName(".values")
  class Values {
    @Test
    @DisplayName("大分類と子設定を返す")
    void returnsAllFlags() {
      assertThat(Flag.values())
          .containsExactly(
              Feature.LUMBERJACK,
              Feature.LUMBERJACK.CHOP,
              Feature.FOODIE,
              Feature.FOODIE.SATURATION);
    }
  }

  @Nested
  @DisplayName("#group")
  class GroupValue {
    @Test
    @DisplayName("所属する大分類を返す")
    void returnsGroup() {
      assertThat(Feature.LUMBERJACK.group()).isEqualTo(Group.LUMBERJACK);
      assertThat(Feature.LUMBERJACK.CHOP.group()).isEqualTo(Group.LUMBERJACK);
      assertThat(Feature.FOODIE.group()).isEqualTo(Group.FOODIE);
      assertThat(Feature.FOODIE.SATURATION.group()).isEqualTo(Group.FOODIE);
    }
  }

  @Nested
  @DisplayName("#key")
  class Key {
    @Test
    @DisplayName("payload用のkeyを返す")
    void returnsKey() {
      assertThat(Feature.LUMBERJACK.key()).isEqualTo("lumberjack.enabled");
      assertThat(Feature.LUMBERJACK.CHOP.key()).isEqualTo("lumberjack.chop");
      assertThat(Feature.FOODIE.key()).isEqualTo("foodie.enabled");
      assertThat(Feature.FOODIE.SATURATION.key()).isEqualTo("foodie.saturation");
    }
  }

  @Nested
  @DisplayName("#configKey")
  class ConfigKey {
    @Test
    @DisplayName("config階層内のkeyを返す")
    void returnsConfigKey() {
      assertThat(Feature.LUMBERJACK.configKey()).isEqualTo("enabled");
      assertThat(Feature.LUMBERJACK.CHOP.configKey()).isEqualTo("chop");
      assertThat(Feature.FOODIE.configKey()).isEqualTo("enabled");
      assertThat(Feature.FOODIE.SATURATION.configKey()).isEqualTo("saturation");
    }
  }

  @Nested
  @DisplayName("#comment")
  class Comment {
    @Test
    @DisplayName("config fallback用の説明を返す")
    void returnsComment() {
      assertThat(Feature.LUMBERJACK.comment()).isEqualTo("Lumberjack related");
      assertThat(Feature.LUMBERJACK.CHOP.comment())
          .isEqualTo("Break a whole tree when chopping a log.");
      assertThat(Feature.FOODIE.comment()).isEqualTo("Food related");
      assertThat(Feature.FOODIE.SATURATION.comment())
          .isEqualTo("Show hidden saturation on the food bar.");
    }
  }

  @Nested
  @DisplayName("#defaultEnabled")
  class DefaultEnabled {
    @Test
    @DisplayName("デフォルトの有効状態を返す")
    void returnsDefaultEnabled() {
      assertThat(Feature.LUMBERJACK.defaultEnabled()).isTrue();
      assertThat(Feature.LUMBERJACK.CHOP.defaultEnabled()).isTrue();
      assertThat(Feature.FOODIE.defaultEnabled()).isTrue();
      assertThat(Feature.FOODIE.SATURATION.defaultEnabled()).isTrue();
    }
  }

  @Nested
  @DisplayName("#translationKey")
  class TranslationKey {
    @Test
    @DisplayName("configの翻訳keyを返す")
    void returnsTranslationKey() {
      assertThat(Feature.LUMBERJACK.translationKey())
          .isEqualTo("glacage.configuration.features.lumberjack.enabled");
      assertThat(Feature.LUMBERJACK.CHOP.translationKey())
          .isEqualTo("glacage.configuration.features.lumberjack.chop");
      assertThat(Feature.FOODIE.translationKey())
          .isEqualTo("glacage.configuration.features.foodie.enabled");
      assertThat(Feature.FOODIE.SATURATION.translationKey())
          .isEqualTo("glacage.configuration.features.foodie.saturation");
    }
  }

  @Nested
  @DisplayName("#parent")
  class Parent {
    @Test
    @DisplayName("大分類はemptyを返す")
    void returnsEmptyForParentSetting() {
      assertThat(Feature.LUMBERJACK.parent()).isEmpty();
      assertThat(Feature.FOODIE.parent()).isEmpty();
    }

    @Test
    @DisplayName("子設定は大分類を返す")
    void returnsParentForChildSetting() {
      assertThat(Feature.LUMBERJACK.CHOP.parent()).contains(Feature.LUMBERJACK);
      assertThat(Feature.FOODIE.SATURATION.parent()).contains(Feature.FOODIE);
    }
  }

  @Nested
  @DisplayName("#child")
  class Child {
    @Test
    @DisplayName("子Flagを作る")
    void returnsChildFlag() {
      var parent = new Flag(Group.LUMBERJACK, "enabled", "Parent comment", true);

      var child = parent.child("chop", "Child comment", false);

      assertThat(child.group()).isEqualTo(Group.LUMBERJACK);
      assertThat(child.key()).isEqualTo("lumberjack.chop");
      assertThat(child.configKey()).isEqualTo("chop");
      assertThat(child.comment()).isEqualTo("Child comment");
      assertThat(child.defaultEnabled()).isFalse();
      assertThat(child.parent()).contains(parent);
    }
  }
}
