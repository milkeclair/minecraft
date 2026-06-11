package com.milkeclair.glacage.actions.delayedBreak;

import com.milkeclair.glacage.config.feature.Flag;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;

/** 遅延破壊の実行管理。 */
public class Runner {
  private final com.milkeclair.glacage.actions.Runner runner;
  private final ArrayDeque<DelayedBreak> breaks = new ArrayDeque<>();

  public Runner(Flag flag) {
    runner = new com.milkeclair.glacage.actions.Runner(flag);
  }

  /** 遅延破壊を追加する。 */
  public void enqueue(Player player, Supplier<Optional<DelayedBreak>> createBreak) {
    runner.call(player, () -> createBreak.get().ifPresent(breaks::add));
  }

  /** キューの処理。 */
  public void tick() {
    if (!runner.enabled()) {
      breaks.clear();
      return;
    }

    if (breaks.isEmpty()) {
      return;
    }

    var current = breaks.peek();
    if (!runner.enabled(current.player())) {
      breaks.removeFirst();
      return;
    }

    runner.call(
        current.player(),
        () -> {
          current.tick();
          if (current.isFinished()) {
            breaks.removeFirst();
          }
        });
  }
}
