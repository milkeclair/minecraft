package com.milkeclair.minecraft.gradle;

import org.gradle.api.file.RegularFileProperty;

public interface WindowsMinecraftClientExtension {
  RegularFileProperty getModJar();
}
