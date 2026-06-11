package com.milkeclair.glacage.helpers;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.milkeclair.glacage.config.ClientConfig;
import com.milkeclair.glacage.config.ServerConfig;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class FakeConfig {
  public static void load() {
    loadClient();
    loadServer();
  }

  public static void loadClient() {
    load(ClientConfig.SPEC);
  }

  public static void loadServer() {
    load(ServerConfig.SPEC);
  }

  private static void load(ModConfigSpec spec) {
    var config = CommentedConfig.inMemory();
    spec.correct(config);
    spec.acceptConfig(loadedConfig(config));
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
