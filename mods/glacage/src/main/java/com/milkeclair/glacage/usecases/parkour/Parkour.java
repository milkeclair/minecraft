package com.milkeclair.glacage.usecases.parkour;

import com.milkeclair.glacage.actions.Runner;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.usecases.parkour.fastClimb.FastClimb;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/* パルクール機能。 */
public class Parkour {
  private final Runner fastClimbRunner = new Runner(Feature.PARKOUR.FAST_CLIMB);

  /* 昇降アクション。 */
  @SubscribeEvent
  public void fastClimb(PlayerTickEvent.Post event) {
    var player = event.getEntity();
    fastClimbRunner.call(player, () -> new FastClimb(player).call());
  }
}
