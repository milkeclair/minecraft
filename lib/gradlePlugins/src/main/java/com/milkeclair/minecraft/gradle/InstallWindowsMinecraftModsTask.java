package com.milkeclair.minecraft.gradle;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

public abstract class InstallWindowsMinecraftModsTask extends DefaultTask {
  private static final Pattern WINDOWS_PATH_PATTERN =
      Pattern.compile("^(?<drive>[a-zA-Z]):[\\\\/](?<path>.*)$");

  private final ExecOperations execOperations;

  @Inject
  public InstallWindowsMinecraftModsTask(ExecOperations execOperations) {
    this.execOperations = execOperations;
  }

  @InputFile
  public abstract RegularFileProperty getModJar();

  @InputFile
  public abstract RegularFileProperty getRuntimeMod();

  @TaskAction
  public void install() throws Exception {
    var destination = Path.of(toWslPath(windowsRoamingAppData() + "\\.minecraft\\mods"));
    Files.createDirectories(destination);

    copyTo(destination, getModJar().get().getAsFile().toPath());
    copyTo(destination, getRuntimeMod().get().getAsFile().toPath());
  }

  private void copyTo(Path destination, Path source) throws Exception {
    Files.copy(
        source, destination.resolve(source.getFileName()), StandardCopyOption.REPLACE_EXISTING);
  }

  private String windowsRoamingAppData() {
    var output = new ByteArrayOutputStream();
    execOperations.exec(
        spec -> {
          spec.setExecutable("powershell.exe");
          spec.args("-NoProfile", "-Command", "[Environment]::GetFolderPath('ApplicationData')");
          spec.setStandardOutput(output);
        });

    return output.toString(StandardCharsets.UTF_8).trim();
  }

  private String toWslPath(String path) {
    var matcher = WINDOWS_PATH_PATTERN.matcher(path);
    if (!matcher.matches()) {
      return path;
    }

    var drive = matcher.group("drive").toLowerCase();
    var rest = matcher.group("path").replace('\\', '/');
    return "/mnt/" + drive + "/" + rest;
  }
}
