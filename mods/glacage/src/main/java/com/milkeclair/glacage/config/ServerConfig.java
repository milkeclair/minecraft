package com.milkeclair.glacage.config;

import com.milkeclair.glacage.config.feature.Flag;
import com.milkeclair.glacage.config.feature.Group;
import java.util.HashMap;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/* サーバー側のMod設定。 */
public class ServerConfig {
  /* Neoforgeの設定。 */
  public static final ModConfigSpec SPEC;

  private static final HashMap<Flag, ModConfigSpec.BooleanValue> ENABLED_VALUES = new HashMap<>();
  private static final ModConfigSpec.EnumValue<Priority> PRIORITY_VALUE;

  static {
    var builder = new ModConfigSpec.Builder();

    builder
        .comment("Server config of glacage features")
        .translation("glacage.configuration.features")
        .push("features");

    PRIORITY_VALUE =
        builder
            .comment("Choose whether client or server feature settings take priority.")
            .translation("glacage.configuration.features.priority")
            .defineEnum("priority", Priority.CLIENT);

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
   * Modの設定にSpecを登録する。
   */
  public static void register(ModContainer container) {
    container.registerConfig(ModConfig.Type.SERVER, SPEC);
  }

  /* サーバーかクライアントか、どちらの設定を優先するか。 */
  public static Priority priority() {
    if (!SPEC.isLoaded()) {
      return Priority.CLIENT;
    }

    return PRIORITY_VALUE.get();
  }

  /* 優先度の設定。 */
  public static void setPriority(Priority priority) {
    PRIORITY_VALUE.set(priority);
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
}
