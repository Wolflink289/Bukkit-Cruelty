package com.wolflink289.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Save configuration utility.
 * 
 * @author Wolflink289
 */
public class Config {
	
	// Fields
	private File last;
	private HashMap<String, String> keys;
	
	// Constructors
	/**
	 * Create a new blank configuration object.
	 */
	public Config() {
		keys = new LinkedHashMap<String, String>();
		last = null;
	}
	
	/**
	 * Create a configuration object from a file.
	 * 
	 * @param file the file to load from.
	 * @throws IOException
	 */
	public Config(File file) throws IOException {
		this();
		load(file);
	}
	
	// Methods
	/**
	 * Load the configuration from a file.
	 * 
	 * @param file the file to load from.
	 * @throws IOException
	 */
	public void load(File file) throws IOException {
		if (file == null) throw new NullPointerException("The file cannot be null");
		
		// Set Last
		last = file;
		
		// Load
		int cidx = 0;
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		
		while ((line = br.readLine()) != null) {
			line = line.trim();
			
			if (line.isEmpty() || line.startsWith("#")) {
				keys.put("#COMMENT " + (cidx++), line);
				continue;
			}
			
			int idx = line.indexOf("=");
			if (idx == -1) {
				keys.put("#COMMENT " + (cidx++), line);
				continue;
			}
			
			keys.put(line.substring(0, idx), line.substring(idx + 1));
		}
		
		// Clean
		line = null;
		br.close();
	}
	
	/**
	 * Save the configuration to a file.
	 * 
	 * @param file the file to save to.
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		if (file == null) file = last;
		if (file == null) throw new NullPointerException("The file cannot be null");
		
		// Create Directories
		file.getParentFile().mkdirs();
		
		// Save
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		Iterator<String> keyi = keys.keySet().iterator();
		
		// Iterate keys
		String next;
		while (keyi.hasNext()) {
			next = keyi.next();
			if (next.startsWith("#")) {
				bw.write(keys.get(next));
				bw.newLine();
				continue;
			}
			
			bw.write(next + "=" + keys.get(next));
			bw.newLine();
		}
		
		// Clean
		next = null;
		bw.close();
	}
	
	/**
	 * Does the configuration contain an entry by the name of the specified key.
	 * 
	 * @param key the name of the entry.
	 * @return whether it contains the entry.
	 */
	public boolean contains(String key) {
		return keys.containsKey(key);
	}
	
	/**
	 * Get the configuration entries as a hash map.
	 * 
	 * @return the hash map used to store entries.
	 */
	public HashMap<String, String> asMap() {
		return keys;
	}
	
	/**
	 * Get a configuration entry as a string.
	 * 
	 * @param key the entry's key.
	 * @return the entry's value.
	 */
	public String get(String key) {
		return keys.get(key);
	}
	
	/**
	 * Get a configuration entry as a string.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public String get(String key, String def) {
		String val = get(key);
		if (val == null) val = def;
		
		return val;
	}
	
	/**
	 * Get a configuration entry as a long.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public long get(String key, long def) {
		try {
			return Long.parseLong(get(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Get a configuration entry as an integer.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public int get(String key, int def) {
		try {
			return Integer.parseInt(get(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Get a configuration entry as a short.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public short get(String key, short def) {
		try {
			return Short.parseShort(get(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Get a configuration entry as a byte.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public byte get(String key, byte def) {
		try {
			return Byte.parseByte(get(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Get a configuration entry as a double.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public double get(String key, double def) {
		try {
			return Double.parseDouble(get(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Get a configuration entry as a float.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public float get(String key, float def) {
		try {
			return Float.parseFloat(get(key));
		} catch (NumberFormatException ex) {
			return def;
		}
	}
	
	/**
	 * Get a configuration entry as a boolean.
	 * 
	 * @param key the entry's key.
	 * @param def the default value of the entry (in case it's mission)
	 * @return the entry's value.
	 */
	public boolean get(String key, boolean def) {
		String val = get(key);
		if (val.equals("1") || val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")) { return true; }
		if (val.equals("0") || val.equalsIgnoreCase("false") || val.equalsIgnoreCase("no")) { return false; }
		return def;
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, String value) {
		keys.put(key, value);
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, long value) {
		keys.put(key, String.valueOf(value));
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, int value) {
		keys.put(key, String.valueOf(value));
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, short value) {
		keys.put(key, String.valueOf(value));
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, byte value) {
		keys.put(key, String.valueOf(value));
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, double value) {
		keys.put(key, String.valueOf(value));
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, float value) {
		keys.put(key, String.valueOf(value));
	}
	
	/**
	 * Set a configuration entry.
	 * 
	 * @param key the entry's key.
	 * @param value the entry's new value.
	 */
	public void set(String key, boolean value) {
		keys.put(key, value ? "yes" : "no");
	}
}
