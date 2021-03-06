package com.chuckbenedict.gradle.plugin.extensions;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.provider.Property;

public class NativeBuildExtension {
  private final Property<File> projectDir;
  private final Property<Task> haikulinkTask;
  private final Property<Configuration> deploymentConfiguration;
  private final Property<String> port;

  public NativeBuildExtension(Project project) {
    this.projectDir = project.getObjects().property(File.class);
    this.haikulinkTask = project.getObjects().property(Task.class);
    this.deploymentConfiguration = project.getObjects().property(Configuration.class);
    this.port = project.getObjects().property(String.class);
  }

  public Property<File> getProjectDirProvider() {
      return projectDir;
  }

  public Property<Task> getHaikulinkTaskProvider() {
    return haikulinkTask;
  }

  public Property<Configuration> getDeploymentConfigurationProvider() {
    return deploymentConfiguration;
  }

  public Property<String> getPortProvider() {
    return port;
  }

  public File getProjectDir() {
    return projectDir.get(); 
  }

  public Task getHaikulinkTask() {
    return haikulinkTask.get();
  }

  public Configuration getDeploymentConfiguration() {
    return deploymentConfiguration.get();
  }

  public String getPort() {
    return port.get();
  }

  public void setProjectDir(File projectDir) {
    this.projectDir.set(projectDir);
  }

  public void setHaikulinkTask(Task haikulinkTask) {
    this.haikulinkTask.set(haikulinkTask);
  }

  public void setDeploymentConfiguration(Configuration deployment) {
    this.deploymentConfiguration.set(deployment);
  }

  public void setPort(String port) {
    this.port.set(port);
  }

  public void setPort(Property<String> port) {
    this.port.set(port);
  }
}