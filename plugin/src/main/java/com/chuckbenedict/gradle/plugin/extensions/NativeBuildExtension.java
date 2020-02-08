package com.chuckbenedict.gradle.plugin.extensions;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.provider.Property;

public class NativeBuildExtension {
  private final Property<File> projectDir;
  private final Property<Task> haikulinkTask;

  public NativeBuildExtension(Project project) {
    this.projectDir = project.getObjects().property(File.class);
    this.haikulinkTask = project.getObjects().property(Task.class);
  }

  public Property<File> getProjectDirProvider() {
      return projectDir;
  }

  public Property<Task> getHaikulinkTaskProvider() {
    return haikulinkTask;
  }

  public File getProjectDir() {
    return projectDir.get(); 
  }

  public Task getHaikulinkTask() {
    return haikulinkTask.get();
  }

  public void setProjectDir(File projectDir) {
    this.projectDir.set(projectDir);
  }

  public void setHaikulinkTask(Task haikulinkTask) {
    this.haikulinkTask.set(haikulinkTask);
  }
}