package com.milkeclair.glacage.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

import com.milkeclair.glacage.config.Feature;
import com.milkeclair.glacage.config.Features;
import com.milkeclair.glacage.usecases.foodie.Saturation;
import java.util.ArrayList;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Foodie")
class FoodieTest {
  @AfterEach
  void reset() {
    Features.clear();
  }

  @Nested
  @DisplayName("#register")
  class Register {
    @Test
    @DisplayName("food levelの上にsaturation layerを登録する")
    void registersLayerAboveFoodLevel() {
      var layers = new ArrayList<GuiLayerManager.NamedLayer>();
      layers.add(new GuiLayerManager.NamedLayer(VanillaGuiLayers.FOOD_LEVEL, (gui, delta) -> {}));

      new Foodie().register(new RegisterGuiLayersEvent(layers));

      assertThat(layers)
          .extracting(GuiLayerManager.NamedLayer::name)
          .containsExactly(VanillaGuiLayers.FOOD_LEVEL, Foodie.SATURATION_LAYER_ID);
    }
  }

  @Nested
  @DisplayName("#renderSaturation")
  class RenderSaturation {
    @Nested
    @DisplayName("機能が有効の場合")
    class EnabledFeature {
      @Test
      @DisplayName("Saturationに描画を委譲する")
      void delegatesRenderingToSaturation() {
        var guiGraphics = mock(GuiGraphics.class);
        var deltaTracker = mock(DeltaTracker.class);

        Features.forceEnable(Feature.FOODIE);

        try (var saturations = mockConstruction(Saturation.class)) {
          new Foodie().renderSaturation(guiGraphics, deltaTracker);

          assertThat(saturations.constructed()).hasSize(1);
          var saturation = saturations.constructed().getFirst();
          verify(saturation).render();
        }
      }
    }

    @Nested
    @DisplayName("機能が無効の場合")
    class DisabledFeature {
      @Test
      @DisplayName("Saturationに描画を委譲しない")
      void doesNotDelegateRenderingToSaturation() {
        var guiGraphics = mock(GuiGraphics.class);
        var deltaTracker = mock(DeltaTracker.class);

        Features.forceDisable(Feature.FOODIE);

        try (var saturations = mockConstruction(Saturation.class)) {
          new Foodie().renderSaturation(guiGraphics, deltaTracker);

          assertThat(saturations.constructed()).isEmpty();
        }
      }
    }
  }
}
