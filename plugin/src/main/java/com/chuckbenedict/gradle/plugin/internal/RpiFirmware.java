package com.chuckbenedict.gradle.plugin.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Get individual files from the Raspberry Pi GitHub boot directory
 * without downloading the entire repository.
 */
public class RpiFirmware {
  private static String firmwareBase = "https://github.com/raspberrypi/firmware/raw/";

  /**
   * Set the raw URL location of the Raspberry Pi GitHub repository
   * 
   * @param firmwareBase  Defaults to "https://github.com/raspberrypi/firmware/raw/"
   */
  public static void setFirmwareBase(String firmwareBase) {
    RpiFirmware.firmwareBase = firmwareBase;
  }

  /**
   * Get a specific file from a GitHub repository (using the raw subdirectory of the repo in question.)
   * 
   * @param branch        The name of the branch or tag (defaults to master)
   * @param file          The name of the file to get.
   * @param destination   The local destination to place the file.
   * @throws IOException
   */
  public static void getFile(String branch, String file, File destination) throws IOException {
    if (branch == "") {
      branch = "master";
    }
    URLReader.copyURLToFile(getURL(branch, file), destination);
  }

  /**
   * Assemble the URL of the Raspberry Pi file to fetch.
   * 
   * @param branch    Repo branch or tag name to get the file from. 
   * @param file      Name of the file to get.
   * @return          The assembled URL of the file to fetch.
   * @throws MalformedURLException
   */
  private static URL getURL(String branch, String file) throws MalformedURLException {
    return new URL(firmwareBase + branch + "/boot/" + file);
  }
}