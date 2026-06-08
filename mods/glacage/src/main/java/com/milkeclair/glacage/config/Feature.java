package com.milkeclair.glacage.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/* 機能設定。 */
public enum Feature {
  /* 木こり機能。 */
  LUMBERJACK("lumberjack", "Do you have three axes?", true),

  /* 隠し満腹度表示機能。 */
  FOODIE("foodie", "Can't you tell if you're full or not?", true);

  /*
   * codec。
   * mapはdecode, encodeの順で渡す。
   */
  public static final StreamCodec<ByteBuf, Feature> CODEC =
      ByteBufCodecs.STRING_UTF8.map(Feature::fromKey, Feature::key);

  private final String key;
  private final String comment;
  private final boolean defaultEnabled;

  private Feature(String key, String comment, boolean defaultEnabled) {
    this.key = key;
    this.comment = comment;
    this.defaultEnabled = defaultEnabled;
  }

  /* キーからFeatureを返す。 */
  public static Feature fromKey(String key) {
    for (var feature : values()) {
      if (feature.key.equals(key)) {
        return feature;
      }
    }

    throw new IllegalArgumentException("Unknown feature: " + key);
  }

  /* キーの値。 */
  public String key() {
    return key;
  }

  /* コメントの値。 */
  public String comment() {
    return comment;
  }

  /* デフォルト値。 */
  public boolean defaultEnabled() {
    return defaultEnabled;
  }

  /* I18nキー。 */
  public String translationKey() {
    return "glacage.configuration.features." + key;
  }
}
