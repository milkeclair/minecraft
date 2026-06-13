package com.milkeclair.glacage.models.livingPlayer.jump;

import net.minecraft.world.entity.player.Player;

/* ジャンプ状態。 */
public class Jump {
  private final Player player;

  public Jump(Player player) {
    this.player = player;
  }

  /* ジャンプできるかどうか。 */
  public boolean can() {
    if (player.onClimbable()
        || player.isInWater()
        || player.isInLava()
        || player.isFallFlying()
        || player.isPassenger()
        || player.isSpectator()) {
      return false;
    }

    var abilities = player.getAbilities().pack();
    return !abilities.flying() && !abilities.mayFly();
  }
}
