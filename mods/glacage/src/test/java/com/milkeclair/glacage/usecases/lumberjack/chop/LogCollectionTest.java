package com.milkeclair.glacage.usecases.lumberjack.chop;

import static org.assertj.core.api.Assertions.assertThat;

import com.milkeclair.glacage.helpers.FakeLevel;
import com.milkeclair.glacage.usecases.lumberjack.Lumberjack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@DisplayName("LogCollection")
@ExtendWith(EphemeralTestServerProvider.class)
class LogCollectionTest {
  @BeforeAll
  static void boot(MinecraftServer server) {}

  @Nested
  @DisplayName("#call")
  class Call {
    @Nested
    @DisplayName("周囲に同じ原木がない場合")
    class OnlyBrokeLog {
      @Test
      @DisplayName("壊された原木だけを返す")
      void returnsOnlyBrokeLog() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());

        var logs = new LogCollection(level.serverLevel(), brokeLogPos, Blocks.OAK_LOG).call();

        assertThat(logs).containsExactly(brokeLogPos);
      }
    }

    @Nested
    @DisplayName("壊された原木につながる同じ原木がある場合")
    class ConnectedLogs {
      @Test
      @DisplayName("つながっている原木だけを返す")
      void returnsConnectedLogs() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var upperLog = brokeLogPos.above();
        var sideLog = brokeLogPos.east();
        var disconnectedLog = new BlockPos(0, 3, 0);
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(upperLog, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(sideLog, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(disconnectedLog, Blocks.OAK_LOG.defaultBlockState());

        var logs = new LogCollection(level.serverLevel(), brokeLogPos, Blocks.OAK_LOG).call();

        assertThat(logs).containsExactlyInAnyOrder(brokeLogPos, upperLog, sideLog);
      }
    }

    @Nested
    @DisplayName("違う原木がつながっている場合")
    class DifferentLog {
      @Test
      @DisplayName("違う原木は返さない")
      void doesNotReturnDifferentLog() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var birchLog = brokeLogPos.above();
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(birchLog, Blocks.BIRCH_LOG.defaultBlockState());

        var logs = new LogCollection(level.serverLevel(), brokeLogPos, Blocks.OAK_LOG).call();

        assertThat(logs).containsExactly(brokeLogPos);
      }
    }

    @Nested
    @DisplayName("探索範囲外に同じ原木がある場合")
    class OutsideSearchArea {
      @Test
      @DisplayName("探索範囲外の原木は返さない")
      void doesNotReturnOutsideLogs() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        var belowLog = brokeLogPos.below();
        var outsideLog = new BlockPos(Lumberjack.MAX_HORIZONTAL_RADIUS + 1, 0, 0);
        level.setBlock(brokeLogPos, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(belowLog, Blocks.OAK_LOG.defaultBlockState());
        level.setBlock(outsideLog, Blocks.OAK_LOG.defaultBlockState());

        var logs = new LogCollection(level.serverLevel(), brokeLogPos, Blocks.OAK_LOG).call();

        assertThat(logs).containsExactly(brokeLogPos);
      }
    }

    @Nested
    @DisplayName("原木が多すぎる場合")
    class TooManyLogs {
      @Test
      @DisplayName("空のSetを返す")
      void returnsEmptySet() {
        var level = new FakeLevel();
        var brokeLogPos = new BlockPos(0, 0, 0);
        // 9 * 16(144)
        for (var x = 0; x <= Lumberjack.MAX_HORIZONTAL_RADIUS; x++) {
          for (var y = 0; y < 16; y++) {
            level.setBlock(new BlockPos(x, y, 0), Blocks.OAK_LOG.defaultBlockState());
          }
        }

        var logs = new LogCollection(level.serverLevel(), brokeLogPos, Blocks.OAK_LOG).call();

        assertThat(logs).isEmpty();
      }
    }
  }
}
