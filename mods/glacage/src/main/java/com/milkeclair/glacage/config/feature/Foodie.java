package com.milkeclair.glacage.config.feature;

/* グルメ機能の設定。 */
public class Foodie extends Flag {
  /* 隠し満腹度表示。 */
  public final Flag SATURATION =
      child("saturation", "Show hidden saturation on the food bar.", true);

  public Foodie() {
    super(Group.FOODIE, "enabled", "Food related", true);
  }
}
