package com.chuckbenedict.gradle.plugin.extensions;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

/**
 * This class extension defines the properties that are settable
 * for a HaikuVM project.
 */
public class HaikuVMExtension {
  private final Property<String> haikuVMDir;
  private final Property<String> mainClass;
  private final Property<String> haikuVMConfig;

  public HaikuVMExtension(Project project) {
    this.haikuVMDir = project.getObjects().property(String.class);
    this.mainClass = project.getObjects().property(String.class);
    this.haikuVMConfig = project.getObjects().property(String.class);
  }

  public Property<String> getHaikuVMDirProvider() {
      return haikuVMDir;
  }

  public Property<String> getMainClassProvider() {
    return mainClass;
  }

  public Property<String> getHaikuVMConfigProvider() {
    return haikuVMConfig;
  }

  public String getHaikuVMDir() {
    return haikuVMDir.get(); 
  }

  public String getMainClass() {
    return mainClass.get();
  }

  public String getHaikuVMConfig() {
    return haikuVMConfig.get();
  }

  public void setHaikuVMDir(String haikuVMDir) {
    this.haikuVMDir.set(haikuVMDir);
  }

  public void setMainClass(String mainClass) {
    this.mainClass.set(mainClass);
  }

  public void setHaikuVMConfig(String haikuVMConfig) {
    this.haikuVMConfig.set(haikuVMConfig);
  }
}