package com.milkeclair.minecraft.gradle;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public abstract class DownloadFileTask extends DefaultTask {
  @Input
  public abstract Property<String> getSourceUrl();

  @OutputFile
  public abstract RegularFileProperty getOutputFile();

  @TaskAction
  public void download() throws Exception {
    var file = getOutputFile().get().getAsFile();
    file.getParentFile().mkdirs();

    try (var input = URI.create(getSourceUrl().get()).toURL().openStream()) {
      Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
  }
}
