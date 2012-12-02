package com.wolflink289.util;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Path retrieval utility.
 * 
 * @author Wolflink289
 */
public class Path {
	/**
	 * Get the directory where the jar is located.
	 * 
	 * @return the directory.
	 */
	static public File getJar() {
		try {
			return new File(Path.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
		} catch (URISyntaxException e) {
			return new File(Path.class.getProtectionDomain().getCodeSource().getLocation().toString()).getParentFile(); // Better than nothing.
		}
	}
	
	/**
	 * Get the current working directory.
	 * 
	 * @return the directory.
	 */
	static public File getWorking() {
		return new File(System.getProperty("user.dir"));
	}
	
	/**
	 * Get the user's home directory.
	 * 
	 * @return the directory.
	 */
	static public File getHome() {
		return new File(System.getProperty("user.home"));
	}
}
