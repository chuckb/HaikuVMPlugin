package com.chuckbenedict.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.internal.impldep.org.junit.Test;
import org.gradle.internal.impldep.org.junit.Assert;
import org.gradle.testfixtures.ProjectBuilder;

public class HaikuVMPluginTest {
  @Test
  public void haikuPluginAddsHaikulinkTaskToProject() {
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply("com.github.chuckb.HaikuVMPlugin");

    Assert.assertTrue(project.getTasks().findByName("haikulink") instanceof HaikuVMPlugin);
  }  
}