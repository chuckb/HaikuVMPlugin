package com.chuckbenedict.gradle.plugin;

import java.net.URI;

import com.chuckbenedict.gradle.plugin.extensions.HaikuVMExtension;
import com.chuckbenedict.gradle.plugin.extensions.NativeBuildExtension;
import com.chuckbenedict.gradle.plugin.tasks.Haikulink;
import com.chuckbenedict.gradle.plugin.tasks.UnzipHaikuVM;

import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;

public class HaikuVMPlugin implements Plugin<Project> {
  @Override
  public void apply(final Project project) {
    HaikuVMExtension haikuVMExtension = project.getExtensions().create("haikuvm", HaikuVMExtension.class, project);
    NativeBuildExtension nativeBuildExtension = project.getExtensions().create("nativebuild", NativeBuildExtension.class, project);

    // Apply Java plugin to project
    project.getPlugins().apply(JavaPlugin.class);

    // Set up repositories
    project.getRepositories().add(project.getRepositories().jcenter());
    project.getRepositories().add(project.getRepositories().maven((maven) -> {
      maven.setUrl(URI.create("https://jitpack.io"));
    }));

    // Add and set up the haikutools configuration
    final Configuration haikuToolsConfig = project.getConfigurations().create("haikutools")
      .setVisible(true)
      .setDescription("The haikutools artifacts to be downloaded for this plugin.");

    // Add the haikutools dependency.
    // TODO: Make the version configurable
    haikuToolsConfig.getDependencies().add(project.getDependencies().create("com.github.chuckb:haikuVM:-SNAPSHOT"));

    // Get the compile configuration
    final Configuration compileConfig = project.getConfigurations().getByName("compile");
    // Add the haikuVM Java build dependencies
    // TODO: Make the version configurable
    compileConfig.getDependencies().add(project.getDependencies().create("com.github.chuckb.haikuVM:haikuRT:-SNAPSHOT"));
    compileConfig.getDependencies().add(project.getDependencies().create("com.github.chuckb.haikuVM:bootstrap:-SNAPSHOT"));

    // Create the unzip task to unbundle HaikuVM
    // TODO: Factor out constants
    Task unzipHaikuVM = project.getTasks().create("unzipHaikuVM", UnzipHaikuVM.class, new Action<UnzipHaikuVM>() {
      public void execute(UnzipHaikuVM unzipHaikuVMTask) {
        unzipHaikuVMTask.setConfiguration("haikutools");
        // Set the extension properties on the task from the extension
        unzipHaikuVMTask.setHaikuVMDir(haikuVMExtension.getHaikuVMDirProvider());
      }
    });
    // Add the task to the project
    project.getTasks().add(unzipHaikuVM);
    // Set the Java task compileJava up with a dependency on the unzip HaikuVM task
    // so we can make sure to have all the Jars from the VM we need.
    project.getTasks().getByName("compileJava").dependsOn(unzipHaikuVM);

    // Create the haikulink task. This generates the c code to embed generated
    // Java class bytecode into the HaikuVM binary.
    Task haikulink = project.getTasks().create("haikulink", Haikulink.class, new Action<Haikulink>() {
      public void execute(Haikulink haikulinkTask) {
        // Set the extension properties on the task from the extension
        haikulinkTask.setHaikuVMDir(haikuVMExtension.getHaikuVMDirProvider());
        haikulinkTask.setMainClass(haikuVMExtension.getMainClassProvider());
        haikulinkTask.setHaikuVMConfig(haikuVMExtension.getHaikuVMConfigProvider());
        nativeBuildExtension.setHaikulinkTask(haikulinkTask);
        nativeBuildExtension.setProjectDir(project.file("."));
      }
    });
    project.getTasks().add(haikulink);

    // Add the plugin to build native code for the Rpi
    project.getPlugins().apply("com.github.chuckb.buildpi");

    // Shim up dependencies between haukilink and native build.
    // Why do this here instead of on the model? Because we need the project to get the 
    // tasks, which is not available in the model unless hoops are jumped through to 
    // make it so. It is possible (see https://dzone.com/articles/gradle-goodness-getting-project-information-into-r).
    // It is just easier to manipulate what the c and cpp plugins have
    // done to the project afterward.
    /*
    project.getPluginManager().withPlugin("com.github.chuckb.buildpi", new Action<AppliedPlugin>() {
      @Override
      public void execute(AppliedPlugin appliedPlugin) {
        project.getTasks().named("compileMainExecutableMainCpp", CppCompile.class,  cppCompile -> {
          System.out.println("in here!!!");
        });
      }
    });
    */
  }
}