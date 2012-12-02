package com.wolflink289.bukkit.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * A wrapper for a Bukkit command executor.
 * 
 * @author Wolflink289
 */
public class BukkitSender {
	
	// Static
	static private String prefix;
	
	/**
	 * Set the message prefix.
	 * 
	 * @param prefix the prefix.
	 */
	static public void setMessagePrefix(String prefix) {
		if (prefix == null) prefix = "";
		BukkitSender.prefix = prefix;
	}
	
	/**
	 * Get the message prefix.
	 * 
	 * @return the prefix.
	 */
	static public String getMessagePrefix() {
		return prefix;
	}
	
	// Instance
	private CommandSender sender;
	
	/**
	 * Create a new CommandSender wrapper object.
	 * 
	 * @param cs
	 */
	public BukkitSender(CommandSender cs) {
		sender = cs;
	}
	
	/**
	 * Get the command sender as a player.
	 * 
	 * @return the player, or null.
	 */
	public Player getPlayer() {
		if (!isPlayer()) return null;
		return (Player) sender;
	}
	
	/**
	 * Get the command sender's name.
	 * 
	 * @return the command sender's name.
	 */
	public String getName() {
		return sender.getName();
	}
	
	/**
	 * Get the command sender.
	 * 
	 * @return the command sender.
	 */
	public CommandSender getSender() {
		return sender;
	}
	
	/**
	 * Is the command sender a player?
	 * 
	 * @return whether the command sender a player.
	 */
	public boolean isPlayer() {
		return sender instanceof Player;
	}
	
	/**
	 * Wrapper for CommandSender.sendMessage(String)
	 * 
	 * @param messages the message to send.
	 */
	public void sendMessage(String message) {
		sender.sendMessage(prefix + message);
	}
	
	/**
	 * Wrapper for CommandSender.sendMessage(String[])
	 * 
	 * @param messages the messages to send.
	 */
	public void sendMessage(String[] messages) {
		if (messages.length != 0) messages[0] = prefix + messages[0];
		sender.sendMessage(messages);
	}
	
	/**
	 * Wrapper for CommandSender.hasPermission(String)
	 * 
	 * @param permission the permission.
	 * @return if the sender has the permission.
	 */
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}
	
	/**
	 * Wrapper for CommandSender.hasPermission(Permission)
	 * 
	 * @param permission the permission.
	 * @return if the sender has the permission.
	 */
	public boolean hasPermission(Permission permission) {
		return sender.hasPermission(permission);
	}
}
