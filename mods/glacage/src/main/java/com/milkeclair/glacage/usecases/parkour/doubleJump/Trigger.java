package com.milkeclair.glacage.usecases.parkour.doubleJump;

import com.milkeclair.glacage.models.livingPlayer.LivingPlayer;
import java.util.function.BooleanSupplier;
import net.minecraft.world.entity.player.Player;

/* 二段ジャンプ要求の発火条件。 */
public class Trigger {
  private boolean available;
  private boolean jumping;
  private boolean releasedInAir;

  /* ジャンプ入力から二段ジャンプ要求を処理する。 */
  public void call(Player player, boolean jumpKeyDown, BooleanSupplier action) {
    var stableGround = new LivingPlayer(player).contact().stable();

    resetOnStableGround(stableGround);

    if (!jumpKeyDown) {
      releaseJump(stableGround);
      return;
    }

    pressJump(stableGround, action);
  }

  private void resetOnStableGround(boolean stableGround) {
    if (stableGround) {
      available = true;
      releasedInAir = false;
    }
  }

  private void releaseJump(boolean stableGround) {
    jumping = false;
    if (!stableGround) {
      releasedInAir = true;
    }
  }

  private void pressJump(boolean stableGround, BooleanSupplier action) {
    if (jumping) {
      return;
    }

    jumping = true;

    if (!canRequestDoubleJump(stableGround)) {
      return;
    }

    if (action.getAsBoolean()) {
      available = false;
    }
  }

  private boolean canRequestDoubleJump(boolean stableGround) {
    return available && releasedInAir && !stableGround;
  }
}
