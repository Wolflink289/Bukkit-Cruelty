package com.wolflink289.bukkit.cruelty;

import org.bukkit.ChatColor;
import com.wolflink289.bukkit.util.BukkitSender;
import com.wolflink289.util.Local;

public class CrueltyStrings {
	
	// Errors
	static public String ERROR_NO_PERMISSION;
	static public String ERROR_IO_SAVE;
	
	// Messages
	static public String MSG_ENABLED;
	static public String MSG_DISABLED;
	
	static public String MSG_USG_FREEZE;
	static public String MSG_USG_FEIGN;
	static public String MSG_USG_CRASH;
	
	static public String MSG_ACT_FREEZE;
	static public String MSG_ACT_FEIGN;
	static public String MSG_ACT_CRASH;
	
	// Prefixes
	static public String PFX_SUCCESS;
	static public String PFX_IMMUNE;
	static public String PFX_NOT_FOUND;
	
	static public String PFX_MSG;
	
	// Usages
	
	/**
	 * Refresh the cached strings.
	 */
	static public void refresh() {
		// Put Defaults
		Local.putDefault("error.denied", ChatColor.RED + "Permission denied!");
		Local.putDefault("error.save", "Unable to save:");
		Local.putDefault("message.enabled", "Enabled.");
		Local.putDefault("message.disabled", "Disabled.");
		Local.putDefault("message.usage.freeze", ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelfreeze " + ChatColor.GRAY + "[Player,Player]");
		Local.putDefault("message.usage.feign", ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelfeign " + ChatColor.GRAY + "[Player,Player]");
		Local.putDefault("message.usage.crash", ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelcrash " + ChatColor.GRAY + "[Player,Player]");
		Local.putDefault("message.action.freeze", "Froze ${COUNT} players: ${PLAYERS}");
		Local.putDefault("message.action.feign", "Feigned ${COUNT} players: ${PLAYERS}");
		Local.putDefault("message.action.crash", "Crashed ${COUNT} players: ${PLAYERS}");
		Local.putDefault("prefix.attack.success", String.valueOf(ChatColor.GREEN));
		Local.putDefault("prefix.attack.immune", String.valueOf(ChatColor.DARK_RED));
		Local.putDefault("prefix.attack.not-found", String.valueOf(ChatColor.RED));
		Local.putDefault("prefix.message", String.valueOf(ChatColor.GOLD));
		
		// Get Strings
		ERROR_NO_PERMISSION = Local.get("error.denied");
		ERROR_IO_SAVE = Local.get("error.save");
		
		MSG_ENABLED = Local.get("message.enabled");
		MSG_DISABLED = Local.get("message.disabled");
		
		MSG_ACT_FREEZE = Local.get("message.action.freeze");
		MSG_ACT_FEIGN = Local.get("message.action.feign");
		MSG_ACT_CRASH = Local.get("message.action.crash");
		
		MSG_USG_FREEZE = Local.get("message.usage.freeze").replace('&', '\247');
		MSG_USG_FEIGN = Local.get("message.usage.feign").replace('&', '\247');
		MSG_USG_CRASH = Local.get("message.usage.crash").replace('&', '\247');
		
		PFX_SUCCESS = Local.get("prefix.attack.success").replace('&', '\247');
		PFX_IMMUNE = Local.get("prefix.attack.immune").replace('&', '\247');
		PFX_NOT_FOUND = Local.get("prefix.attack.not-found").replace('&', '\247');
		
		PFX_MSG = Local.get("prefix.message").replace('&', '\247');
		if (!ChatColor.stripColor(PFX_MSG).isEmpty()) PFX_MSG += " ";
		
		BukkitSender.setMessagePrefix(PFX_MSG);
	}
}
