package com.milkeclair.glacage.config.feature;

import com.milkeclair.glacage.config.ServerConfig;
import java.util.HashMap;
import net.minecraft.world.entity.player.Player;

/* 機能のAPI。 */
public class Feature {
  /* 木こり機能。 */
  public static final Lumberjack LUMBERJACK = new Lumberjack();
  /* グルメ機能。 */
  public static final Foodie FOODIE = new Foodie();
  /* 採掘機能。 */
  public static final Miner MINER = new Miner();

  private static final HashMap<Flag, Mode> OVERRIDES = new HashMap<>();

  private Feature() {}

  /* 最優先で有効化。 */
  public static void forceEnable(Flag flag) {
    OVERRIDES.put(flag, Mode.ENABLED);
  }

  /* 最優先で無効化。 */
  public static void forceDisable(Flag flag) {
    OVERRIDES.put(flag, Mode.DISABLED);
  }

  /* プレイヤーレベルの個別設定を使う。 */
  public static void usePlayerPreference(Flag flag) {
    OVERRIDES.put(flag, Mode.USER);
  }

  /* 設定のクリア。 */
  public static void clear() {
    OVERRIDES.clear();
  }

  /* 有効かどうかの判定。 */
  public static boolean enabled(Flag flag) {
    return enabled(flag, null);
  }

  /* 有効かどうかの判定。 */
  public static boolean enabled(Flag flag, Player player) {
    // 親カテゴリが無効なら子設定も無効になる。
    if (flag.parent().isPresent() && !enabled(flag.parent().get(), player)) {
      return false;
    }

    return switch (override(flag)) {
      case ENABLED -> true;
      case DISABLED -> false;
      case USER -> configured(flag, player);
    };
  }

  private static boolean configured(Flag flag, Player player) {
    return switch (ServerConfig.priority()) {
      case CLIENT -> PlayerPreference.enabled(flag, player);
      case SERVER -> ServerConfig.enabled(flag);
    };
  }

  private static Mode override(Flag flag) {
    return OVERRIDES.getOrDefault(flag, Mode.USER);
  }
}
