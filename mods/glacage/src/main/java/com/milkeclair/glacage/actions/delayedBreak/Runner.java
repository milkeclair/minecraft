package com.milkeclair.glacage.actions.delayedBreak;

import com.milkeclair.glacage.config.feature.Feature;
import com.milkeclair.glacage.config.feature.Flag;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;

/** 遅延破壊の実行管理。 */
public class Runner {
  private final Flag flag;
  private final ArrayDeque<DelayedBreak> breaks = new ArrayDeque<>();
  private boolean isRunning = false;

  public Runner(Flag flag) {
    this.flag = flag;
  }

  /** 遅延破壊を追加する。 */
  public void enqueue(Player player, Supplier<Optional<DelayedBreak>> createBreak) {
    if (!Feature.enabled(flag, player)) {
      return;
    }

    if (isRunning) {
      return;
    }

    isRunning = true;
    try {
      createBreak.get().ifPresent(breaks::add);
    } finally {
      isRunning = false;
    }
  }

  /** キューの処理。 */
  public void tick() {
    if (!Feature.enabled(flag)) {
      breaks.clear();
      return;
    }

    if (isRunning || breaks.isEmpty()) {
      return;
    }

    isRunning = true;
    try {
      var current = breaks.peek();
      if (!Feature.enabled(flag, current.player())) {
        breaks.removeFirst();
        return;
      }

      current.tick();
      if (current.isFinished()) {
        breaks.removeFirst();
      }
    } finally {
      isRunning = false;
    }
  }
}
