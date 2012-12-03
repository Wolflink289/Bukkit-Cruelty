package com.wolflink289.bukkit.cruelty;

import org.bukkit.ChatColor;
import com.wolflink289.bukkit.util.BukkitSender;
import com.wolflink289.util.Local;

public class CrueltyStrings {
	
	// Errors
	static public String ERROR_NO_PERMISSION = ChatColor.RED + "Permission denied!";
	static public String ERROR_IO_SAVE = "Unable to save:";
	
	// Messages
	static public String MSG_ENABLED = "Enabled.";
	static public String MSG_DISABLED = "Disabled.";
	
	static public String MSG_USG_FREEZE = ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelfreeze " + ChatColor.GRAY + "[Player,Player]";
	static public String MSG_USG_FEIGN = ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelfeign " + ChatColor.GRAY + "[Player,Player]";
	static public String MSG_USG_CRASH = ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelcrash " + ChatColor.GRAY + "[Player,Player]";
	
	static public String MSG_ACT_FREEZE = "Froze ${COUNT} players: ${PLAYERS}";
	static public String MSG_ACT_FEIGN = "Feigned ${COUNT} players: ${PLAYERS}";
	static public String MSG_ACT_CRASH = "Crashed ${COUNT} players: ${PLAYERS}";
	
	// Prefixes
	static public String PFX_SUCCESS = String.valueOf(ChatColor.GREEN);
	static public String PFX_IMMUNE = String.valueOf(ChatColor.DARK_RED);
	static public String PFX_NOT_FOUND = String.valueOf(ChatColor.RED);
	
	static public String PFX_MSG = String.valueOf(ChatColor.GOLD);
	
	// Usages
	
	/**
	 * Refresh the cached strings.
	 */
	static public void refresh() {
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
