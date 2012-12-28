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
	static public String MSG_USG_IFUCK;
	static public String MSG_USG_SPAM;
	static public String MSG_USG_DOS;
	
	static public String MSG_PR1_IFUCK;
	static public String MSG_PR1_SPAM;
	
	static public String MSG_ACT_FREEZE;
	static public String MSG_ACT_FEIGN;
	static public String MSG_ACT_CRASH;
	static public String MSG_ACT_IFUCK;
	static public String MSG_ACT_SPAM;
	static public String MSG_ACT_DOS;
	
	static public String MSG_ERR_DEPEND;
	
	static public String MSG_DO_SPAM_JOIN, MSG_DO_SPAM_LEAVE, MSG_DO_SPAM_SPEAK;
	
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
		Local.putDefault("message.usage.invfuck", ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelinvfuck " + ChatColor.GRAY + "[Player,Player] [Method]");
		Local.putDefault("message.usage.spam", ChatColor.RED + "Usage: " + ChatColor.WHITE + "/cruelspam " + ChatColor.GRAY + "[Player,Player] [Method]");
		Local.putDefault("message.usage.dos", ChatColor.RED + "Usage: " + ChatColor.WHITE + "/crueldos " + ChatColor.GRAY + "[Player,Player]");
		Local.putDefault("message.param.invfuck.1", ChatColor.RED + "Methods: " + ChatColor.WHITE + "${METHODS}");
		Local.putDefault("message.param.spam.1", ChatColor.RED + "Methods: " + ChatColor.WHITE + "${METHODS}");
		Local.putDefault("message.action.freeze", "Froze ${COUNT} players: ${PLAYERS}");
		Local.putDefault("message.action.feign", "Feigned ${COUNT} players: ${PLAYERS}");
		Local.putDefault("message.action.crash", "Crashed ${COUNT} players: ${PLAYERS}");
		Local.putDefault("message.action.invfuck", "Fucked with ${COUNT} inventories: ${PLAYERS}");
		Local.putDefault("message.action.spam", "Spammed ${COUNT} players: ${PLAYERS}");
		Local.putDefault("message.action.dos", "Dos'd ${COUNT} players: ${PLAYERS}");
		Local.putDefault("message.do.spam.join", ChatColor.YELLOW + "${NAME} joined the game.");
		Local.putDefault("message.do.spam.leave", ChatColor.YELLOW + "${NAME} left the game.");
		Local.putDefault("message.do.spam.speak", "<${NAME}> ${MESSAGE}");
		Local.putDefault("message.error.depend", ChatColor.RED + "Error: one or more missing dependencies.");
		Local.putDefault("prefix.attack.success", String.valueOf(ChatColor.GREEN));
		Local.putDefault("prefix.attack.immune", String.valueOf(ChatColor.DARK_RED));
		Local.putDefault("prefix.attack.not-found", String.valueOf(ChatColor.RED));
		Local.putDefault("prefix.message", String.valueOf(ChatColor.GOLD));
		
		// Get Strings
		ERROR_NO_PERMISSION = Local.get("error.denied").replace('&', '\247');
		ERROR_IO_SAVE = Local.get("error.save");
		
		MSG_ENABLED = Local.get("message.enabled");
		MSG_DISABLED = Local.get("message.disabled");
		
		MSG_ACT_FREEZE = Local.get("message.action.freeze").replace('&', '\247');
		MSG_ACT_FEIGN = Local.get("message.action.feign").replace('&', '\247');
		MSG_ACT_CRASH = Local.get("message.action.crash").replace('&', '\247');
		MSG_ACT_IFUCK = Local.get("message.action.invfuck").replace('&', '\247');
		MSG_ACT_SPAM = Local.get("message.action.spam").replace('&', '\247');
		MSG_ACT_DOS = Local.get("message.action.dos").replace('&', '\247');
		
		MSG_PR1_IFUCK = Local.get("message.param.invfuck.1").replace('&', '\247');
		MSG_PR1_SPAM = Local.get("message.param.spam.1").replace('&', '\247');
		
		MSG_USG_FREEZE = Local.get("message.usage.freeze").replace('&', '\247');
		MSG_USG_FEIGN = Local.get("message.usage.feign").replace('&', '\247');
		MSG_USG_CRASH = Local.get("message.usage.crash").replace('&', '\247');
		MSG_USG_IFUCK = Local.get("message.usage.invfuck").replace('&', '\247');
		MSG_USG_SPAM = Local.get("message.usage.spam").replace('&', '\247');
		MSG_USG_DOS = Local.get("message.usage.dos").replace('&', '\247');
		
		MSG_ERR_DEPEND = Local.get("message.error.depend").replace('&', '\247');
		
		MSG_DO_SPAM_JOIN = Local.get("message.do.spam.join").replace('&', '\247');
		MSG_DO_SPAM_LEAVE = Local.get("message.do.spam.leave").replace('&', '\247');
		MSG_DO_SPAM_SPEAK = Local.get("message.do.spam.speak").replace('&', '\247');
		
		PFX_SUCCESS = Local.get("prefix.attack.success").replace('&', '\247');
		PFX_IMMUNE = Local.get("prefix.attack.immune").replace('&', '\247');
		PFX_NOT_FOUND = Local.get("prefix.attack.not-found").replace('&', '\247');
		
		PFX_MSG = Local.get("prefix.message").replace('&', '\247');
		if (!ChatColor.stripColor(PFX_MSG).isEmpty()) PFX_MSG += " ";
		
		BukkitSender.setMessagePrefix(PFX_MSG);
	}
}
