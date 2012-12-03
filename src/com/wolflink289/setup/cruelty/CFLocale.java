package com.wolflink289.setup.cruelty;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.wolflink289.setup.cruelty.gui.WXListItem;
import com.wolflink289.setup.cruelty.gui.WXListRenderer;
import com.wolflink289.util.Local;
import com.wolflink289.util.Path;

/**
 * Configuration for locale.
 * 
 * @author Wolflink289
 */
public class CFLocale {
	
	static private JFrame window;
	static private JButton next;
	static private JList localelist;
	static private String[] locales;
	static private String locale;
	static private int lastlocale;
	
	/**
	 * A list display object for a locale.
	 * 
	 * @author Wolflink289
	 */
	static private class CFLocaleItem extends WXListItem {
		public final String locale;
		
		public CFLocaleItem(String locale) {
			super(locale, locale);
			this.locale = locale;
			
			// Set Localized Info
			String[] info = CFLocale.info(locale);
			set(info[0], info[1]);
		}
		
		@Override
		public String toString() {
			return locale;
		}
	}
	
	/**
	 * List directory contents for a resource folder. Not recursive.
	 * This is basically a brute-force implementation.
	 * Works for regular files and also JARs.
	 * 
	 * @author Greg Briggs
	 * @param clazz Any java class that lives in the same place as the resources you want.
	 * @param path Should end with "/", but not start with one.
	 * @return Just the name of each member item, not the full paths.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	static private String[] getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			/* A file path: easy enough */
			return new File(dirURL.toURI()).list();
		}
		
		if (dirURL == null) {
			/* 
			 * In case of a jar file, we can't actually find a directory.
			 * Have to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}
		
		if (dirURL.getProtocol().equals("file")) {
			String nd = URLDecoder.decode(dirURL.toURI().getPath(), "UTF-8");
			nd = nd.substring(0, nd.lastIndexOf("com/wolflink289/setup/cruelty/"));
			path = path.substring(1);
			
			return new File(nd, path).list();
		}
		
		if (dirURL.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			HashSet<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path)) { // filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					result.add(entry);
				}
			}
			return result.toArray(new String[result.size()]);
		}
		
		throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
	}
	
	/**
	 * Load the list of locales.
	 */
	static private void load() {
		try {
			// Load Locales
			String[] list = getResourceListing(CFLocale.class, "locale/");
			ArrayList<String> locales = new ArrayList<String>();
			
			// Enumerate
			for (int i = 0; i < list.length; i++) {
				if (list[i].contains(".")) continue;
				if (Thread.currentThread().getContextClassLoader().getResource("locale/" + list[i] + "/strings.txt") == null) continue;
				if (Thread.currentThread().getContextClassLoader().getResource("locale/" + list[i] + "/strings_wiz.txt") == null) continue;
				locales.add(list[i]);
			}
			
			// Set
			CFLocale.locales = locales.toArray(new String[0]);
			locales.clear();
			locales = null;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "The locales cannot be listed!", Local.get("error.title.fatal"), JOptionPane.CANCEL_OPTION | JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Get the information about a locale.
	 * 
	 * @param locale the locale.
	 * @return the locale info.
	 */
	static String[] info(String locale) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(CFLocale.class.getResourceAsStream("/locale/" + locale + "/strings_wiz.txt"), "UTF-8"));
			String line;
			String[] info = new String[2];
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				
				int idx = line.indexOf(':');
				if (idx == -1) continue;
				
				if (line.substring(0, idx).trim().equals("locale.language")) {
					info[0] = line.substring(idx + 1).trim();
					if (info[0] != null && info[1] != null) break;
				} else if (line.substring(0, idx).trim().equals("locale.region")) {
					info[1] = line.substring(idx + 1).trim();
					if (info[0] != null && info[1] != null) break;
				}
				
			}
			line = null;
			br.close();
			
			if (info[0] == null) info[0] = locale;
			if (info[1] == null) info[1] = "Unknown";
			return info;
		} catch (Exception ex) {
			return new String[] { locale, locale };
		}
	}
	
	/**
	 * Update the window state with locale information.
	 * 
	 * @param locale the locale name.
	 */
	static void update(String locale) {
		if (locale.equals(CFLocale.locale)) return;
		
		CFLocale.locale = locale;
		
		Local.setLocale(locale);
		Local.refresh();
		
		window.setTitle(Local.get("win.locale.title"));
		next.setText(Local.get("win.locale.button.next"));
	}
	
	/**
	 * Show the locale selection window.
	 */
	static public void show() {
		// Load
		load();
		
		// Generate locale list items
		CFLocaleItem[] items = new CFLocaleItem[locales.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = new CFLocaleItem(locales[i]);
		}
		
		// Set L&F
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {}
		
		// Build GUI
		window = new JFrame(Local.get("win.locale.title"));
		next = new JButton(Local.get("win.locale.button.next"));
		localelist = new JList(items);
		JScrollPane jsp = new JScrollPane(localelist);
		SpringLayout spl = new SpringLayout();
		
		// Build GUI - Locale List
		localelist.setCellRenderer(new WXListRenderer());
		localelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		localelist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) return;
				if (((JList) e.getSource()).getSelectedIndex() == -1) {
					localelist.setSelectedIndex(lastlocale);
					return;
				}
				
				lastlocale = ((JList) e.getSource()).getSelectedIndex();
				CFLocale.update(((JList) e.getSource()).getModel().getElementAt(lastlocale).toString());
			}
		});
		
		localelist.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				e.consume();
			}
		});
		
		// Select default locales
		boolean ul = false;
		for (int i = 0; i < locales.length; i++) {
			if (Local.getLocale().equals(locales[i])) {
				lastlocale = i;
				localelist.setSelectedIndex(i);
				CFLocale.locale = locales[i];
				ul = true;
				break;
			}
		}
		
		if (!ul) {
			for (int i = 0; i < locales.length; i++) {
				if (locales[i].equals("enUS")) {
					lastlocale = i;
					localelist.setSelectedIndex(i);
					CFLocale.locale = locales[i];
					break;
				}
			}
		}
		
		// Build GUI - Button
		next.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				CrueltySetup.config.set("locale", locale);
				try {
					CrueltySetup.save();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, Local.get("error.message.save") + " " + new File(Path.getJar(), "Cruelty" + File.separator + "config.txt").getAbsolutePath(), Local.get("error.title.fatal"), JOptionPane.CANCEL_OPTION | JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
					System.exit(0);
				}
				
				CFLocale.kill();
			}
			
		});
		
		// Build GUI - Layout
		spl.putConstraint("West", jsp, 0, "West", window.getContentPane());
		spl.putConstraint("East", jsp, 0, "East", window.getContentPane());
		spl.putConstraint("North", jsp, 0, "North", window.getContentPane());
		spl.putConstraint("South", jsp, -2, "North", next);
		
		spl.putConstraint("West", next, 2, "West", window.getContentPane());
		spl.putConstraint("East", next, -2, "East", window.getContentPane());
		spl.putConstraint("South", next, -2, "South", window.getContentPane());
		
		// Build GUI - Window Properties
		window.getContentPane().setLayout(spl);
		window.getContentPane().add(jsp);
		window.getContentPane().add(next);
		window.setResizable(false);
		window.setSize(200, 230);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		
		// Clean
		spl = null;
		jsp = null;
		items = null;
	}
	
	/**
	 * Destroy the locale selection window.
	 */
	static public void kill() {
		next = null;
		localelist = null;
		
		window.dispose();
		window = null;
		
		locale = null;
		locales = null;
		
		try {
			Runtime.getRuntime().gc();
		} catch (Exception ex) {}
	}
}
