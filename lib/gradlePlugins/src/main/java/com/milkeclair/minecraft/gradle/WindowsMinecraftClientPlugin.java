package com.milkeclair.minecraft.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.jvm.tasks.Jar;

public class WindowsMinecraftClientPlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    var extension =
        project
            .getExtensions()
            .create("windowsMinecraftClient", WindowsMinecraftClientExtension.class);

    project
        .getPluginManager()
        .withPlugin(
            "java",
            _plugin ->
                extension
                    .getModJar()
                    .convention(
                        project.getTasks().named("jar", Jar.class).flatMap(Jar::getArchiveFile)));

    var copyToWindowsMods =
        project
            .getTasks()
            .register(
                "copyToWindowsMinecraftMods",
                InstallWindowsMinecraftModsTask.class,
                task -> {
                  task.dependsOn("jar");
                  task.getModJar().set(extension.getModJar());
                });

    var launchWindowsLauncher =
        project
            .getTasks()
            .register(
                "launchWindowsMinecraftLauncher",
                LaunchWindowsMinecraftLauncherTask.class,
                task -> {
                  task.dependsOn(copyToWindowsMods);
                });

    project.getTasks().register("runWindowsClient", task -> task.dependsOn(launchWindowsLauncher));
  }
}
