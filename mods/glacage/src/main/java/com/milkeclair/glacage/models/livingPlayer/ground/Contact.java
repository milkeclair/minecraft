package com.milkeclair.glacage.models.livingPlayer.ground;

import net.minecraft.world.entity.player.Player;

/* 地面との接触状態。 */
public class Contact {
  private final Player player;

  public Contact(Player player) {
    this.player = player;
  }

  /* 安定して接地しているかどうか。 */
  public boolean stable() {
    return player.onGround() && player.getDeltaMovement().y <= 0;
  }
}
