package com.milkeclair.glacage.usecases.parkour;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.player.Player;

/* パルクール入力の実行環境。 */
public class InputContext {
  private final Player player;
  private final KeyMapping jumpKey;
  private final ClientPacketListener connection;

  public InputContext(Minecraft minecraft) {
    this(
        minecraft == null ? null : minecraft.player,
        minecraft == null || minecraft.options == null ? null : minecraft.options.keyJump,
        minecraft == null ? null : minecraft.getConnection());
  }

  public InputContext(Player player, KeyMapping jumpKey, ClientPacketListener connection) {
    this.player = player;
    this.jumpKey = jumpKey;
    this.connection = connection;
  }

  /* 入力処理を実行できるかどうか。 */
  public boolean available() {
    return player != null && jumpKey != null && connection != null;
  }

  /* プレイヤー。 */
  public Player player() {
    return player;
  }

  /* ジャンプキーが押されているかどうか。 */
  public boolean jumpKeyDown() {
    return jumpKey != null && jumpKey.isDown();
  }
}
