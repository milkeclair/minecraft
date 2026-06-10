package com.milkeclair.glacage.usecases.foodie.saturation;

import com.milkeclair.glacage.models.satiety.Bar;
import com.milkeclair.glacage.models.satiety.Satiety;

/* 隠し満腹度表示に必要な状態。 */
public interface State {
  public boolean canRender();

  public Satiety satiety();

  public Bar bar();
}
