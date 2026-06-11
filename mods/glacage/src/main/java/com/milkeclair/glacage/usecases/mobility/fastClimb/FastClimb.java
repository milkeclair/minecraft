package com.milkeclair.glacage.usecases.mobility.fastClimb;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/* 昇降高速化。 */
public class FastClimb {
  /* 1tickあたりの昇降速度。 */
  public static final double CLIMB_SPEED = 0.24;

  private final Player player;

  public FastClimb(Player player) {
    this.player = player;
  }

  /* 昇降速度を変更する。 */
  public void call() {
    if (!canClimbFast()) {
      return;
    }

    var movement = player.getDeltaMovement();
    if (movement.y > 0) {
      player.setDeltaMovement(new Vec3(movement.x, CLIMB_SPEED, movement.z));
    } else if (movement.y < 0) {
      player.setDeltaMovement(new Vec3(movement.x, -CLIMB_SPEED, movement.z));
    }
  }

  private boolean canClimbFast() {
    return player.onClimbable()
        && !player.isSpectator()
        && !player.isShiftKeyDown()
        && !player.getAbilities().flying
        && !player.isInWater();
  }
}
