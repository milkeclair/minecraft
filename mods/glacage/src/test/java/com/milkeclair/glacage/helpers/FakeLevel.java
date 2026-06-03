package com.milkeclair.glacage.helpers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockEvent;

public class FakeLevel {
  private final HashMap<BlockPos, BlockState> blocks = new HashMap<>();
  private final ServerLevel serverLevel = mock(ServerLevel.class);

  public FakeLevel() {
    when(serverLevel.getBlockState(any(BlockPos.class)))
        .thenAnswer(
            invocation ->
                blocks.getOrDefault(invocation.getArgument(0), Blocks.AIR.defaultBlockState()));
    when(serverLevel.setBlock(any(BlockPos.class), any(BlockState.class), anyInt()))
        .thenAnswer(
            invocation -> {
              blocks.put(invocation.getArgument(0), invocation.getArgument(1));

              return true;
            });
  }

  public ServerLevel serverLevel() {
    return serverLevel;
  }

  public FakeLevel setBlock(BlockPos pos, BlockState state) {
    serverLevel.setBlock(pos, state, 0);

    return this;
  }

  public BlockEvent.BreakEvent breakEvent(BlockPos pos, Player player) {
    return new BlockEvent.BreakEvent(serverLevel, pos, serverLevel.getBlockState(pos), player);
  }
}
