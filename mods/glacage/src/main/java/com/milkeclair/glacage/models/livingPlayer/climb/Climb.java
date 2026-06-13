package com.milkeclair.glacage.models.livingPlayer.climb;

import net.minecraft.world.entity.player.Player;

/* 登れる状態。 */
public class Climb {
  private final Player player;

  public Climb(Player player) {
    this.player = player;
  }

  /* 高速昇降できるかどうか。 */
  public boolean canMoveFast() {
    if (!player.onClimbable()
        || player.isSpectator()
        || player.isShiftKeyDown()
        || player.isInWater()) {
      return false;
    }

    return !player.getAbilities().pack().flying();
  }
}
