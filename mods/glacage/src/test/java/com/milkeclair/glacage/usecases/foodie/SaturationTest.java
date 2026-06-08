package com.milkeclair.glacage.usecases.foodie;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.milkeclair.glacage.models.Satiety;
import com.milkeclair.glacage.models.satiety.Bar;
import com.milkeclair.glacage.usecases.foodie.saturation.State;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Saturation")
class SaturationTest {
  @Nested
  @DisplayName("#render")
  class Render {
    @Nested
    @DisplayName("描画できない場合")
    class NotRenderable {
      @Test
      @DisplayName("何もしない")
      void doesNothing() {
        var guiGraphics = mock(GuiGraphics.class);
        var state = new TestState(false, new Satiety(20, 5.0F, false), new Bar(200, 100, 49));

        new Saturation(guiGraphics, state).render();

        verifyNoInteractions(guiGraphics);
      }
    }

    @Nested
    @DisplayName("描画できる場合")
    class Renderable {
      @Test
      @DisplayName("隠し満腹度アイコンを描画する")
      void rendersSaturationIcons() {
        var guiGraphics = mock(GuiGraphics.class);
        var state = new TestState(true, new Satiety(20, 5.0F, false), new Bar(200, 100, 49));
        var fullSprite = Identifier.fromNamespaceAndPath("glacage", "hud/saturation_full");
        var halfSprite = Identifier.fromNamespaceAndPath("glacage", "hud/saturation_half");

        new Saturation(guiGraphics, state).render();

        verify(guiGraphics).blitSprite(RenderPipelines.GUI_TEXTURED, fullSprite, 182, 61, 9, 9);
        verify(guiGraphics).blitSprite(RenderPipelines.GUI_TEXTURED, fullSprite, 174, 61, 9, 9);
        verify(guiGraphics).blitSprite(RenderPipelines.GUI_TEXTURED, halfSprite, 166, 61, 9, 9);
        verifyNoMoreInteractions(guiGraphics);
      }
    }

    @Nested
    @DisplayName("空腹エフェクト中で描画できる場合")
    class RenderableWithHunger {
      @Test
      @DisplayName("空腹用の隠し満腹度アイコンを描画する")
      void rendersHungrySaturationIcon() {
        var guiGraphics = mock(GuiGraphics.class);
        var state = new TestState(true, new Satiety(20, 1.0F, true), new Bar(200, 100, 49));
        var halfHungerSprite =
            Identifier.fromNamespaceAndPath("glacage", "hud/saturation_half_hunger");

        new Saturation(guiGraphics, state).render();

        verify(guiGraphics)
            .blitSprite(RenderPipelines.GUI_TEXTURED, halfHungerSprite, 182, 61, 9, 9);
        verifyNoMoreInteractions(guiGraphics);
      }
    }
  }

  private record TestState(boolean canRender, Satiety satiety, Bar bar) implements State {}
}
