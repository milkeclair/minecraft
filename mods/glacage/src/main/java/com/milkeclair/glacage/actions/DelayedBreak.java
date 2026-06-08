package com.milkeclair.glacage.actions;

import com.milkeclair.glacage.actions.delayedBreak.Queue;
import java.util.LinkedHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** tickごとにブロックを壊す演出。 */
public class DelayedBreak {
  private final ServerPlayer player;
  private final ServerLevel level;
  private final Queue blocks;
  private final LinkedHashSet<BlockPos> durabilityBlocks;

  public DelayedBreak(ServerPlayer player, ServerLevel level, LinkedHashSet<BlockPos> blocks) {
    this(player, level, blocks, new LinkedHashSet<>());
  }

  public DelayedBreak(
      ServerPlayer player,
      ServerLevel level,
      LinkedHashSet<BlockPos> blocks,
      LinkedHashSet<BlockPos> durabilityBlocks) {
    this.player = player;
    this.level = level;
    this.blocks = new Queue(blocks);
    this.durabilityBlocks = new LinkedHashSet<>(durabilityBlocks);
  }

  /* プレイヤーの取得。 */
  public ServerPlayer player() {
    return player;
  }

  /** 各tickの処理。 */
  public void tick() {
    for (var pos : blocks.nextBatch()) {
      if (level.isEmptyBlock(pos)) {
        continue;
      }

      if (durabilityBlocks.contains(pos)) {
        player.gameMode.destroyBlock(pos);
      } else {
        level.destroyBlock(pos, true, player);
      }
    }
  }

  /** queueが残っていないことの判定。 */
  public boolean isFinished() {
    return blocks.isEmpty();
  }
}
