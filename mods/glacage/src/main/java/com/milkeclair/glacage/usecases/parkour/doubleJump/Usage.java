package com.milkeclair.glacage.usecases.parkour.doubleJump;

import java.util.HashSet;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;

/* 二段ジャンプの使用状態。 */
public class Usage {
  private final HashSet<UUID> usedPlayerIds = new HashSet<>();

  /* 二段ジャンプできる状態かどうか。 */
  public boolean available(Player player) {
    return !usedPlayerIds.contains(player.getUUID());
  }

  /* 二段ジャンプを使用済みにする。 */
  public void use(Player player) {
    usedPlayerIds.add(player.getUUID());
  }

  /* 二段ジャンプを再度使える状態にする。 */
  public void reset(Player player) {
    usedPlayerIds.remove(player.getUUID());
  }
}
