package com.milkeclair.glacage.usecases.parkour.doubleJump;

import com.milkeclair.glacage.models.livingPlayer.LivingPlayer;
import net.minecraft.world.entity.player.Player;

/* 二段ジャンプの実行。 */
public class Action {
  private final Player player;

  public Action(Player player) {
    this.player = player;
  }

  /* 二段ジャンプを実行する。 */
  public boolean call() {
    var livingPlayer = new LivingPlayer(player);
    if (livingPlayer.contact().stable() || !livingPlayer.jump().can()) {
      return false;
    }

    player.jumpFromGround();
    player.resetFallDistance();

    livingPlayer.markMotionChanged();

    return true;
  }
}
