package com.chuckbenedict.gradle.plugin.tasks;

import java.io.IOException;

import javax.inject.Inject;

import com.chuckbenedict.gradle.plugin.internal.ImageBuilder;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle task which builds an SD card image containing GPU/ARM
 * startup and serial bootloader.
 */
public class BuildPiImage extends DefaultTask {
  private final ObjectFactory objectFactory;

  @OutputFile
  public final RegularFileProperty image;

  @InputDirectory
  public final RegularFileProperty imageRoot;

  @Inject
  public BuildPiImage(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
    image = objectFactory.fileProperty();
    imageRoot = objectFactory.fileProperty();
    this.setDescription("Create a Raspberry Pi boot image file.");
  }

  @TaskAction
  void build() throws IOException {
    // Build the image file
    ImageBuilder.of(imageRoot.getAsFile().get()).createDiskImage(image.getAsFile().get());
  }
}
