package com.milkeclair.glacage.usecases.lumberjack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;
import static org.mockito.Mockito.mockConstruction;

import com.milkeclair.glacage.actions.DelayedBreak;
import com.milkeclair.glacage.helpers.FakeLevel;
import com.milkeclair.glacage.helpers.FakePlayer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("Chop")
@ExtendWith(EphemeralTestServerProvider.class)
class ChopTest {
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
        var player = new FakePlayer();
        var brokeLogPos = new BlockPos(0, 0, 0);
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());
        var event = level.breakEvent(brokeLogPos, player.serverPlayer());
        event.setCanceled(true);

        assertThat(new Chop(event).call()).isEmpty();
      }
    }

    @Nested
    @DisplayName("プレイヤーがサーバープレイヤーではない場合")
    class NotServerPlayer {
      @Test
      @DisplayName("処理を開始しない")
      void doesNotReadBlockState() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());
        var event = level.breakEvent(brokeLogPos, (Player) null);

        assertThat(new Chop(event).call()).isEmpty();
      }
    }

    @Nested
    @DisplayName("斧を持っていない場合")
    class NotHoldingAxe {
      @Test
      @DisplayName("処理を開始しない")
      void doesNotStart() {
        var level = new FakeLevel();
        var player = new FakePlayer().setMainHandItem(new ItemStack(Items.STICK));
        var brokeLogPos = new BlockPos(0, 0, 0);
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());

        assertThat(new Chop(level.breakEvent(brokeLogPos, player.serverPlayer())).call()).isEmpty();
      }
    }

    @Nested
    @DisplayName("壊したブロックが原木ではない場合")
    class NotLog {
      @Test
      @DisplayName("処理を開始しない")
      void doesNotSearchWorld() {
        var level = new FakeLevel();
        var player = new FakePlayer().setMainHandItem(new ItemStack(Items.WOODEN_AXE));
        var brokeLogPos = new BlockPos(0, 0, 0);
        level.setBlock(brokeLogPos, Blocks.STONE.defaultBlockState());
        var event = level.breakEvent(brokeLogPos, player.serverPlayer());

        assertThat(new Chop(event).call()).isEmpty();
      }
    }

    @Nested
    @DisplayName("自然な葉が足りない場合")
    class NotEnoughNaturalLeaves {
      @Test
      @DisplayName("遅延破壊を作成しない")
      void doesNotCreateDelayedBreak() {
        var level = new FakeLevel();
        var player = new FakePlayer().setMainHandItem(new ItemStack(Items.WOODEN_AXE));
        var brokeLogPos = new BlockPos(0, 0, 0);
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(brokeLogPos.above(), Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(brokeLogPos.above().above(), naturalLeaf(1));
        player.setLevel(level.serverLevel());

        assertThat(new Chop(level.breakEvent(brokeLogPos, player.serverPlayer())).call()).isEmpty();
      }
    }

    @Nested
    @DisplayName("木として扱える場合")
    class ChoppableTree {
      FakeLevel level;
      FakePlayer player;
      BlockPos brokeLogPos;
      BlockPos upperLog;
      List<BlockPos> leafPositions;
      AtomicReference<List<?>> delayedBreakArguments;
      List<DelayedBreak> delayedBreaks;
      Optional<DelayedBreak> result;

      @BeforeEach
      void call() {
        level = new FakeLevel();
        player = new FakePlayer().setMainHandItem(new ItemStack(Items.WOODEN_AXE));
        brokeLogPos = new BlockPos(0, 0, 0);
        upperLog = brokeLogPos.above();
        leafPositions =
            List.of(
                brokeLogPos.west(),
                brokeLogPos.east(),
                brokeLogPos.north(),
                brokeLogPos.south(),
                upperLog.above(),
                upperLog.east());
        delayedBreakArguments = new AtomicReference<>();
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(upperLog, Blocks.OAK_LOG.defaultBlockState());
        for (var leafPos : leafPositions) {
          level.setBlock(leafPos, naturalLeaf(1));
        }
        player.setLevel(level.serverLevel());

        try (var mockedDelayedBreaks =
            mockConstruction(
                DelayedBreak.class,
                (mock, context) -> delayedBreakArguments.set(context.arguments()))) {
          result = new Chop(level.breakEvent(brokeLogPos, player.serverPlayer())).call();
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
      @DisplayName("壊した原木を除いた原木と葉を遅延破壊に渡す")
      void passesLogsAndLeavesExceptBrokeLogToDelayedBreak() {
        assertThat(delayedBreakArguments.get()).hasSize(4);
        assertThat(delayedBreakArguments.get().get(2))
            .asInstanceOf(iterable(BlockPos.class))
            .startsWith(upperLog)
            .doesNotContain(brokeLogPos)
            .containsExactlyInAnyOrder(
                upperLog,
                leafPositions.get(0),
                leafPositions.get(1),
                leafPositions.get(2),
                leafPositions.get(3),
                leafPositions.get(4),
                leafPositions.get(5));
      }

      @Test
      @DisplayName("追加で壊す原木だけを耐久対象として渡す")
      void passesOnlyAdditionalLogsAsDurabilityBlocks() {
        assertThat(delayedBreakArguments.get()).hasSize(4);
        assertThat(delayedBreakArguments.get().get(3))
            .asInstanceOf(iterable(BlockPos.class))
            .containsExactly(upperLog);
      }
    }
  }

  private static BlockState naturalLeaf(int distance) {
    return Blocks.OAK_LEAVES
        .defaultBlockState()
        .setValue(LeavesBlock.PERSISTENT, false)
        .setValue(LeavesBlock.DISTANCE, distance);
  }
}
