package com.chuckbenedict.gradle.plugin.internal;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.specs.Spec;

public class GradleUtil {
	public static File toFile(Project project, Object object) {
		if (object == null) {
			return null;
		}
		return project.file(object);
	}

  /**
   * Given a project and configuration name, search through files to see
   * if the given string name is contained within any of them. If so, return
   * the first file found.
   * @param project             The project to search.
   * @param configurationName   The configuration name conained with the project.
   * @param name                The piece of the filename to search for.
   * @return                    The first found File.
   */
  public static File getJar(final Project project, final String configurationName, final String name) {
    return project.getConfigurations().getByName(configurationName).filter(new Spec<File>() {
      @Override
      public boolean isSatisfiedBy(File file) {
        return file.getName().contains(name);
      }
    }).getSingleFile();    
  }
}