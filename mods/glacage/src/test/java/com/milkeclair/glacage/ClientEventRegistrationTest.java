package com.milkeclair.glacage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;

import net.neoforged.neoforge.common.NeoForge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ClientEventRegistration")
class ClientEventRegistrationTest {
  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("呼び出した場合")
    class Called {
      @Test
      @DisplayName("クライアントイベントハンドラを登録する")
      void registersEventHandlers() {
        try (var clients =
            mockConstruction(com.milkeclair.glacage.usecases.parkour.doubleJump.Client.class)) {
          new ClientEventRegistration().call();

          assertThat(clients.constructed()).hasSize(1);
          NeoForge.EVENT_BUS.unregister(clients.constructed().getFirst());
        }
      }
    }
  }
}
