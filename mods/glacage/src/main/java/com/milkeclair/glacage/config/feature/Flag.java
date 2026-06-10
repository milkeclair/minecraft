package com.milkeclair.glacage.config.feature;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/* 機能フラグ。 */
public class Flag {
  /*
   * codec。
   * mapはdecode, encodeの順で渡す。
   */
  public static final StreamCodec<ByteBuf, Flag> CODEC =
      ByteBufCodecs.STRING_UTF8.map(Flag::fromKey, Flag::key);

  private final Group group;
  private final String configKey;
  private final String comment;
  private final boolean defaultEnabled;
  private final Flag parent;

  public Flag(Group group, String configKey, String comment, boolean defaultEnabled) {
    this(group, configKey, comment, defaultEnabled, null);
  }

  private Flag(Group group, String configKey, String comment, boolean defaultEnabled, Flag parent) {
    this.group = group;
    this.configKey = configKey;
    this.comment = comment;
    this.defaultEnabled = defaultEnabled;
    this.parent = parent;
  }

  /* 大分類からFlagを返す。 */
  public static Flag fromGroup(Group group) {
    return switch (group) {
      case LUMBERJACK -> Feature.LUMBERJACK;
      case FOODIE -> Feature.FOODIE;
    };
  }

  /* キーからFlagを返す。 */
  public static Flag fromKey(String key) {
    var flag = byKey().get(key);
    if (flag != null) {
      return flag;
    }

    throw new IllegalArgumentException("Unknown feature flag: " + key);
  }

  /* すべてのFlag。 */
  public static Flag[] values() {
    return new Flag[] {
      Feature.LUMBERJACK, Feature.LUMBERJACK.CHOP, Feature.FOODIE, Feature.FOODIE.SATURATION
    };
  }

  /* 大分類配下のFlagを返す。 */
  public static List<Flag> forGroup(Group group) {
    var flags = new ArrayList<Flag>();
    for (var flag : values()) {
      if (flag.group == group) {
        flags.add(flag);
      }
    }

    return List.copyOf(flags);
  }

  /* 所属する大分類。 */
  public Group group() {
    return group;
  }

  /* payload用のkey。 */
  public String key() {
    return group.key() + "." + configKey;
  }

  /* config階層内のkey。 */
  public String configKey() {
    return configKey;
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
    return "glacage.configuration.features." + key();
  }

  /* 親フラグ。 */
  public Optional<Flag> parent() {
    return Optional.ofNullable(parent);
  }

  /* 子フラグの作成。 */
  public Flag child(String configKey, String comment, boolean defaultEnabled) {
    return new Flag(group, configKey, comment, defaultEnabled, this);
  }

  private static LinkedHashMap<String, Flag> byKey() {
    var flags = new LinkedHashMap<String, Flag>();
    for (var flag : values()) {
      flags.put(flag.key(), flag);
    }

    return flags;
  }
}
