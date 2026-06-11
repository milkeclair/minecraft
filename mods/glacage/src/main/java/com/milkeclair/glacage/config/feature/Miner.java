package com.milkeclair.glacage.config.feature;

/* 採掘機能の設定。 */
public class Miner extends Flag {
  /* ブランチマイニングで採掘を阻害するブロックを一度に壊す。 */
  public final Flag OBSTRUCTIVE_BLOCK_BREAK =
      child(
          "obstructive_block_break",
          "Break connected blocks that obstruct mining forward and upward while underground.",
          true);

  public Miner() {
    super(Group.MINER, "enabled", "Mining related", true);
  }
}
