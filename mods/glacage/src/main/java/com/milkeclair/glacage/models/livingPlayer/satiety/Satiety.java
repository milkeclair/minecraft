package com.milkeclair.glacage.models.livingPlayer.satiety;

/* 満腹度。 */
public class Satiety {
  private static final int MAX_FOOD_LEVEL = 20;

  private final int foodLevel;
  private final float saturation;
  private final boolean hungry;

  public Satiety(int foodLevel, float saturation, boolean hungry) {
    this.foodLevel = foodLevel;
    this.saturation = saturation;
    this.hungry = hungry;
  }

  /* 空腹状態かどうかの判定。 */
  public boolean isHungry() {
    return hungry;
  }

  /* 隠し満腹度ゲージ。 */
  public int saturationPoints() {
    if (saturation <= 0.0F) {
      return 0;
    }

    var clampedFoodLevel = Math.min(Math.max(foodLevel, 0), MAX_FOOD_LEVEL);

    // 20以上は20に丸める。
    return Math.min((int) Math.ceil(saturation), clampedFoodLevel);
  }
}
