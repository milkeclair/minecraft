package com.milkeclair.glacage.config.feature;

/* 機能の大分類。 */
public enum Group {
  /* 木こり機能。 */
  LUMBERJACK("lumberjack"),

  /* グルメ機能。 */
  FOODIE("foodie"),

  /* 採掘機能。 */
  MINER("miner"),

  /* パルクール機能。 */
  PARKOUR("parkour");

  private final String key;

  private Group(String key) {
    this.key = key;
  }

  /* キーの値。 */
  public String key() {
    return key;
  }

  /* I18nキー。 */
  public String translationKey() {
    return "glacage.configuration.features." + key;
  }
}
