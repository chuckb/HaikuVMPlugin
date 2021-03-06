package com.chuckbenedict.gradle.plugin.tasks;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

public class ShellDeployScript extends DefaultTask {
  private final ObjectFactory objectFactory;

  @OutputFile
  public final RegularFileProperty script;

  @InputFile
  public final RegularFileProperty image;

  @InputFile
  public final RegularFileProperty bootLoader;

  @Input
  public final Property<String> port;

  @Inject
  public ShellDeployScript(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
    script = objectFactory.fileProperty();
    image = objectFactory.fileProperty();
    bootLoader = objectFactory.fileProperty();
    port = objectFactory.property(String.class);
    this.setDescription("Create a shell script that will launch an external process to run Rasbootin.");
  }

  @TaskAction
  void create() throws IOException {
    File file = script.getAsFile().get();
    file.delete();
    file.createNewFile();
    file.setExecutable(true, true);
    PrintWriter writer = new PrintWriter(file);
    writer.println("#!/bin/bash");
    writer.println("'" + bootLoader.getAsFile().get().getAbsolutePath() + "' '" + port.get() + "' '" + image.getAsFile().get().getAbsolutePath() + "'");
    writer.close();
  }  
}