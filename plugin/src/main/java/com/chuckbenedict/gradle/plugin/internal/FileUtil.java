package com.chuckbenedict.gradle.plugin.internal;

import java.io.File;

public class FileUtil {
	public static String getAbsolutePath(File file) {
		String absolutePath = file.getAbsolutePath();

		return absolutePath.replace('\\', '/');
	}  
}