package com.milkeclair.glacage.actions.blockBreak;

import java.util.LinkedHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/* ブロックの破壊。 */
public class BlockBreak {
  private final ServerPlayer player;
  private final ServerLevel level;
  private final LinkedHashSet<BlockPos> blocks;
  private final LinkedHashSet<BlockPos> durabilityBlocks;

  public BlockBreak(ServerPlayer player, ServerLevel level, LinkedHashSet<BlockPos> blocks) {
    this(player, level, blocks, new LinkedHashSet<>());
  }

  public BlockBreak(
      ServerPlayer player,
      ServerLevel level,
      LinkedHashSet<BlockPos> blocks,
      LinkedHashSet<BlockPos> durabilityBlocks) {
    this.player = player;
    this.level = level;
    this.blocks = new LinkedHashSet<>(blocks);
    this.durabilityBlocks = new LinkedHashSet<>(durabilityBlocks);
  }

  /* 破壊実行。 */
  public void call() {
    for (var pos : blocks) {
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
}
