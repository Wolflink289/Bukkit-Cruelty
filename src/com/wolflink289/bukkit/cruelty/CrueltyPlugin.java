package com.wolflink289.bukkit.cruelty;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import com.wolflink289.apis.bukkit.Cruelty;
import com.wolflink289.bukkit.cruelty.command.CrashCommand;
import com.wolflink289.bukkit.cruelty.command.FeignCommand;
import com.wolflink289.bukkit.cruelty.command.FreezeCommand;
import com.wolflink289.bukkit.cruelty.command.InventoryFuckCommand;
import com.wolflink289.bukkit.cruelty.command.SpamCommand;
import com.wolflink289.bukkit.util.BukkitCommand;
import com.wolflink289.bukkit.util.BukkitSender;
import com.wolflink289.util.Config;
import com.wolflink289.util.Local;
import com.wolflink289.util.Path;

public class CrueltyPlugin extends JavaPlugin {
	
	// Variables
	static private Config cfg;
	static private Logger log;
	
	// Methods
	static public Logger getPluginLogger() {
		return log;
	}
	
	// Listener: Plugin Enabled
	@Override
	public void onEnable() {
		// Set logger
		log = this.getLogger();
		
		// Load dependencies
		Cruelty.reload();
		
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
		BukkitCommand.register("cruelinventoryfuck", new InventoryFuckCommand());
		BukkitCommand.register("cruelspam", new SpamCommand());
		
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
	
	// Static Methods
	/**
	 * Send the no permission message to a sender.
	 * 
	 * @param sender the sender.
	 */
	static public void noPermission(BukkitSender sender) {
		String message = CrueltyStrings.ERROR_NO_PERMISSION;
		if (message.equals("${HIDDEN}")) {
			sender.getSender().getServer().dispatchCommand(sender.getSender(), "/^ CRUELTY NO PERMISSION MESSAGE ^\\ #" + Math.random());
		} else {
			sender.getSender().sendMessage(message);
		}
	}
}
