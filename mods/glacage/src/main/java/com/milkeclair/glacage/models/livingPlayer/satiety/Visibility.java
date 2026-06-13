package com.milkeclair.glacage.models.livingPlayer.satiety;

/* 満腹度ゲージの表示可否。 */
public class Visibility {
  private final boolean initialized;
  private final boolean hasPlayer;
  private final boolean hasGameMode;
  private final boolean hiddenGui;
  private final boolean canHurtPlayer;
  private final boolean vehicleHealthVisible;

  public Visibility(
      boolean initialized,
      boolean hasPlayer,
      boolean hasGameMode,
      boolean hiddenGui,
      boolean canHurtPlayer,
      boolean vehicleHealthVisible) {
    this.initialized = initialized;
    this.hasPlayer = hasPlayer;
    this.hasGameMode = hasGameMode;
    this.hiddenGui = hiddenGui;
    this.canHurtPlayer = canHurtPlayer;
    this.vehicleHealthVisible = vehicleHealthVisible;
  }

  /* 表示できるかどうか。 */
  public boolean canRender() {
    return initialized
        && hasPlayer
        && hasGameMode
        && !hiddenGui
        && canHurtPlayer
        && !vehicleHealthVisible;
  }
}
