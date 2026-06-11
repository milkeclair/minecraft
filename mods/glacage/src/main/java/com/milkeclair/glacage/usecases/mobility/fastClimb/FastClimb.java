package com.milkeclair.glacage.usecases.mobility.fastClimb;

import com.milkeclair.glacage.models.sight.Sight;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/* 昇降高速化。 */
public class FastClimb {
  /* 昇降を始める視線角度。 */
  public static final float CLIMB_PITCH_THRESHOLD = 45;
  /* 1tickあたりの昇降速度。 */
  public static final double CLIMB_SPEED = 1;

  private final Player player;

  public FastClimb(Player player) {
    this.player = player;
  }

  /* 昇降速度を変更する。 */
  public void call() {
    if (!canClimbFast()) {
      return;
    }

    switch (new Sight(player.getXRot()).tilt(CLIMB_PITCH_THRESHOLD)) {
      case UP -> climb(CLIMB_SPEED);
      case DOWN -> climb(-CLIMB_SPEED);
      case CENTER -> {}
    }
  }

  private void climb(double speed) {
    player.move(MoverType.SELF, new Vec3(0, speed, 0));
    player.resetFallDistance();
  }

  private boolean canClimbFast() {
    return player.onClimbable()
        && !player.isSpectator()
        && !player.isShiftKeyDown()
        && !player.getAbilities().flying
        && !player.isInWater();
  }
}
