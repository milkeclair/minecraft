package com.milkeclair.glacage.config.feature;

/* 木こり機能の設定。 */
public class Lumberjack extends Flag {
  /* 木を一度に切り倒す。 */
  public final Flag CHOP = child("chop", "Break a whole tree when chopping a log.", true);

  public Lumberjack() {
    super(Group.LUMBERJACK, "enabled", "Lumberjack related", true);
  }
}
