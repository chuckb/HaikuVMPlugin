package com.chuckbenedict.gradle.plugin;

import java.net.URI;

import com.chuckbenedict.gradle.plugin.extensions.HaikuVMExtension;
import com.chuckbenedict.gradle.plugin.extensions.NativeBuildExtension;
import com.chuckbenedict.gradle.plugin.internal.GradleUtil;
import com.chuckbenedict.gradle.plugin.tasks.Haikulink;

import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Copy;

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
    haikuToolsConfig.getDependencies().add(project.getDependencies().create("com.github.chuckb:haikuVM:master-SNAPSHOT"));

    // Get the compile configuration
    final Configuration compileConfig = project.getConfigurations().getByName("compile");
    // Add the haikuVM Java build dependencies
    // TODO: Make the version configurable
    compileConfig.getDependencies().add(project.getDependencies().create("com.github.chuckb.haikuVM:haikuRT:master-SNAPSHOT"));
    compileConfig.getDependencies().add(project.getDependencies().create("com.github.chuckb.haikuVM:bootstrap:master-SNAPSHOT"));

    // Create the unzip task to unbundle HaikuVM
    Task unzipHaikuVM = project.getTasks().create("unzipHaikuVM", Copy.class, new Action<Copy>() {
      public void execute(Copy t) {
        t.from(project.zipTree(GradleUtil.getJar(project, "haikutools", "build")), spec -> {
          spec.include("**/*");
        });
        t.into(haikuVMExtension.getHaikuVMDirProvider());
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
    nativeBuildExtension.setPort(haikuVMExtension.getPortProvider());
    project.getPlugins().apply("com.github.chuckb.buildpi");

    // Add plugin to build Pi image files
    project.getPlugins().apply("com.github.chuckb.piimage");
  }
}