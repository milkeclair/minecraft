package com.milkeclair.glacage.usecases.miner.obstructiveBlockBreak;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;
import static org.mockito.Mockito.mockConstruction;

import com.milkeclair.glacage.actions.delayedBreak.DelayedBreak;
import com.milkeclair.glacage.helpers.FakeLevel;
import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("ObstructiveBlockBreak")
@ExtendWith(EphemeralTestServerProvider.class)
class ObstructiveBlockBreakTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("イベントがキャンセルされている場合")
    class CanceledEvent {
      @Test
      @DisplayName("処理を開始しない")
      void doesNotStart() {
        var level = new FakeLevel();
        var player =
            new FakePlayer()
                .setLevel(level.serverLevel())
                .setDirection(Direction.NORTH)
                .setBlockPosition(new BlockPos(0, 10, 0));
        var brokePos = new BlockPos(0, 10, 0);
        level.setHeight(0, 0, 20);
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());
        var event = level.breakEvent(brokePos, player.serverPlayer());
        event.setCanceled(true);

        assertThat(new ObstructiveBlockBreak(event).call()).isEmpty();
      }
    }

    @Nested
    @DisplayName("プレイヤーがサーバープレイヤーではない場合")
    class NotServerPlayer {
      @Test
      @DisplayName("処理を開始しない")
      void doesNotStart() {
        var level = new FakeLevel();
        var brokePos = new BlockPos(0, 0, 0);
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());
        var event = level.breakEvent(brokePos, (Player) null);

        assertThat(new ObstructiveBlockBreak(event).call()).isEmpty();
      }
    }

    @Nested
    @DisplayName("プレイヤーが地下にいない場合")
    class NotUnderground {
      @Test
      @DisplayName("処理を開始しない")
      void doesNotStart() {
        var level = new FakeLevel();
        var player =
            new FakePlayer()
                .setLevel(level.serverLevel())
                .setDirection(Direction.NORTH)
                .setBlockPosition(new BlockPos(0, 20, 0));
        var brokePos = new BlockPos(0, 20, 0);
        level.setHeight(0, 0, 40);
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());
        level.setBlock(brokePos.north(), Blocks.GRAVEL.defaultBlockState());

        assertThat(
                new ObstructiveBlockBreak(level.breakEvent(brokePos, player.serverPlayer())).call())
            .isEmpty();
      }
    }

    @Nested
    @DisplayName("地表判定に原木が含まれている場合")
    class SurfaceWithLogs {
      @Test
      @DisplayName("原木を除いた地表で地下判定する")
      void ignoresLogsForUndergroundCheck() {
        var level = new FakeLevel();
        var player =
            new FakePlayer()
                .setLevel(level.serverLevel())
                .setDirection(Direction.NORTH)
                .setBlockPosition(new BlockPos(0, 9, 0));
        var brokePos = new BlockPos(0, 9, 0);
        level.setHeight(0, 0, 20);

        for (var y = 10; y < 20; y++) {
          level.setBlock(new BlockPos(0, y, 0), Blocks.OAK_LOG.defaultBlockState());
        }
        level.setBlock(new BlockPos(0, 9, 0), Blocks.DIRT.defaultBlockState());
        level.setBlock(brokePos.north(), Blocks.DIRT.defaultBlockState());

        assertThat(
                new ObstructiveBlockBreak(level.breakEvent(brokePos, player.serverPlayer())).call())
            .isEmpty();
      }
    }

    @Nested
    @DisplayName("壊したブロックが対象ではない場合")
    class NotObstructiveBlock {
      @Test
      @DisplayName("処理を開始しない")
      void doesNotStart() {
        var level = new FakeLevel();
        var player = new FakePlayer();
        var brokePos = new BlockPos(0, 10, 0);
        level.setBlock(brokePos, Blocks.STONE.defaultBlockState());

        assertThat(
                new ObstructiveBlockBreak(level.breakEvent(brokePos, player.serverPlayer())).call())
            .isEmpty();
      }
    }

    @Nested
    @DisplayName("追加で壊せるブロックがない場合")
    class NoAdditionalBlock {
      @Test
      @DisplayName("遅延破壊を作成しない")
      void doesNotCreateDelayedBreak() {
        var level = new FakeLevel();
        var player =
            new FakePlayer()
                .setLevel(level.serverLevel())
                .setDirection(Direction.NORTH)
                .setBlockPosition(new BlockPos(0, 10, 0));
        var brokePos = new BlockPos(0, 10, 0);
        level.setHeight(0, 0, 40);
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());

        assertThat(
                new ObstructiveBlockBreak(level.breakEvent(brokePos, player.serverPlayer())).call())
            .isEmpty();
      }
    }

    @Nested
    @DisplayName("砂利を追加で壊せる場合")
    class BreakableGravel {
      FakeLevel level;
      FakePlayer player;
      BlockPos brokePos;
      BlockPos forward;
      BlockPos upward;
      AtomicReference<List<?>> delayedBreakArguments;
      List<DelayedBreak> delayedBreaks;
      Optional<DelayedBreak> result;

      @BeforeEach
      void call() {
        level = new FakeLevel();
        player =
            new FakePlayer()
                .setLevel(level.serverLevel())
                .setDirection(Direction.NORTH)
                .setBlockPosition(new BlockPos(0, 10, 0));
        brokePos = new BlockPos(0, 10, 0);
        forward = brokePos.north();
        upward = brokePos.above();
        delayedBreakArguments = new AtomicReference<>();
        level.setHeight(0, 0, 40);
        level.setBlock(brokePos, Blocks.GRAVEL.defaultBlockState());
        level.setBlock(forward, Blocks.GRAVEL.defaultBlockState());
        level.setBlock(upward, Blocks.GRAVEL.defaultBlockState());

        try (var mockedDelayedBreaks =
            mockConstruction(
                DelayedBreak.class,
                (mock, context) -> delayedBreakArguments.set(context.arguments()))) {
          result =
              new ObstructiveBlockBreak(level.breakEvent(brokePos, player.serverPlayer())).call();
          delayedBreaks = List.copyOf(mockedDelayedBreaks.constructed());
        }
      }

      @Test
      @DisplayName("遅延破壊を返す")
      void returnsDelayedBreak() {
        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(delayedBreaks.getFirst());
      }

      @Test
      @DisplayName("プレイヤーとlevelを遅延破壊に渡す")
      void passesPlayerAndLevelToDelayedBreak() {
        assertThat(delayedBreakArguments.get()).hasSize(4);
        assertThat(delayedBreakArguments.get().get(0)).isSameAs(player.serverPlayer());
        assertThat(delayedBreakArguments.get().get(1)).isSameAs(level.serverLevel());
      }

      @Test
      @DisplayName("追加で壊す砂利を遅延破壊に渡す")
      void passesAdditionalGravelToDelayedBreak() {
        assertThat(delayedBreakArguments.get()).hasSize(4);
        assertThat(delayedBreakArguments.get().get(2))
            .asInstanceOf(iterable(BlockPos.class))
            .containsExactly(forward, upward);
      }

      @Test
      @DisplayName("追加で壊す砂利を耐久対象として渡す")
      void passesAdditionalGravelAsDurabilityBlocks() {
        assertThat(delayedBreakArguments.get()).hasSize(4);
        assertThat(delayedBreakArguments.get().get(3))
            .asInstanceOf(iterable(BlockPos.class))
            .containsExactly(forward, upward);
      }
    }

    @Nested
    @DisplayName("土を追加で壊せる場合")
    class BreakableDirt {
      @Test
      @DisplayName("遅延破壊を返す")
      void returnsDelayedBreak() {
        var level = new FakeLevel();
        var player =
            new FakePlayer()
                .setLevel(level.serverLevel())
                .setDirection(Direction.NORTH)
                .setBlockPosition(new BlockPos(0, 10, 0));
        var brokePos = new BlockPos(0, 10, 0);
        level.setHeight(0, 0, 40);
        level.setBlock(brokePos, Blocks.DIRT.defaultBlockState());
        level.setBlock(brokePos.north(), Blocks.DIRT.defaultBlockState());

        try (var mockedDelayedBreaks = mockConstruction(DelayedBreak.class)) {
          var result =
              new ObstructiveBlockBreak(level.breakEvent(brokePos, player.serverPlayer())).call();

          assertThat(result).contains(mockedDelayedBreaks.constructed().getFirst());
        }
      }
    }
  }
}
