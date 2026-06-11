package com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak;

import com.milkeclair.glacage.actions.delayedBreak.DelayedBreak;
import com.milkeclair.glacage.models.block.Obstructive;
import com.milkeclair.glacage.models.block.SolidBlock;
import com.milkeclair.glacage.models.ground.Ground;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.level.BlockEvent;

/* 邪魔なブロックの破壊。 */
public class ObstructiveBlockBreak {
  private final BlockEvent.BreakEvent event;

  public ObstructiveBlockBreak(BlockEvent.BreakEvent event) {
    this.event = event;
  }

  /* 遅延破壊を作成する。 */
  public Optional<DelayedBreak> call() {
    if (event.isCanceled() || !(event.getPlayer() instanceof ServerPlayer player)) {
      return Optional.empty();
    }

    if (!new SolidBlock(event.getState()).isObstructiveTo(Obstructive.MINING)) {
      return Optional.empty();
    }

    if (!new Ground(player.level(), player.blockPosition()).isUnderground()) {
      return Optional.empty();
    }

    var level = player.level();
    var baseBlock = event.getState().getBlock();
    var blocks = new BlockCollection(level, event.getPos().immutable(), player.getDirection(), baseBlock)
        .call();
    if (blocks.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new DelayedBreak(player, level, blocks, blocks));
  }
}
