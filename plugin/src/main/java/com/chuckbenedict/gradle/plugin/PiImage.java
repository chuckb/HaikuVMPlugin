package com.chuckbenedict.gradle.plugin;

import com.chuckbenedict.gradle.plugin.tasks.BuildPiImage;
import com.chuckbenedict.gradle.plugin.tasks.GetPiImageFiles;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class PiImage implements Plugin<Project> {
  @Override
  public void apply(final Project project) {
    // Add a task to get the necessary files to build a minimal Pi boot image
    Task getPiImageFiles = project.getTasks().create("getPiImageFiles", GetPiImageFiles.class, new Action<GetPiImageFiles>() {
      public void execute(GetPiImageFiles task) {
      }
    });
    project.getTasks().add(getPiImageFiles);

    // Add a task to build a Pi SD card image file to boot a serial boot loader
    Task buildPiImage = project.getTasks().create("buildPiImage", BuildPiImage.class, new Action<BuildPiImage>() {
      public void execute(BuildPiImage task) {
        task.image.set(project.file("build/firmware/pi/boot.img"));
        task.imageRoot.set(project.file("build/firmware/pi/files"));
        task.dependsOn("getPiImageFiles");
      }
    });
    project.getTasks().add(buildPiImage);
  }
}