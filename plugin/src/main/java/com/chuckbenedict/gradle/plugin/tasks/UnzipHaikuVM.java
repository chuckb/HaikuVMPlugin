package com.chuckbenedict.gradle.plugin.tasks;

import java.io.File;

import com.chuckbenedict.gradle.plugin.internal.GradleUtil;

import org.gradle.api.file.FileTree;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;

/**
 * A task that unzips a local copy of haikutools and the embedded JVM so
 * haiku projects can be built.
 */
public class UnzipHaikuVM extends Copy {
  private final Property<String> haikuVMDir;
  private String configuration;

  /**
   * Constructor that takes the name of the java configuration which contains the
   * jar files of the haiku build.
   * @param configuration   The name of the java configuration.
   */
  public UnzipHaikuVM() {
    haikuVMDir = getProject().getObjects().property(String.class);
    this.configuration = "";

    setDescription("Install local copy of haikutools and embedded JVM.");
  }

  /**
   * Get the local directory which stores the downloaded HaikuVM
   * base files, which are required to generate a HaikuVM instance
   * for any given HaikuVM project.
   * @return  The directory string as a Gradle property.
   */
  public Property<String> getHaikuVMDirProvider() {
    return haikuVMDir;
  }

  @Input
  public String getConfiguration() {
    return configuration;
  }

  @InputFiles
  public FileTree getJars() {
    return getProject().zipTree(GradleUtil.getJar(getProject(), this.configuration, "build"));
  }

  public void setConfiguration(String configuration) {
    this.configuration = configuration;
  }

  @OutputDirectory
  public String getHaikuVMDir() {
    return haikuVMDir.get(); 
  }

  public void setHaikuVMDir(String haikuVMDir) {
    this.haikuVMDir.set(haikuVMDir);
  }

  public void setHaikuVMDir(Property<String> haikuVMDir) {
    this.haikuVMDir.set(haikuVMDir);
  }

  @Override
	public void copy() {
    // TODO: Does the search string of the file need to be factored out? Probably. Names could change.
    from(getJars());
    include("**/*");
    into(new File(getHaikuVMDir()));
    super.copy();
  }
}