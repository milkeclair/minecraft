package com.milkeclair.glacage.models.sight;

/* 視線モデル。 */
public class Sight {
  private final float pitch;

  public Sight(float pitch) {
    this.pitch = pitch;
  }

  /* 視線の傾き。 */
  public Tilt tilt(float threshold) {
    if (pitch <= -threshold) {
      return Tilt.UP;
    }

    if (pitch >= threshold) {
      return Tilt.DOWN;
    }

    return Tilt.CENTER;
  }
}
