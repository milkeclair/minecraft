package com.milkeclair.glacage.config;

import com.milkeclair.glacage.Glacage;
import com.milkeclair.glacage.config.feature.Payload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/* 設定のネットワーク管理。 */
public class Network {
  /* eventにplayToServerで設定の同期を登録する。 */
  public static void register(RegisterPayloadHandlersEvent event) {
    var registrar = event.registrar(Glacage.MOD_ID);

    registrar.playToServer(Payload.TYPE, Payload.CODEC, Payload::handle);
    registrar.playToServer(
        com.milkeclair.glacage.usecases.parkour.doubleJump.Request.TYPE,
        com.milkeclair.glacage.usecases.parkour.doubleJump.Request.CODEC,
        com.milkeclair.glacage.usecases.parkour.doubleJump.Request::handle);
  }
}
