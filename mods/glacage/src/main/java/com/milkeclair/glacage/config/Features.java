package com.milkeclair.glacage.config;

import com.milkeclair.glacage.config.feature.OverrideClient;
import java.util.EnumMap;
import net.minecraft.world.entity.player.Player;

/* 機能コンポーネント。 */
public class Features {
  private static final EnumMap<Feature, OverrideClient> OVERRIDES = new EnumMap<>(Feature.class);

  /* 最優先で有効化。 */
  public static void forceEnable(Feature feature) {
    OVERRIDES.put(feature, OverrideClient.ENABLED);
  }

  /* 最優先で無効化。 */
  public static void forceDisable(Feature feature) {
    OVERRIDES.put(feature, OverrideClient.DISABLED);
  }

  /* ユーザーレベルの個別設定。 */
  public static void useUserSetting(Feature feature) {
    OVERRIDES.put(feature, OverrideClient.USER);
  }

  /* 設定のクリア。 */
  public static void clear() {
    OVERRIDES.clear();
  }

  /* 有効かどうかの判定。 */
  public static boolean enabled(Feature feature) {
    return enabled(feature, null);
  }

  /* 有効かどうかの判定。 */
  public static boolean enabled(Feature feature, Player player) {
    return switch (override(feature)) {
      case ENABLED -> true;
      case DISABLED -> false;
      case USER -> PlayerSettings.enabled(feature, player);
    };
  }

  private static OverrideClient override(Feature feature) {
    return OVERRIDES.getOrDefault(feature, OverrideClient.USER);
  }
}
