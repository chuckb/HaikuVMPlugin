package com.chuckbenedict.gradle.plugin.tasks;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import com.chuckbenedict.gradle.plugin.internal.RpiFirmware;

import org.gradle.api.DefaultTask;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileTree;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle task which builds an SD card image containing GPU/ARM
 * startup and serial bootloader.
 */
public class GetPiImageFiles extends DefaultTask {
  private final ObjectFactory objectFactory;
  private Configuration deployment;
  
  @OutputDirectory
  public File getPiImageFilesDirectory() {
    // TODO: Make configurable and get build directory from project layout
    return this.getProject().file("build/firmware/pi/files");
  }

  @OutputFiles
  public FileTree getPiImageFiles() {
    // TODO: Make configurable and get build directory from project layout
    return this.getProject().files("build/firmware/pi/files").getAsFileTree();
  }

  @Inject
  public GetPiImageFiles(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
    this.setDescription("Fetch Raspberry Pi boot image files from remote repo.");
    deployment = this.getProject().getConfigurations().findByName("deployment");
    this.dependsOn(deployment);
  }

  @TaskAction
  /**
   * Get the minimum files required to transfer control to custom built kernel for the Rpi.
   * @throws IOException
   */
  void getFiles() throws IOException {
    // TODO: Parameterize branch and destination locations.
    RpiFirmware.getFile("master", "bootcode.bin", this.getProject().file("build/firmware/pi/files/bootcode.bin"));
    RpiFirmware.getFile("master", "start.elf", this.getProject().file("build/firmware/pi/files/start.elf"));
    // Copy the Pi cross-compiled serial boot loader kernel image to the firmware files
    // directory so that it can eventually be packaged up into an image file for deployment
    // on to an SD card.
    this.getProject()
      .copy(spec -> {
        spec.from(deployment.filter(new Spec<File>() {
          @Override
          public boolean isSatisfiedBy(File file) {
            return file.getName().contains("kernel.img");
          }
        }).getSingleFile()
      );
      spec.into(this.getProject().file("build/firmware/pi/files"));
    });
  }
}
