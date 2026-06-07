package com.milkeclair.glacage.config;

import com.milkeclair.glacage.Glacage;
import com.milkeclair.glacage.config.feature.SyncPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/* 設定のネットワーク管理。 */
public class Network {
  /* eventにplayToServerで設定の同期を登録する。 */
  public static void register(RegisterPayloadHandlersEvent event) {
    event
        .registrar(Glacage.MOD_ID)
        .playToServer(SyncPayload.TYPE, SyncPayload.CODEC, SyncPayload::handle);
  }
}
