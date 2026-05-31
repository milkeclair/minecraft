package com.milkeclair.minecraft.gradle;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

public abstract class LaunchWindowsMinecraftLauncherTask extends DefaultTask {
  private final ExecOperations execOperations;

  @Inject
  public LaunchWindowsMinecraftLauncherTask(ExecOperations execOperations) {
    this.execOperations = execOperations;
  }

  @TaskAction
  public void launch() {
    var launcherAppId = launcherAppId();
    var target = "shell:AppsFolder\\" + launcherAppId;
    execOperations.exec(
        spec -> {
          spec.setExecutable("powershell.exe");
          spec.args(
              "-NoProfile",
              "-ExecutionPolicy",
              "Bypass",
              "-Command",
              "Start-Process " + toPowerShellSingleQuoted(target));
        });
  }

  private String launcherAppId() {
    var output = new ByteArrayOutputStream();
    execOperations.exec(
        spec -> {
          spec.setExecutable("powershell.exe");
          spec.args("-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", launcherAppIdCommand());
          spec.setStandardOutput(output);
        });

    var launcherAppId = output.toString(StandardCharsets.UTF_8).trim();
    if (launcherAppId.isEmpty()) {
      throw new GradleException("Minecraft Launcher was not found in Windows Start apps.");
    }

    return launcherAppId.lines().findFirst().orElseThrow();
  }

  private String launcherAppIdCommand() {
    return """
        $apps = Get-StartApps | Where-Object { $_.Name -like '*Minecraft*' }
        $app = $apps | Where-Object { $_.Name -eq 'Minecraft Launcher' } | Select-Object -First 1
        if ($null -ne $app) {
          $app.AppID
        }
        """;
  }

  private String toPowerShellSingleQuoted(String value) {
    return "'" + value.replace("'", "''") + "'";
  }
}
