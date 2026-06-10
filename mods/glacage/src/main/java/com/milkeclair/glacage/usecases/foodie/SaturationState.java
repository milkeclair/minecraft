package com.milkeclair.glacage.usecases.foodie;

import com.milkeclair.glacage.models.Satiety;
import com.milkeclair.glacage.models.satiety.Bar;
import com.milkeclair.glacage.models.satiety.Visibility;
import com.milkeclair.glacage.usecases.foodie.saturation.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

/* Minecraftから隠し満腹度表示に必要な状態を取り出す。 */
public class SaturationState implements State {
  private final Minecraft minecraft;
  private final GuiGraphics guiGraphics;

  public SaturationState(Minecraft minecraft, GuiGraphics guiGraphics) {
    this.minecraft = minecraft;
    this.guiGraphics = guiGraphics;
  }

  /* レンダーできるかの判定。 */
  @Override
  public boolean canRender() {
    return visibility().canRender();
  }

  /* 満腹度の状態。 */
  @Override
  public Satiety satiety() {
    var foodData = minecraft.player.getFoodData();

    return new Satiety(
        foodData.getFoodLevel(),
        foodData.getSaturationLevel(),
        minecraft.player.hasEffect(MobEffects.HUNGER));
  }

  /* 満腹度のバー。 */
  @Override
  public Bar bar() {
    return new Bar(guiGraphics.guiWidth(), guiGraphics.guiHeight(), minecraft.gui.rightHeight);
  }

  private Visibility visibility() {
    if (minecraft == null) {
      return new Visibility(false, false, false, false, false, false);
    }

    var hasPlayer = minecraft.player != null;
    var hasGameMode = minecraft.gameMode != null;

    return new Visibility(
        true,
        hasPlayer,
        hasGameMode,
        minecraft.options.hideGui,
        hasGameMode && minecraft.gameMode.canHurtPlayer(),
        hasPlayer
            && minecraft.player.getVehicle() instanceof LivingEntity vehicle
            && vehicle.showVehicleHealth());
  }
}
