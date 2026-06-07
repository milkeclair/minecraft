package com.milkeclair.glacage.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.milkeclair.glacage.Glacage;
import com.milkeclair.glacage.config.feature.SyncPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Network")
class NetworkTest {
  @Nested
  @DisplayName(".register")
  class Register {
    @Test
    @DisplayName("Feature設定同期payloadをserver-boundで登録する")
    void registersSyncPayload() {
      var event = mock(RegisterPayloadHandlersEvent.class);
      var registrar = mock(PayloadRegistrar.class);

      when(event.registrar(Glacage.MOD_ID)).thenReturn(registrar);
      when(registrar.playToServer(eq(SyncPayload.TYPE), eq(SyncPayload.CODEC), any()))
          .thenReturn(registrar);

      Network.register(event);

      verify(registrar).playToServer(eq(SyncPayload.TYPE), eq(SyncPayload.CODEC), any());
    }
  }
}
