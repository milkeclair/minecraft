package com.milkeclair.glacage.config.feature;

import com.milkeclair.glacage.config.ClientConfig;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/* プレイヤー別に同期された設定。 */
public class PlayerPreference {
  private static final HashMap<Flag, HashMap<UUID, Boolean>> ENABLED = new HashMap<>();

  /* 有効かどうかの判定。 */
  public static boolean enabled(Flag flag, Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      return enabledPlayers(flag).getOrDefault(serverPlayer.getUUID(), ClientConfig.enabled(flag));
    }

    return ClientConfig.enabled(flag);
  }

  /* 有効、無効の設定。 */
  public static void setEnabled(Flag flag, ServerPlayer player, boolean enabled) {
    enabledPlayers(flag).put(player.getUUID(), enabled);
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

  private static HashMap<UUID, Boolean> enabledPlayers(Flag flag) {
    return ENABLED.computeIfAbsent(flag, ignored -> new HashMap<>());
  }
}
