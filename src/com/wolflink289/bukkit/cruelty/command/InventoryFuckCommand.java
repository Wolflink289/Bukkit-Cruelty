package com.wolflink289.bukkit.cruelty.command;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.wolflink289.apis.bukkit.Cruelty;
import com.wolflink289.bukkit.cruelty.CrueltyPermissions;
import com.wolflink289.bukkit.cruelty.CrueltyPlugin;
import com.wolflink289.bukkit.cruelty.CrueltyStrings;
import com.wolflink289.bukkit.util.BukkitCommand;
import com.wolflink289.bukkit.util.BukkitSender;
import com.wolflink289.bukkit.util.StrParser;

public class InventoryFuckCommand extends BukkitCommand {
	
	@Override
	protected void handle(BukkitSender sender, String[] params) {
		if (!CrueltyPermissions.CRASH.canUse(sender)) {
			CrueltyPlugin.noPermission(sender);
			return;
		}

		if (!Cruelty.Attacks.INVFUCK_HOTSWAP.isEnabled() || !Cruelty.Attacks.INVFUCK_SCRAMBLE.isEnabled()) {
			sender.getSender().sendMessage(CrueltyStrings.MSG_ERR_DEPEND);
			return;
		}
		
		if (params.length != 2) {
			sender.getSender().sendMessage(CrueltyStrings.MSG_USG_IFUCK); // Send without prefix
			return;
		}
		
		Cruelty.Attacks method = getMethod(params[1]);
		if (method == null) {
			sender.getSender().sendMessage(CrueltyStrings.MSG_USG_IFUCK); // Send without prefix
			sender.getSender().sendMessage(CrueltyStrings.MSG_PR1_IFUCK.replace("${METHODS}", "scramble hotswap")); // Send without prefix
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		Player[] targets = StrParser.LIST.parseAsPlayers(params[0]);
		String[] targetsl = StrParser.LIST.parse(params[0]);
		int suc = 0;
		for (int i = 0; i < targets.length; i++) {
			if (i != 0) sb.append(ChatColor.WHITE + ", ");
			if (targets[i] == null) {
				sb.append(CrueltyStrings.PFX_NOT_FOUND);
				sb.append(targetsl[i]);
			} else if (!targets[i].isOnline()) {
				sb.append(CrueltyStrings.PFX_NOT_FOUND);
				sb.append(targetsl[i]);
			} else {
				boolean ats = attack(method, targets[i]);
				if (ats) suc++;
				sb.append(ats ? CrueltyStrings.PFX_SUCCESS : CrueltyStrings.PFX_IMMUNE);
				sb.append(targets[i].getName());
			}
		}
		
		sender.sendMessage(CrueltyStrings.MSG_ACT_IFUCK.replace("${COUNT}", String.valueOf(suc)).replace("${PLAYERS}", sb.toString()));
		
		sb = null;
		targets = null;
		targetsl = null;
		method = null;
	}
	
	static private Cruelty.Attacks getMethod(String method) {
		if (method.equals("scramble")) return Cruelty.Attacks.INVFUCK_SCRAMBLE;
		if (method.equals("hotswap")) return Cruelty.Attacks.INVFUCK_HOTSWAP;
		
		return null;
	}
	
	static private boolean attack(Cruelty.Attacks attack, Player player) {
		return Cruelty.attack(attack, player);
	}
}
