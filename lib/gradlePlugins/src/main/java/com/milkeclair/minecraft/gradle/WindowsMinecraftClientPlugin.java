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

    extension
        .getRuntimeModUrl()
        .convention(project.getProviders().gradleProperty("kotlin_for_forge_runtime_mod_url"));
    extension
        .getRuntimeModFileName()
        .convention(
            project
                .getProviders()
                .gradleProperty("kotlin_for_forge_version")
                .map(version -> "kotlinforforge-" + version + "-all.jar")
                .orElse("kotlinforforge-runtime.jar"));

    project
        .getPluginManager()
        .withPlugin(
            "java",
            _plugin ->
                extension
                    .getModJar()
                    .convention(
                        project.getTasks().named("jar", Jar.class).flatMap(Jar::getArchiveFile)));

    var runtimeMod =
        project
            .getLayout()
            .getBuildDirectory()
            .file(extension.getRuntimeModFileName().map(fileName -> "runtime-mods/" + fileName));

    var downloadRuntimeMod =
        project
            .getTasks()
            .register(
                "downloadKotlinForForgeRuntimeMod",
                DownloadFileTask.class,
                task -> {
                  task.getSourceUrl().set(extension.getRuntimeModUrl());
                  task.getOutputFile().set(runtimeMod);
                });

    var copyToWindowsMods =
        project
            .getTasks()
            .register(
                "copyToWindowsMinecraftMods",
                InstallWindowsMinecraftModsTask.class,
                task -> {
                  task.dependsOn("jar", downloadRuntimeMod);
                  task.getModJar().set(extension.getModJar());
                  task.getRuntimeMod()
                      .set(downloadRuntimeMod.flatMap(DownloadFileTask::getOutputFile));
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
