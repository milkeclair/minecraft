package com.milkeclair.glacage.actions.delayedBreak;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import net.minecraft.core.BlockPos;

/** ブロック破壊のキュー。 */
public class Queue {
  private static final int BLOCKS_PER_TICK = 2;

  private final ArrayDeque<BlockPos> blocks;
  private final int blocksPerTick;

  public Queue(LinkedHashSet<BlockPos> blocks) {
    this(blocks, BLOCKS_PER_TICK);
  }

  public Queue(LinkedHashSet<BlockPos> blocks, int blocksPerTick) {
    if (blocksPerTick <= 0) {
      throw new IllegalArgumentException("blocksPerTick must be greater than 0");
    }

    this.blocks = new ArrayDeque<>(blocks);
    this.blocksPerTick = blocksPerTick;
  }

  /** 次のバッチを取得する。 */
  public LinkedHashSet<BlockPos> nextBatch() {
    var batch = new LinkedHashSet<BlockPos>();

    while (!blocks.isEmpty() && batch.size() < blocksPerTick) {
      batch.add(blocks.removeFirst());
    }

    return batch;
  }

  /** キューが空かどうかの判定。 */
  public boolean isEmpty() {
    return blocks.isEmpty();
  }
}
