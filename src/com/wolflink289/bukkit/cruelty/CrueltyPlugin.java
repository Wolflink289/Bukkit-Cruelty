package com.wolflink289.bukkit.cruelty;

import java.io.File;
import java.io.IOException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import com.wolflink289.bukkit.cruelty.command.CrashCommand;
import com.wolflink289.bukkit.cruelty.command.FeignCommand;
import com.wolflink289.bukkit.cruelty.command.FreezeCommand;
import com.wolflink289.bukkit.util.BukkitCommand;
import com.wolflink289.util.Config;
import com.wolflink289.util.Local;
import com.wolflink289.util.Path;

public class CrueltyPlugin extends JavaPlugin {
	
	// Variables
	static private Config cfg;
	
	// Listener: Plugin Enabled
	@Override
	public void onEnable() {
		// Load config
		try {
			cfg = new Config();
			cfg.load(new File(Path.getJar(), "Cruelty" + File.separator + "config.txt"));
		} catch (IOException e) {
			try {
				cfg.save(null);
			} catch (Exception ex) {
				getLogger().severe(CrueltyStrings.ERROR_IO_SAVE + " " + new File(Path.getJar(), "Cruelty" + File.separator + "config.txt").getAbsolutePath());
			}
		}
		
		// Load locale
		Local.setLocale(cfg.get("locale", "enUS"));
		Local.refresh();
		CrueltyStrings.refresh();
		
		// Register commands
		BukkitCommand.clear();
		BukkitCommand.register("cruelfreeze", new FreezeCommand());
		BukkitCommand.register("cruelfeign", new FeignCommand());
		BukkitCommand.register("cruelcrash", new CrashCommand());
		
		// Log
		getLogger().info(CrueltyStrings.MSG_ENABLED);
	}
	
	// Listener: Plugin Disabled
	@Override
	public void onDisable() {
		// Clear
		BukkitCommand.clear();
		cfg = null;
		
		// Log
		getLogger().info(CrueltyStrings.MSG_DISABLED);
	}
	
	// Listener: Command Executed
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] params) {
		return BukkitCommand.call(sender, command, alias, params);
	}
}
