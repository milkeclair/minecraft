package com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak;

import com.milkeclair.glacage.actions.blockBreak.BlockBreak;
import com.milkeclair.glacage.models.block.Obstructive;
import com.milkeclair.glacage.models.block.SolidBlock;
import com.milkeclair.glacage.models.ground.Ground;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.level.BlockEvent;

/* 邪魔なブロックの破壊。 */
public class ObstructiveBlockBreak {
  private final BlockEvent.BreakEvent event;

  public ObstructiveBlockBreak(BlockEvent.BreakEvent event) {
    this.event = event;
  }

  /* 邪魔なブロックを壊す。 */
  public void call() {
    if (event.isCanceled() || !(event.getPlayer() instanceof ServerPlayer player)) {
      return;
    }

    if (!new SolidBlock(event.getState()).isObstructiveTo(Obstructive.MINING)) {
      return;
    }

    if (!new Ground(player.level(), player.blockPosition()).isUnderground()) {
      return;
    }

    var level = player.level();
    var baseBlock = event.getState().getBlock();
    var blocks =
        new BlockCollection(level, event.getPos().immutable(), player.getDirection(), baseBlock)
            .call();
    if (blocks.isEmpty()) {
      return;
    }

    new BlockBreak(player, level, blocks, blocks).call();
  }
}
