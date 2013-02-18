package com.wolflink289.bukkit.cruelty;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import com.wolflink289.apis.bukkit.Cruelty;
import com.wolflink289.bukkit.cruelty.command.*;
import com.wolflink289.bukkit.util.BukkitCommand;
import com.wolflink289.bukkit.util.BukkitSender;
import com.wolflink289.util.Config;
import com.wolflink289.util.Local;
import com.wolflink289.util.Path;

public class CrueltyPlugin extends JavaPlugin {
	
	// Variables
	static private Config cfg;
	static private Logger log;
	static public JavaPlugin instance;
	
	// Methods
	static public Logger getPluginLogger() {
		return log;
	}
	
	// Listener: Plugin Enabled
	@Override
	public void onEnable() {
		instance = this;
		
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
		CrueltyStrings.refresh(cfg);
		
		// Register commands
		BukkitCommand.clear();
		BukkitCommand.register("cruelfreeze", new FreezeCommand());
		BukkitCommand.register("cruelfeign", new FeignCommand());
		BukkitCommand.register("cruelcrash", new CrashCommand());
		BukkitCommand.register("cruelinventoryfuck", new InventoryFuckCommand());
		BukkitCommand.register("cruelspam", new SpamCommand());
		BukkitCommand.register("crueldos", new DosCommand());
		BukkitCommand.register("crueltrip", new TripCommand());
		BukkitCommand.register("cruelscream", new ScreamCommand());
		BukkitCommand.register("cruelnothingness", new NothingnessCommand());
		BukkitCommand.register("cruelparanoia", new ParanoiaCommand());
		BukkitCommand.register("crueldeny", new DenyCommand());
		BukkitCommand.register("cruellag", new LagCommand());
		BukkitCommand.register("cruelannoy", new AnnoyCommand());
		
		// Log
		getLogger().info(CrueltyStrings.MSG_ENABLED);
	}
	
	// Listener: Plugin Disabled
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		// Clear
		Cruelty.disable();
		BukkitCommand.clear();
		cfg = null;
		
		// Log
		getLogger().info(CrueltyStrings.MSG_DISABLED);
	}
	
	// Listener: Command Executed
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] params) {
		try {
			return BukkitCommand.call(sender, command, alias, params);
		} catch (UnsupportedOperationException ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
			return true;
		}
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
