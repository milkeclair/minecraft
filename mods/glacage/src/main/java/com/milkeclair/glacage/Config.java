package com.milkeclair.glacage;

import com.milkeclair.glacage.config.Feature;
import java.util.EnumMap;
import java.util.function.Consumer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/* Modの設定。 */
public class Config {
  // Neoforgeの設定。
  public static final ModConfigSpec SPEC;

  private static final EnumMap<Feature, ModConfigSpec.BooleanValue> ENABLED_VALUES =
      new EnumMap<>(Feature.class);
  private static Consumer<Feature> syncer = feature -> {};

  static {
    var builder = new ModConfigSpec.Builder();

    builder
        .comment("Config of glacage features")
        .translation("glacage.configuration.features")
        .push("features");

    for (var feature : Feature.values()) {
      ENABLED_VALUES.put(
          feature,
          builder
              .comment(feature.comment())
              .translation(feature.translationKey())
              .define(feature.key(), feature.defaultEnabled()));
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
    modEventBus.addListener(ModConfigEvent.Loading.class, Config::load);
    modEventBus.addListener(ModConfigEvent.Reloading.class, Config::load);
  }

  /* 有効かどうかの判定。 */
  public static boolean enabled(Feature feature) {
    var value = ENABLED_VALUES.get(feature);
    if (!SPEC.isLoaded()) {
      return value.getDefault();
    }

    return value.getAsBoolean();
  }

  /* 有効、無効の切り替え。 */
  public static void setEnabled(Feature feature, boolean enabled) {
    ENABLED_VALUES.get(feature).set(enabled);
  }

  /* 設定の同期関数。 */
  public static void setSyncer(Consumer<Feature> syncer) {
    Config.syncer = syncer;
  }

  /* 同期。syncerにfeatureをyieldする。 */
  public static void sync(Feature feature) {
    syncer.accept(feature);
  }

  /* 全設定の同期。 */
  public static void syncAll() {
    for (var feature : Feature.values()) {
      sync(feature);
    }
  }

  private static void load(ModConfigEvent event) {
    if (event.getConfig().getSpec() != SPEC) {
      return;
    }

    syncAll();
  }
}
