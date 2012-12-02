package com.wolflink289.bukkit.util;

import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * A simpler, more organized way to handle commands in a Bukkit plugin.
 * 
 * @author Wolflink289
 */
public abstract class BukkitCommand {
	
	// As Handler
	static private HashMap<String, BukkitCommand> commands = new HashMap<String, BukkitCommand>();
	
	/**
	 * Register a command to the system.
	 * 
	 * @param command the command.
	 * @param handler the handler.
	 */
	static public void register(String command, BukkitCommand handler) {
		commands.put(command.toLowerCase(), handler);
	}
	
	/**
	 * Call (execute) a registered command.
	 * 
	 * @param sender the command sender.
	 * @param command the command.
	 * @param alias the command's alias.
	 * @param params the parameters.
	 * @return true for handled, false for not.
	 */
	static public boolean call(CommandSender sender, Command command, String alias, String[] params) {
		if (!commands.containsKey(command.getName().toLowerCase())) { return false; }
		commands.get(command.getName().toLowerCase()).handle(new BukkitSender(sender), params);
		return true;
	}
	
	// As Instance
	/**
	 * Handle the command.
	 * 
	 * @param sender the command sender.
	 * @param params the command parameters.
	 */
	protected abstract void handle(BukkitSender sender, String[] params);
}
