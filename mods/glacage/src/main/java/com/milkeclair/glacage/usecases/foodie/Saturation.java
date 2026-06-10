package com.milkeclair.glacage.usecases.foodie;

import com.milkeclair.glacage.usecases.foodie.saturation.Icons;
import com.milkeclair.glacage.usecases.foodie.saturation.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

/* 隠し満腹度。 */
public class Saturation {
  private static final int ICON_SIZE = 9;

  private final GuiGraphics guiGraphics;
  private final State state;

  public Saturation(GuiGraphics guiGraphics) {
    this(guiGraphics, new SaturationState(Minecraft.getInstance(), guiGraphics));
  }

  public Saturation(GuiGraphics guiGraphics, State state) {
    this.guiGraphics = guiGraphics;
    this.state = state;
  }

  /* 隠し満腹度の表示。 */
  public void render() {
    if (!state.canRender()) {
      return;
    }

    var icons = new Icons(state.satiety(), state.bar()).collect();

    for (var icon : icons) {
      guiGraphics.blitSprite(
          RenderPipelines.GUI_TEXTURED, icon.sprite(), icon.x(), icon.y(), ICON_SIZE, ICON_SIZE);
    }
  }
}
