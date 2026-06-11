package com.milkeclair.glacage.config;

import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.Group;
import java.util.HashMap;
import java.util.function.Consumer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/* クライアント側のMod設定。 */
public class ClientConfig {
  // Neoforgeの設定。
  public static final ModConfigSpec SPEC;

  private static final HashMap<Flag, ModConfigSpec.BooleanValue> ENABLED_VALUES = new HashMap<>();
  private static Consumer<Flag> syncer = flag -> {};

  static {
    var builder = new ModConfigSpec.Builder();

    builder
        .comment("Client config of glacage features")
        .translation("glacage.configuration.features")
        .push("features");

    for (var group : Group.values()) {
      var groupFlag = Flag.fromGroup(group);
      builder.comment(groupFlag.comment()).translation(group.translationKey()).push(group.key());

      for (var flag : Flag.forGroup(group)) {
        ENABLED_VALUES.put(
            flag,
            builder
                .comment(flag.comment())
                .translation(flag.translationKey())
                .define(flag.configKey(), flag.defaultEnabled()));
      }

      builder.pop();
    }
    // featuresから抜ける。
    builder.pop();

    SPEC = builder.build();
  }

  /*
   * バスへの登録。
   * Specはクライアント側の設定として追加する。
   */
  public static void register(ModContainer container, IEventBus modEventBus) {
    container.registerConfig(ModConfig.Type.CLIENT, SPEC);
    modEventBus.addListener(ModConfigEvent.Loading.class, ClientConfig::load);
    modEventBus.addListener(ModConfigEvent.Reloading.class, ClientConfig::load);
  }

  /* 有効かどうかの判定。 */
  public static boolean enabled(Flag flag) {
    var value = ENABLED_VALUES.get(flag);
    if (!SPEC.isLoaded()) {
      return value.getDefault();
    }

    return value.getAsBoolean();
  }

  /* 有効、無効の切り替え。 */
  public static void setEnabled(Flag flag, boolean enabled) {
    ENABLED_VALUES.get(flag).set(enabled);
  }

  /* 設定の同期関数。 */
  public static void setSyncer(Consumer<Flag> syncer) {
    ClientConfig.syncer = syncer;
  }

  /* 同期。syncerにflagをyieldする。 */
  public static void sync(Flag flag) {
    syncer.accept(flag);
  }

  /* 全フラグの同期。 */
  public static void syncAll() {
    for (var flag : Flag.values()) {
      sync(flag);
    }
  }

  private static void load(ModConfigEvent event) {
    if (event.getConfig().getSpec() != SPEC) {
      return;
    }

    syncAll();
  }
}
