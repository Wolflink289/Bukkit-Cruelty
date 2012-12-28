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

public class FeignCommand extends BukkitCommand {
	
	@Override
	protected void handle(BukkitSender sender, String[] params) {
		if (!CrueltyPermissions.FEIGN.canUse(sender)) {
			CrueltyPlugin.noPermission(sender);
			return;
		}

		if (!Cruelty.Attacks.FEIGN.isEnabled()) {
			sender.getSender().sendMessage(CrueltyStrings.MSG_ERR_DEPEND);
			return;
		}
		
		if (params.length != 1) {
			sender.getSender().sendMessage(CrueltyStrings.MSG_USG_FEIGN); // Send without prefix
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
				boolean ats = attack(targets[i]);
				if (ats) suc++;
				sb.append(ats ? CrueltyStrings.PFX_SUCCESS : CrueltyStrings.PFX_IMMUNE);
				sb.append(targets[i].getName());
			}
		}
		
		sender.sendMessage(CrueltyStrings.MSG_ACT_FEIGN.replace("${COUNT}", String.valueOf(suc)).replace("${PLAYERS}", sb.toString()));
		
		sb = null;
		targets = null;
		targetsl = null;
	}
	
	static private boolean attack(Player player) {
		return Cruelty.attack(Cruelty.Attacks.FEIGN, player);
	}
}
