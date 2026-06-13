package com.milkeclair.glacage.config.feature;

/* パルクール機能。 */
public class Parkour extends Flag {
  /* 空中でもう一度ジャンプできる機能。 */
  public final Flag DOUBLE_JUMP = child("double_jump", "Jump again while airborne.", true);
  /* 昇降高速化機能。 */
  public final Flag FAST_CLIMB = child("fast_climb", "Climb faster on climbable blocks.", true);

  public Parkour() {
    super(Group.PARKOUR, "enabled", "Parkour related", true);
  }
}
