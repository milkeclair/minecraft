package com.milkeclair.glacage.usecases.parkour.doubleJump;

import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.usecases.parkour.InputContext;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/* クライアント側の二段ジャンプ。 */
public class Client {
  private final Trigger trigger = new Trigger();

  /* 二段ジャンプ入力。 */
  @SubscribeEvent
  public void doubleJump(ClientTickEvent.Post event) {
    tick(new InputContext(Minecraft.getInstance()));
  }

  /* クライアントtickで二段ジャンプ入力を処理する。 */
  public void tick(InputContext context) {
    if (!context.available()) {
      return;
    }

    var player = context.player();
    if (!Feature.enabled(Feature.PARKOUR.DOUBLE_JUMP, player)) {
      return;
    }

    trigger.call(
        player,
        context.jumpKeyDown(),
        () -> {
          if (!new Action(player).call()) {
            return false;
          }

          ClientPacketDistributor.sendToServer(new Request());
          return true;
        });
  }
}
