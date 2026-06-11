package com.milkeclair.glacage.actions;

import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import net.minecraft.world.entity.player.Player;

/* アクションの実行ラッパー。 */
public class Runner {
  private final Flag flag;
  private boolean isRunning = false;

  public Runner(Flag flag) {
    this.flag = flag;
  }

  /* 機能が有効かの判定。 */
  public boolean enabled() {
    return Feature.enabled(flag);
  }

  /* 機能が有効かの判定。 */
  public boolean enabled(Player player) {
    return Feature.enabled(flag, player);
  }

  /* コンカレント処理を含めた実行。 */
  public void call(Player player, Runnable action) {
    if (!enabled(player)) {
      return;
    }

    if (isRunning) {
      return;
    }

    isRunning = true;
    try {
      action.run();
    } finally {
      isRunning = false;
    }
  }
}
