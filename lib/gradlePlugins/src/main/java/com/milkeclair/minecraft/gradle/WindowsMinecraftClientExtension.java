package com.milkeclair.minecraft.gradle;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

public interface WindowsMinecraftClientExtension {
  Property<String> getRuntimeModUrl();

  Property<String> getRuntimeModFileName();

  RegularFileProperty getModJar();
}
