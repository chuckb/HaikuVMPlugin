package com.chuckbenedict.gradle.plugin;

import javax.inject.Inject;

import org.gradle.api.model.ObjectFactory;

/**
 * TODO: This is a work in process to break out Java build
 * particulars for HaikuVM
 */
public class JavaPlugin extends org.gradle.api.plugins.JavaPlugin {
  private ObjectFactory objectFactory;
  /**
   * Call the super's constructor and wire up a local copy
   * of the gradle ObjectFactory for future use.
   * @param objectFactory
   */
  @Inject
  public JavaPlugin(ObjectFactory objectFactory) {
    super(objectFactory);
    this.objectFactory = objectFactory;
  }
}