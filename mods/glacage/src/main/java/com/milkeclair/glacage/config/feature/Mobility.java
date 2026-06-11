package com.milkeclair.glacage.config.feature;

/* 移動機能。 */
public class Mobility extends Flag {
  /* 昇降高速化機能。 */
  public final Flag FAST_CLIMB = child("fast_climb", "Climb faster on climbable blocks.", true);

  public Mobility() {
    super(Group.MOBILITY, "enabled", "Mobility related", true);
  }
}
