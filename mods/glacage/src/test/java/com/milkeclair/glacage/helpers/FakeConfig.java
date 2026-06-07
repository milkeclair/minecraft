package com.milkeclair.glacage.helpers;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.milkeclair.glacage.Config;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

public class FakeConfig {
  public static void load() {
    var config = CommentedConfig.inMemory();
    Config.SPEC.correct(config);
    Config.SPEC.acceptConfig(loadedConfig(config));
  }

  private static IConfigSpec.ILoadedConfig loadedConfig(CommentedConfig config) {
    try {
      var loadedConfig = Class.forName("net.neoforged.fml.config.LoadedConfig");
      var constructor =
          loadedConfig.getDeclaredConstructor(CommentedConfig.class, Path.class, ModConfig.class);
      constructor.setAccessible(true);

      return (IConfigSpec.ILoadedConfig) constructor.newInstance(config, null, null);
    } catch (ClassNotFoundException
        | NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException exception) {
      throw new IllegalStateException(exception);
    }
  }
}
