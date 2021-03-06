package com.wolflink289.bukkit.cruelty;

import org.bukkit.entity.Player;
import com.wolflink289.bukkit.util.BukkitSender;

public enum CrueltyPermissions {
	DOS("cruelty.X.dos"), CRASH("cruelty.X.crash"), FEIGN("cruelty.X.feign"), FREEZE("cruelty.X.freeze"), INVENTORY_FUCK("cruelty.X.invfuck"), SPAM("cruelty.X.spam"), TRIP("cruelty.X.trip"), SCREAM("cruelty.X.scream"), NOTHINGNESS("cruelty.X.nothingness"), PARANOIA("cruelty.X.paranoia"), LAG("cruelty.X.lag"), DENY("cruelty.X.deny"), ANNOY("cruelty.X.annoy");
	
	/**
	 * Does the target have immunity?
	 * 
	 * @param target the sender.
	 * @return whether they have immunity.
	 */
	public boolean hasImmunity(BukkitSender target) {
		return target.hasPermission(permission.replace("X", "immune"));
	}
	
	/**
	 * Does the target have immunity?
	 * 
	 * @param target the sender.
	 * @return whether they have immunity.
	 */
	public boolean hasImmunity(Player target) {
		return target.hasPermission(permission.replace("X", "immune"));
	}
	
	/**
	 * Does the sender have the ability to use?
	 * 
	 * @param sender the sender.
	 * @return whether they have the permission to use it.
	 */
	public boolean canUse(BukkitSender sender) {
		return sender.hasPermission(permission.replace("X", "attack"));
	}
	
	private String permission;
	
	private CrueltyPermissions(String permission) {
		this.permission = permission;
	}
}
