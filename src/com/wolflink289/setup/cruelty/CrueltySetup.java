package com.wolflink289.setup.cruelty;

import java.io.File;
import java.io.IOException;
import com.wolflink289.util.Config;
import com.wolflink289.util.Local;
import com.wolflink289.util.Path;

public class CrueltySetup {
	static public Config config;
	
	static public void save() throws IOException {
		config.save(new File(Path.getJar(), "Cruelty" + File.separator + "config.txt"));
	}
	
	static public void main(String[] args) {
		// Load Config
		try {
			config = new Config(new File(Path.getJar(), "Cruelty" + File.separator + "config.txt"));
		} catch (IOException e) {
			config = new Config();
		}
		
		// Load Locale
		if (config.contains("locale")) Local.setLocale(config.get("locale"));
		Local.setStringPath("strings_wiz.txt");
		Local.refresh();
		
		// Select Locale
		// if (!config.contains("locale")) {
		CFLocale.show();
		// }
	}
}
