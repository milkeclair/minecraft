package com.milkeclair.glacage.usecases.parkour.doubleJump;

import com.milkeclair.glacage.models.livingPlayer.LivingPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/* サーバー側の二段ジャンプ。 */
public class Server {
  private static final Usage USAGE = new Usage();

  public Server() {}

  /* 二段ジャンプ状態のリセット。 */
  @SubscribeEvent
  public void resetDoubleJump(PlayerTickEvent.Post event) {
    var player = event.getEntity();

    if (player.level().isClientSide() || !new LivingPlayer(player).contact().stable()) {
      return;
    }

    reset(player);
  }

  /* 二段ジャンプを実行する。 */
  public static void call(Player player) {
    if (!USAGE.available(player)) {
      return;
    }

    if (new Action(player).call()) {
      USAGE.use(player);
    }
  }

  /* 二段ジャンプを再度使える状態にする。 */
  public static void reset(Player player) {
    USAGE.reset(player);
  }
}
