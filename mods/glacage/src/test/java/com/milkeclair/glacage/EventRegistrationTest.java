package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;

import com.milkeclair.glacage.usecases.lumberjack.Lumberjack;
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
      @DisplayName("Lumberjackを登録する")
      void registersLumberjack() {
        try (var lumberjacks = mockConstruction(Lumberjack.class)) {
          new EventRegistration().call();

          assertThat(lumberjacks.constructed()).hasSize(1);
          NeoForge.EVENT_BUS.unregister(lumberjacks.constructed().getFirst());
        }
      }
    }
  }
}
