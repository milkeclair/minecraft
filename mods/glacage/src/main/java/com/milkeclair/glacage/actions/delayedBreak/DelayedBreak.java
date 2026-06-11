package com.milkeclair.glacage.actions.delayedBreak;

import com.milkeclair.glacage.actions.blockBreak.BlockBreak;
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
    new BlockBreak(player, level, blocks.nextBatch(), durabilityBlocks).call();
  }

  /** queueが残っていないことの判定。 */
  public boolean isFinished() {
    return blocks.isEmpty();
  }
}
