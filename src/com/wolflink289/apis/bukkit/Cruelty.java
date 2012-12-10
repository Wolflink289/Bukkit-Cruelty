package com.wolflink289.apis.bukkit;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet60Explosion;
import net.minecraft.server.Packet8UpdateHealth;
import net.minecraft.server.Vec3D;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.wolflink289.bukkit.cruelty.CrueltyPermissions;
import com.wolflink289.bukkit.cruelty.CrueltyStrings;

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
		FREEZE(CrueltyPermissions.FREEZE),
		
		/**
		 * Fuck with the player's inventory:<br>
		 * Scramble the player's inventory but not hotbar.
		 */
		INVFUCK_SCRAMBLE(CrueltyPermissions.INVENTORY_FUCK),
		
		/**
		 * Fuck with the player's inventory:<br>
		 * Scramble the player's hotbar.
		 */
		INVFUCK_HOTSWAP(CrueltyPermissions.INVENTORY_FUCK),
		
		/**
		 * Spam a player's chat with fake bots:<br>
		 * Spam the player for a relatively short amount of time. (45-120 seconds)
		 */
		SPAM(CrueltyPermissions.INVENTORY_FUCK),
		
		/**
		 * Spam a player's chat with fake bots:<br>
		 * Spam the player until they log out.
		 */
		SPAM_ENDLESS(CrueltyPermissions.INVENTORY_FUCK);
		
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
		if (attack == Attacks.INVFUCK_SCRAMBLE) {
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Scramble
			Random rand = new Random(System.currentTimeMillis());
			ItemStack[] cont = target.getInventory().getContents();
			ItemStack[] scont = new ItemStack[cont.length];
			
			for (int i = 0; i < 9; i++) {
				scont[i] = cont[i];
			}
			
			int t = rand.nextInt(scont.length - 9) + 9;
			for (int i = 9; i < cont.length; i++) {
				while (scont[t] != null) {
					t = rand.nextInt(scont.length - 9) + 9;
				}
				
				scont[t] = cont[i];
			}
			
			target.getInventory().setContents(scont);
			
			// Clean
			cont = null;
			scont = null;
			rand = null;
			return true;
		}
		if (attack == Attacks.INVFUCK_HOTSWAP) {
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Scramble
			Random rand = new Random(System.currentTimeMillis());
			ItemStack[] cont = target.getInventory().getContents();
			ItemStack[] scont = new ItemStack[cont.length];
			
			for (int i = 9; i < cont.length; i++) {
				scont[i] = cont[i];
			}
			
			int t = rand.nextInt(9);
			for (int i = 0; i < 9; i++) {
				while (scont[t] != null) {
					t = rand.nextInt(9);
				}
				
				scont[t] = cont[i];
			}
			
			target.getInventory().setContents(scont);
			
			// Clean
			cont = null;
			scont = null;
			rand = null;
			return true;
		}
		if (attack == Attacks.SPAM || attack == Attacks.SPAM_ENDLESS) {
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Spam
			new Thread() {
				protected Player target;
				protected Random rand;
				protected boolean endless;
				
				@Override
				public void run() {
					// Create Random
					rand = new Random(System.currentTimeMillis());
					
					// Determine
					long end = System.currentTimeMillis() + (rand.nextInt(120 - 45) + 45) * 1000;
					long lag = rand.nextInt(500) + 75;
					int num = rand.nextInt(15) + 1;
					String[] bots = new String[num];
					
					// Name
					int scheme = rand.nextInt(3);
					int misc = 0;
					for (int i = 0; i < bots.length; i++) {
						switch (scheme) {
							default:
								// Any character goes
								int botnl = rand.nextInt(16 - 3) + 3;
								bots[i] = "";
								for (int j = 0; j < botnl; j++) {
									bots[i] += SPAM_ACHRS[rand.nextInt(SPAM_ACHRS.length)];
								}
								break;
							case 1:
								// Ordered, Offset Few
								misc += rand.nextInt(5) == 2 ? 1 : 2;
								bots[i] = "Bot_" + misc;
								break;
							case 2:
								// Random Numbers
								bots[i] = "";
								for (int j = 0; j < 16; j++) {
									bots[i] += SPAM_NCHRS[rand.nextInt(SPAM_NCHRS.length)];
								}
								break;
						}
					}
					
					// "Join"
					for (int i = 0; i < bots.length; i++) {
						target.sendMessage(CrueltyStrings.MSG_DO_SPAM_JOIN.replace("${NAME}", bots[i]));
						
						try {
							Thread.sleep(lag + (rand.nextInt(100) - 50));
						} catch (Exception ex) {}
					}
					
					// Loop
					String[] msgs = new String[bots.length];
					int len;
					scheme = rand.nextInt(2);
					while ((endless || System.currentTimeMillis() < end) && target.isOnline()) {
						// Create Message
						for (int i = 0; i < msgs.length; i++) {
							len = rand.nextInt(100 - 3) + 3;
							msgs[i] = "";
							if (scheme == 0) {
								for (int j = 0; j < len; j++) {
									msgs[i] += SPAM_NCHRS[rand.nextInt(SPAM_NCHRS.length)];
								}
							} else {
								for (int j = 0; j < len; j++) {
									msgs[i] += SPAM_ALL[rand.nextInt(SPAM_ALL.length)];
								}
							}
						}
						
						// Speak
						for (int i = 0; i < bots.length; i++) {
							if (rand.nextInt(bots.length) == 0) continue;
							
							// Speak
							target.sendMessage(CrueltyStrings.MSG_DO_SPAM_SPEAK.replace("${NAME}", bots[i]).replace("${MESSAGE}", msgs[i]));
							
							// Wait
							try {
								Thread.sleep((lag / 4) + (rand.nextInt(10) - 5));
							} catch (Exception ex) {}
						}
						
						// Wait
						try {
							Thread.sleep((lag / 2) + (rand.nextInt(20) - 10));
						} catch (Exception ex) {}
					}
					
					// "Leave"
					if (target.isOnline()) {
						for (int i = 0; i < bots.length; i++) {
							target.sendMessage(CrueltyStrings.MSG_DO_SPAM_LEAVE.replace("${NAME}", bots[i]));
							
							try {
								Thread.sleep(lag + (rand.nextInt(100) - 50));
							} catch (Exception ex) {}
						}
					}
					
					// Clean
					rand = null;
					target = null;
				}
				
				public void start(Player target, boolean endless) {
					this.target = target;
					this.endless = endless;
					setDaemon(true);
					start();
				}
			}.start(target, attack == Attacks.SPAM_ENDLESS);
			
			// Clean
			target = null;
		}
		
		return false;
	}
	
	// Resources
	static private final char[] SPAM_NCHRS = "0123456789".toCharArray();
	static private final char[] SPAM_ACHRS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_".toCharArray();
	static private final char[] SPAM_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_!@#$%^&*()-+=[]{}\\|;:'\",<.>/?~".toCharArray();
}
