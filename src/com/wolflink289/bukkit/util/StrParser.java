package com.wolflink289.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The enclosing class for all useful string parsers.
 * 
 * @author Wolflink289
 */
public class StrParser {
	
	/**
	 * A list parser.
	 * 
	 * @author Wolflink289
	 */
	static public class LIST {
		
		/**
		 * Parse the list.
		 * 
		 * @param csl the list separated by commas.
		 * @return the list split into a string array.
		 */
		static public String[] parse(String csl) {
			if (csl.trim().equals("*")) {
				Player[] li = Bukkit.getOnlinePlayers();
				String[] lit = new String[li.length];
				for (int i = 0; i < li.length; i++) {
					lit[i] = li[i].getName() + ", ";
				}
				
				li = null;
				return lit;
			}
			
			if (!csl.contains(",")) return new String[] { csl };
			return csl.split(",");
		}
		
		/**
		 * Parse the list and get a player object for each.
		 * 
		 * @param csl the list separated by commas.
		 * @return the list as player objects.
		 */
		static public Player[] parseAsPlayers(String csl) {
			if (csl.trim().equals("*")) { return Bukkit.getOnlinePlayers(); }
			
			String[] players = parse(csl);
			Player[] playersp = new Player[players.length];
			
			for (int i = 0; i < players.length; i++) {
				if (players[i].startsWith("@")) {
					playersp[i] = Bukkit.getPlayerExact(players[i].substring(1));
				} else {
					playersp[i] = Bukkit.getPlayer(players[i]);
				}
			}
			
			players = null;
			return playersp;
		}
	}
}
