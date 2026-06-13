package com.milkeclair.glacage;

import net.neoforged.neoforge.common.NeoForge;

/* クライアントイベント登録。 */
public class ClientEventRegistration {
  /* ゲームからクライアントイベントを受け取るためのバスに、各イベントを登録する。 */
  public void call() {
    NeoForge.EVENT_BUS.register(new com.milkeclair.glacage.usecases.parkour.doubleJump.Client());
  }
}
