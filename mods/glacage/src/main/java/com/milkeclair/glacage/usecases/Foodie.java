package com.milkeclair.glacage.usecases;

import com.milkeclair.glacage.Glacage;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.usecases.foodie.Saturation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/* グルメ。 */
public class Foodie {
  /* 隠し満腹度レイヤーのID。 */
  public static final Identifier SATURATION_LAYER_ID =
      Identifier.fromNamespaceAndPath(Glacage.MOD_ID, "foodie/saturation");

  /* 登録処理。 */
  @SubscribeEvent
  public void register(RegisterGuiLayersEvent event) {
    event.registerAbove(VanillaGuiLayers.FOOD_LEVEL, SATURATION_LAYER_ID, this::renderSaturation);
  }

  /* 隠し満腹度の表示処理。 deltaTrackerは使っていない。 */
  public void renderSaturation(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
    if (Feature.enabled(Feature.FOODIE.SATURATION)) {
      new Saturation(guiGraphics).render();
    }
  }
}
