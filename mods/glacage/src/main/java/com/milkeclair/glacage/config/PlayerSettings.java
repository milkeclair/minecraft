package com.milkeclair.glacage.config;

import com.milkeclair.glacage.Config;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/* プレイヤーの設定関連。 */
public class PlayerSettings {
  private static final EnumMap<Feature, HashMap<UUID, Boolean>> ENABLED =
      new EnumMap<>(Feature.class);

  /* 有効かどうかの判定。 */
  public static boolean enabled(Feature feature, Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      return enabledPlayers(feature).getOrDefault(serverPlayer.getUUID(), Config.enabled(feature));
    }

    return Config.enabled(feature);
  }

  /* 有効、無効の設定。 */
  public static void setEnabled(Feature feature, ServerPlayer player, boolean enabled) {
    enabledPlayers(feature).put(player.getUUID(), enabled);
  }

  /* 対象のplayerを除外する。 */
  public static void remove(ServerPlayer player) {
    for (var enabledPlayers : ENABLED.values()) {
      enabledPlayers.remove(player.getUUID());
    }
  }

  /* 一括削除。 */
  public static void clear() {
    ENABLED.clear();
  }

  private static HashMap<UUID, Boolean> enabledPlayers(Feature feature) {
    return ENABLED.computeIfAbsent(feature, ignored -> new HashMap<>());
  }
}
