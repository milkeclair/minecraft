package com.milkeclair.glacage;

import com.milkeclair.glacage.usecases.lumberjack.Lumberjack;
import com.milkeclair.glacage.usecases.miner.Miner;
import net.neoforged.neoforge.common.NeoForge;

/** イベント登録を行う。 */
public class EventRegistration {
  /** ゲームからイベントを受け取るためのバスに、各イベントを登録する。 */
  public void call() {
    NeoForge.EVENT_BUS.register(new Lumberjack());
    NeoForge.EVENT_BUS.register(new Miner());
  }
}
