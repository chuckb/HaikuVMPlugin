package com.chuckbenedict.gradle.plugin.extensions;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Property;

/**
 * This class extension defines the properties that are settable
 * for a HaikuVM project.
 */
public class HaikuVMExtension {
  private final Property<Directory> haikuVMDir;
  private final Property<String> mainClass;
  private final Property<String> haikuVMConfig;
  private final Property<String> port;

  public HaikuVMExtension(Project project) {
    this.haikuVMDir = project.getObjects().property(Directory.class);
    this.haikuVMDir.convention(project.getLayout().getBuildDirectory().dir("haikuVMSDK"));
    this.mainClass = project.getObjects().property(String.class);
    this.mainClass.convention("Main.java");
    this.haikuVMConfig = project.getObjects().property(String.class);
    this.port = project.getObjects().property(String.class);
  }

  public Property<Directory> getHaikuVMDirProvider() {
      return haikuVMDir;
  }

  public Property<String> getMainClassProvider() {
    return mainClass;
  }

  public Property<String> getHaikuVMConfigProvider() {
    return haikuVMConfig;
  }

  public Property<String> getPortProvider() {
    return port;
  }

  public Directory getHaikuVMDir() {
    return haikuVMDir.get(); 
  }

  public String getMainClass() {
    return mainClass.get();
  }

  public String getHaikuVMConfig() {
    return haikuVMConfig.get();
  }

  public String getPort() {
    return port.get();
  }

  public void setHaikuVMDir(Directory haikuVMDir) {
    this.haikuVMDir.set(haikuVMDir);
  }

  public void setMainClass(String mainClass) {
    this.mainClass.set(mainClass);
  }

  public void setHaikuVMConfig(String haikuVMConfig) {
    this.haikuVMConfig.set(haikuVMConfig);
  }

  public void setPort(String port) {
    this.port.set(port);
  }
}