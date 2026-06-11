package com.milkeclair.glacage.helpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;

public class FakePlayer {
  private final ServerPlayer serverPlayer = mock(ServerPlayer.class);
  private final ServerPlayerGameMode gameMode = mock(ServerPlayerGameMode.class);

  public FakePlayer() {
    try {
      Field field = ServerPlayer.class.getField("gameMode");
      field.setAccessible(true);
      field.set(serverPlayer, gameMode);
    } catch (ReflectiveOperationException exception) {
      throw new IllegalStateException(exception);
    }
  }

  public ServerPlayer serverPlayer() {
    return serverPlayer;
  }

  public ServerPlayerGameMode gameMode() {
    return gameMode;
  }

  public FakePlayer setLevel(ServerLevel level) {
    when(serverPlayer.level()).thenReturn(level);

    return this;
  }

  public FakePlayer setMainHandItem(ItemStack itemStack) {
    when(serverPlayer.getMainHandItem()).thenReturn(itemStack);

    return this;
  }

  public FakePlayer setUuid(UUID uuid) {
    when(serverPlayer.getUUID()).thenReturn(uuid);

    return this;
  }

  public FakePlayer setDirection(Direction direction) {
    when(serverPlayer.getDirection()).thenReturn(direction);

    return this;
  }

  public FakePlayer setBlockPosition(BlockPos pos) {
    when(serverPlayer.blockPosition()).thenReturn(pos);

    return this;
  }
}
