package com.wolflink289.bukkit.cruelty;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import com.wolflink289.bukkit.util.BukkitCommand;
import com.wolflink289.util.Local;

public class CrueltyPlugin extends JavaPlugin {
	
	// Listener: Plugin Enabled
	@Override
	public void onEnable() {
		// Load locale
		Local.refresh();
		
		// Load config
		
		
		// Register commands
		// TODO register
		
		// Log
		getLogger().info("Enabled.");
	}
	
	// Listener: Plugin Disabled
	@Override
	public void onDisable() {
		
		// Log
		getLogger().info("Disabled.");
	}
	
	// Listener: Command Executed
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] params) {
		return BukkitCommand.call(sender, command, alias, params);
	}
}
