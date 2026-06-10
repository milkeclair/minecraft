package com.milkeclair.glacage.usecases.foodie.saturation;

import com.milkeclair.glacage.Glacage;
import com.milkeclair.glacage.models.satiety.Bar;
import com.milkeclair.glacage.models.satiety.Satiety;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;

/* 隠し満腹度のアイコン状況。 */
public class Icons {
  private static final Identifier FOOD_HALF_SPRITE =
      Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "hud/saturation_half");
  private static final Identifier FOOD_FULL_SPRITE =
      Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "hud/saturation_full");
  private static final Identifier FOOD_HALF_HUNGER_SPRITE =
      Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "hud/saturation_half_hunger");
  private static final Identifier FOOD_FULL_HUNGER_SPRITE =
      Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "hud/saturation_full_hunger");
  private static final int MAX_ICONS = 10;

  private final Satiety satiety;
  private final Bar bar;

  public Icons(Satiety satiety, Bar bar) {
    this.satiety = satiety;
    this.bar = bar;
  }

  /* 隠し満腹度に表示するアイコンを収集する。 */
  public List<Icon> collect() {
    var icons = new ArrayList<Icon>();
    var saturationPoints = satiety.saturationPoints();

    for (var index = 0; index < MAX_ICONS; index++) {
      // アイコンは2ポイントでフルになる。
      // 2ポイントが完了して次のアイコンの半分の時、前回の2ポイント分を描画する。
      if (index * 2 + 1 < saturationPoints) {
        icons.add(new Icon(fullSprite(), bar.x(index), bar.y()));
      }

      if (index * 2 + 1 == saturationPoints) {
        icons.add(new Icon(halfSprite(), bar.x(index), bar.y()));
      }
    }

    return icons;
  }

  private Identifier halfSprite() {
    if (satiety.isHungry()) {
      return FOOD_HALF_HUNGER_SPRITE;
    }

    return FOOD_HALF_SPRITE;
  }

  private Identifier fullSprite() {
    if (satiety.isHungry()) {
      return FOOD_FULL_HUNGER_SPRITE;
    }

    return FOOD_FULL_SPRITE;
  }
}
