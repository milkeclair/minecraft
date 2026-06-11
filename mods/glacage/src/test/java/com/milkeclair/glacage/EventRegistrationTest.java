package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;

import com.milkeclair.glacage.usecases.lumberjack.Lumberjack;
import com.milkeclair.glacage.usecases.miner.Miner;
import net.neoforged.neoforge.common.NeoForge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("EventRegistration")
class EventRegistrationTest {
  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("呼び出した場合")
    class Called {
      @Test
      @DisplayName("イベントハンドラを登録する")
      void registersEventHandlers() {
        try (var lumberjacks = mockConstruction(Lumberjack.class);
            var miners = mockConstruction(Miner.class)) {
          new EventRegistration().call();

          assertThat(lumberjacks.constructed()).hasSize(1);
          assertThat(miners.constructed()).hasSize(1);
          NeoForge.EVENT_BUS.unregister(lumberjacks.constructed().getFirst());
          NeoForge.EVENT_BUS.unregister(miners.constructed().getFirst());
        }
      }
    }
  }
}
