package com.chuckbenedict.gradle.plugin.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.chuckbenedict.gradle.plugin.internal.GradleUtil;

import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.OutputDirectories;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

/**
 * Gradle task which implements calling the haikulink process.
 * This process converts compiled Java class files into a runnable
 * Java VM by embedding the class files into C structs.
 * See http://haiku-vm.sourceforge.net/
 */
public class Haikulink extends JavaExec {
  private final Property<String> haikuVMDir;
  private final Property<String> mainClass;
  private final Property<String> haikuVMConfig;

  public Haikulink() {
    // Initialize member variables
    haikuVMDir = getProject().getObjects().property(String.class);
    mainClass = getProject().getObjects().property(String.class);
    haikuVMConfig = getProject().getObjects().property(String.class);

    // Run this class
    setMain("haikuvm.pc.tools.HaikuVM");
    // Set what haikulink does for us
    setDescription("Code generate embedded JVM, system, and user classes into a C project.");
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

  /**
   * Java file containing the static main function entry point for the application.
   * @return  The relative (to the default source set) file name string of static main function
   *          as a Gradle property string. 
   */
  public Property<String> getMainClassProvider() {
    return mainClass;
  }

  /**
   * The name of the Haiku confiugration (contained with the HaikuVM.properties file)
   * that haikuvmlink will use to code generate the VM. This file can be within the root
   * of the project. If not, the file contained within the HaikuVM directory will be used.
   * The profile name defined within this property must exist within the properties file.
   * See http://haiku-vm.sourceforge.net/.
   * @return  The profile name as a Gradle property string.
   */
  public Property<String> getHaikuVMConfigProvider() {
    return haikuVMConfig;
  }

  @Input
  public String getHaikuVMDir() {
    return haikuVMDir.get(); 
  }

  @Input
  public String getMainClass() {
    return mainClass.get();
  }

  @Input
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

  public void setHaikuVMDir(Property<String> haikuVMDir) {
    this.haikuVMDir.set(haikuVMDir);
  }

  public void setMainClass(Property<String> mainClass) {
    this.mainClass.set(mainClass);
  }

  public void setHaikuVMConfig(Property<String> haikuVMConfig) {
    this.haikuVMConfig.set(haikuVMConfig);
  }

  @InputFiles
  public FileCollection getInputFiles() {
    // The project, configured as a java plugin, has a sourceSet named main.
    final SourceSet main = ((SourceSetContainer) getProject().getProperties().get("sourceSets")).getByName("main");
    // The main sourceSet has an output collection. This colledtion forms the input
    // Java classes that haikulink will consume for generation into c structs.
    return main.getOutput().getClassesDirs();
  }

  @OutputDirectories
  // TODO: Make into overridable constant settings.
  public FileCollection getOutputDirectories() {
    return getProject().files("build/HaikuVM", "build/target");
  }

  @Override
	public void exec() {
    // Wire up any args already set with args that we need to make
    // haikulink run successfully.
    setArgs(getCompleteArgs());

    // Wire up args for the JVM
    setJvmArgs(getCompleteJvmArgs());

    // Wire up the classpath
    setClasspath(getCompleteClasspath());

    // Call the JavaExec exec method
    super.exec();
  }
  
	protected List<String> getCompleteArgs() {
		List<String> args = new ArrayList<>(getArgs());

    args.add("--bootclasspath");
    args.add(getCompleteBootstrapClasspath());
    args.add("--Config");
    args.add(getHaikuVMConfig());
    args.add("haikulink");
    args.add("-d");
    args.add(getProject().file("build").getAbsolutePath());
    args.add("-v");
    args.add(GradleUtil.toFile(getProject(), getMainClass()).getAbsolutePath());
  
		return args;
  }

  protected String getCompleteBootstrapClasspath() {
    FileCollection collection =  
      getInputFiles()
      .plus(getProject().getConfigurations().getByName("compile"))
      .plus(getProject().getConfigurations().getByName("haikutools"))
      .plus(getProject().files(new File(getHaikuVMDir() + "/lib/nxt/classes.jar")));
    String bcp = "";
    for (File file : collection) {
      bcp += file + ";";
    }
    return bcp;
  }

  protected FileCollection getCompleteClasspath() {
    ConfigurableFileTree cft = getProject().fileTree(getHaikuVMDir() + "/lib/pc");
    cft.include("**/*.jar");
    return getClasspath()
      .plus(cft);
  }

  protected List<String> getCompleteJvmArgs() {
		List<String> args = new ArrayList<>(getJvmArgs());

    args.add("-Dhaikuvm.home=" + getHaikuVMDir());
    args.add("-DCOMMAND_NAME=haikulink");
    
    return args;
  }
}