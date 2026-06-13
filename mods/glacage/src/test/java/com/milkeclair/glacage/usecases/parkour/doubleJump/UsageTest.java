package com.milkeclair.glacage.usecases.parkour.doubleJump;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import net.minecraft.world.entity.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Usage")
class UsageTest {
  @Nested
  @DisplayName("#available")
  class Available {
    @Nested
    @DisplayName("まだ二段ジャンプしていない場合")
    class Unused {
      @Test
      @DisplayName("trueを返す")
      void returnsTrue() {
        var player = mock(Player.class);
        var usage = new Usage();

        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        assertThat(usage.available(player)).isTrue();
      }
    }

    @Nested
    @DisplayName("既に二段ジャンプしている場合")
    class Used {
      @Test
      @DisplayName("falseを返す")
      void returnsFalse() {
        var player = mock(Player.class);
        var usage = new Usage();

        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        usage.use(player);

        assertThat(usage.available(player)).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("#reset")
  class Reset {
    @Nested
    @DisplayName("使用済みの場合")
    class Used {
      @Test
      @DisplayName("再度使える状態にする")
      void makesAvailable() {
        var player = mock(Player.class);
        var usage = new Usage();

        when(player.getUUID()).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        usage.use(player);
        usage.reset(player);

        assertThat(usage.available(player)).isTrue();
      }
    }
  }
}
