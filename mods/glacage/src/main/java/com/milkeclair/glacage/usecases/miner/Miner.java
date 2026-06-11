package com.milkeclair.glacage.usecases.miner;

import com.milkeclair.glacage.actions.Runner;
import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak.ObstructiveBlockBreak;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/* 炭鉱夫機能。 */
public class Miner {
  /* 前方探索範囲。 */
  public static final int MAX_FORWARD_DISTANCE = 8;
  /* 上方探索範囲。 */
  public static final int MAX_UP_DISTANCE = 8;
  /* 最大の対象数。 */
  public static final int MAX_OBSTRUCTIVE_BLOCKS = 64;

  private final Runner runner = new Runner(Feature.MINER.OBSTRUCTIVE_BLOCK_BREAK);

  /* 破壊アクション。 */
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void breakObstructiveBlock(BlockEvent.BreakEvent event) {
    runner.call(event.getPlayer(), () -> new ObstructiveBlockBreak(event).call());
  }
}
