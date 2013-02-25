package com.wolflink289.apis.bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.wolflink289.bukkit.cruelty.CrueltyPermissions;
import com.wolflink289.bukkit.cruelty.CrueltyPlugin;
import com.wolflink289.bukkit.cruelty.CrueltyStrings;
import com.wolflink289.apis.bukkit.LProtocol.Packet;
import com.wolflink289.apis.bukkit.CrueltyLibs.Depend;

/**
 * The API for the Cruelty Bukkit plugin found at http://dev.bukkit.org/server-mods/cruelty/
 * 
 * @author Wolflink289
 */
public final class Cruelty {
	
	/**
	 * A constant variable (MUST BE "STATIC PRIVATE FINAL") which tells the compiler to replace the Attacks.CRASH and Attacks.FREEZE methods with "throw new UnsupportedOperationException()"
	 */
	static private final boolean BUKKIT_DEV = false;
	
	/**
	 * An enum containing the available attacks.
	 * 
	 * @author Wolflink289
	 */
	static public enum Attacks {
		/**
		 * Initiate a simulated denial of service attack on a player.
		 */
		DOS(CrueltyPermissions.DOS, new Depend[] { Depend.PROTOCOLLIB }),
		
		/**
		 * Crash a player's client.
		 */
		CRASH(CrueltyPermissions.CRASH),
		
		/**
		 * Trick the player's client into thinking it's dead. <br>
		 * <b>Dependencies: </b> ProtocolLib
		 */
		FEIGN(CrueltyPermissions.FEIGN, new Depend[] { Depend.PROTOCOLLIB }),
		
		/**
		 * Freeze a player's client indefinitely. <br>
		 * <b>Dependencies: </b> ProtocolLib
		 */
		FREEZE(CrueltyPermissions.FREEZE, new Depend[] { Depend.PROTOCOLLIB }),
		
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
		SPAM_ENDLESS(CrueltyPermissions.INVENTORY_FUCK),
		
		/**
		 * Make a player's screen extremely trippy and unplayable. <br>
		 * <b>Soft Dependencies: </b> ProtocolLib
		 */
		TRIP(CrueltyPermissions.TRIP, new Depend[] { Depend.PROTOCOLLIB }),
		
		/**
		 * Make a player see and here creepers everywhere near them. <br>
		 * <b>Soft Dependencies: </b> ProtocolLib
		 */
		PARANOIA(CrueltyPermissions.PARANOIA, new Depend[] { Depend.PROTOCOLLIB }),
		
		/**
		 * Spam the player's speakers with mob death sounds.
		 */
		SCREAM(CrueltyPermissions.SCREAM),
		
		/**
		 * Trick a player's client into thinking they are in the end. While in this state, the player cannot see any other entities. <br>
		 */
		NOTHINGNESS(CrueltyPermissions.NOTHINGNESS, new Depend[] { Depend.PROTOCOLLIB }),
		
		/**
		 * Murder the frame rate of a player's client.
		 */
		LAG(CrueltyPermissions.LAG, new Depend[] { Depend.PROTOCOLLIB }),
		
		/**
		 * There is a chance that world actions and commands will be cancelled.
		 */
		DENY(CrueltyPermissions.DENY),
		
		/**
		 * Play the annoying door and chest opening and closing sounds until the player goes insane.
		 */
		ANNOY(CrueltyPermissions.ANNOY);
		
		private CrueltyPermissions perm;
		private Depend[] depends;
		
		private Attacks(CrueltyPermissions permission) {
			perm = permission;
		}
		
		private Attacks(CrueltyPermissions permission, Depend[] dependencies) {
			perm = permission;
			depends = dependencies;
		}
		
		/**
		 * Check if the player is immune to the attack.
		 * 
		 * @return whether they are immune or not.
		 */
		public boolean isImmune(Player player) {
			return perm.hasImmunity(player);
		}
		
		/**
		 * Check if the attack is enabled. The only reason why the attack would be disabled is if a dependency is missing.
		 * 
		 * @return If the attack is enabled.
		 */
		public boolean isEnabled() {
			if (depends == null) return true;
			for (int i = 0; i < depends.length; i++) {
				if (!depends[i].check()) return false;
			}
			
			return true;
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
		try {
			return doAttack(attack, target, true);
		} catch (InvocationTargetException ex) {
			System.err.println("[Cruelty] Error sending packet:");
			ex.printStackTrace();
			return false;
		}
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
		try {
			return doAttack(attack, target, false);
		} catch (InvocationTargetException ex) {
			System.err.println("[Cruelty] Error sending packet:");
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Attempt to load the dependencies.
	 */
	static public void reload() {
		dossing = new ArrayList<Integer>();
		nothinging = new ArrayList<Integer>();
		denied = new ArrayList<Integer>();
		
		if (crueltylistener == null) crueltylistener = new CrueltyListener();
		
		CrueltyLibs.reload();
		CrueltyPlugin.instance.getServer().getPluginManager().registerEvents(crueltylistener, CrueltyPlugin.instance);
	}
	
	/**
	 * Disable the hooks used by the plugin.
	 * 
	 * @deprecated ONLY THIS PLUGIN SHOULD CALL THIS METHOD.
	 */
	static public void disable() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if (elements.length < 2) return;
		if (!elements[2].equals("com.wolflink289.bukkit.cruelty.CrueltyPlugin")) return;
		
		CrueltyLibs.disable();
	}
	
	/**
	 * <b>PRIVATE: </b>Do the attack.
	 */
	static private boolean doAttack(Attacks attack, Player target, boolean canbeimmune) throws InvocationTargetException {
		// Setup
		if (!CrueltyLibs.dependencies_loaded) reload();
		
		// Attack
		if (attack == Attacks.FREEZE) {
			/*
			 * The following is handled by the java "preprocessor".
			 * If the constant "BUKKIT_DEV" is set to false, the attack will take place.
			 */
			if (BUKKIT_DEV) {
				throw new UnsupportedOperationException("Bukkit Dev does not allow use of this feature.");
			} else {
				// Dependency check
				if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
				
				// Immunity + Cast Checks
				if (canbeimmune && attack.isImmune(target)) return false;
				
				// Action - Generate Packet
				LProtocol.Packet packet = new LProtocol.Packet(60);
				packet.getModifier(double.class).write(0, target.getLocation().getX()).write(1, target.getLocation().getY()).write(2, target.getLocation().getZ());
				packet.getModifier(float.class).write(0, 20f).write(1, (float) target.getLocation().getX()).write(2, (float) target.getLocation().getY()).write(3, (float) target.getLocation().getZ());
				
				// Action - Send Packets
				for (int j = 0; j < 100; j++)
					LProtocol.sendPacket(target, packet);
				
				// Clean
				packet = null;
				return true;
			}
		}
		if (attack == Attacks.FEIGN) {
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
			// Immunity + Cast Checks
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Generate Packet
			Packet packet1 = new Packet(38);
			packet1.getModifier(int.class).write(0, target.getEntityId());
			packet1.getModifier(byte.class).write(0, (byte) 2);
			
			Packet packet2 = new Packet(8);
			packet2.getModifier(int.class).write(0, 0).write(0, 0);
			packet2.getModifier(float.class).write(0, 0f);
			
			// Action - Send Packets
			LProtocol.sendPacket(target, packet1);
			LProtocol.sendPacket(target, packet2);
			
			// Clean
			packet1 = null;
			packet2 = null;
			return true;
		}
		if (attack == Attacks.CRASH) {
			/*
			 * The following is handled by the java "preprocessor".
			 * If the constant "BUKKIT_DEV" is set to false, the attack will take place.
			 */
			if (BUKKIT_DEV) {
				throw new UnsupportedOperationException("Bukkit Dev does not allow use of this feature.");
			} else {
				// Dependency check
				if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
				
				// Immunity Check
				if (canbeimmune && attack.isImmune(target)) return false;
				
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
		}
		if (attack == Attacks.INVFUCK_SCRAMBLE) {
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
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
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
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
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Spam
			new Thread("Cruelty: SPAM (" + target.getName() + ")") {
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
			return true;
		}
		if (attack == Attacks.DOS) {
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Dos
			dossing.add(target.getEntityId());
			
			// Return
			return true;
		}
		if (attack == Attacks.TRIP) {
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Trip
			short time = 20 * 60 * 5;
			if (attack.isEnabled()) {
				Packet packet1 = new Packet(41);
				packet1.getModifier(int.class).write(0, target.getEntityId());
				packet1.getModifier(byte.class).write(0, (byte) PotionEffectType.CONFUSION.getId());
				packet1.getModifier(byte.class).write(1, (byte) 100);
				packet1.getModifier(short.class).write(0, time);
				
				Packet packet2 = new Packet(41);
				packet2.getModifier(int.class).write(0, target.getEntityId());
				packet2.getModifier(byte.class).write(0, (byte) PotionEffectType.SLOW_DIGGING.getId());
				packet2.getModifier(byte.class).write(1, (byte) 35);
				packet2.getModifier(short.class).write(0, time);
				
				LProtocol.sendPacket(target, packet1);
				LProtocol.sendPacket(target, packet2);
			} else {
				target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, time, 10));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time, 10));
			}
			
			target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time, 10));
			target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 10));
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, 2));
			
			// Return
			return true;
		}
		if (attack == Attacks.SCREAM) {
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Ear Rape
			target.playSound(target.getLocation(), Sound.GHAST_SCREAM, 10f, 1f);
			target.playSound(target.getLocation(), Sound.GHAST_SCREAM2, 10f, 1f);
			target.playSound(target.getLocation(), Sound.GHAST_MOAN, 10f, 1f);
			target.playSound(target.getLocation(), Sound.GHAST_DEATH, 10f, 1f);
			target.playSound(target.getLocation(), Sound.BAT_DEATH, 10f, 1f);
			target.playSound(target.getLocation(), Sound.ANVIL_BREAK, 10f, 1f);
			target.playSound(target.getLocation(), Sound.BLAZE_DEATH, 10f, 1f);
			target.playSound(target.getLocation(), Sound.CAT_HISS, 10f, 1f);
			target.playSound(target.getLocation(), Sound.CREEPER_DEATH, 10f, 1f);
			target.playSound(target.getLocation(), Sound.CREEPER_HISS, 10f, 1f);
			target.playSound(target.getLocation(), Sound.ENDERDRAGON_GROWL, 10f, 1f);
			target.playSound(target.getLocation(), Sound.ENDERDRAGON_DEATH, 10f, 1f);
			target.playSound(target.getLocation(), Sound.EXPLODE, 10f, 1f);
			target.playSound(target.getLocation(), Sound.ENDERMAN_SCREAM, 10f, 1f);
			target.playSound(target.getLocation(), Sound.SILVERFISH_KILL, 10f, 1f);
			
			// Return
			return true;
		}
		if (attack == Attacks.NOTHINGNESS) {
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
			// Immunity + Cast Checks
			if (canbeimmune && attack.isImmune(target)) return false;
			
			try {
				// Add
				nothinging.add(target.getEntityId());
				
				// Feign
				Packet packet1 = new Packet(38);
				packet1.getModifier(int.class).write(0, target.getEntityId());
				packet1.getModifier(byte.class).write(0, (byte) 2);
				
				Packet packet2 = new Packet(8);
				packet2.getModifier(int.class).write(0, 0).write(0, 0);
				packet2.getModifier(float.class).write(0, 0f);
				
				Packet packet3 = new Packet(9);
				packet3.x_setup_9(GameMode.CREATIVE, target.getWorld().getWorldType());
				packet3.getModifier(int.class).write(0, -1).write(1, target.getWorld().getDifficulty().getValue()).write(2, target.getWorld().getMaxHeight());
				
				Packet packet5 = new Packet(8);
				packet5.getModifier(int.class).write(0, target.getHealth()).write(1, target.getFoodLevel());
				packet5.getModifier(float.class).write(0, target.getSaturation());
				
				Packet packet6 = new Packet(43);
				packet6.getModifier(int.class).write(0, (int) target.getExp()).write(1, target.getLevel());
				packet6.getModifier(float.class).write(0, target.getExp());
				
				// Send Packets
				LProtocol.sendPacket(target, packet1);
				LProtocol.sendPacket(target, packet2);
				LProtocol.sendPacket(target, packet3);
				LProtocol.sendPacket(target, packet5);
				LProtocol.sendPacket(target, packet6);
				
				// Resend Inventory
				resendInventory(target);
				
				// Resend Chunks
				Chunk[][] send = new Chunk[33][33];
				Chunk[] sends = new Chunk[32 * 32 + 1];
				
				// Get
				int pcx = target.getLocation().getChunk().getX();
				int pcy = target.getLocation().getChunk().getZ();
				int hf = 16;
				for (int i = -hf; i < hf; i++) {
					for (int j = -hf; j < hf; j++) {
						send[i + hf][j + hf] = target.getWorld().getChunkAt(pcx + i, pcy + j);
					}
				}
				
				// Spiral
				int x = 0, y = 0, dx = 0, dy = -1;
				int t = 33;
				int origin = (int) Math.floor((t - 1) / 2d);
				int maxI = (t - 1) * (t - 1);
				
				for (int i = 0; i < maxI; i++) {
					if ((-33 / 2 < x && x <= 33 / 2) && (-33 / 2 < y && y <= 33 / 2)) {
						sends[i] = send[origin + x][origin + y];
					}
					
					if ((x == y) || ((x < 0) && (x == -y)) || ((x > 0) && (x == 1 - y))) {
						t = dx;
						dx = -dy;
						dy = t;
					}
					x += dx;
					y += dy;
				}
				
				// Send Chunks
				resendChunks(target, sends);
				
				// Respawn
				Packet packet4 = new Packet(13);
				packet4.getModifier(double.class).write(0, target.getLocation().getX()).write(1, target.getLocation().getY()).write(2, target.getLocation().getZ()).write(3, target.getLocation().getY() + 1.62d);
				packet4.getModifier(float.class).write(0, target.getLocation().getYaw()).write(1, target.getLocation().getPitch());
				packet4.getModifier(boolean.class).write(0, !target.isFlying());
				
				// Send Respawn
				LProtocol.sendPacket(target, packet4);
				
				// Clean
				send = null;
				packet1 = null;
				packet2 = null;
				packet3 = null;
				packet4 = null;
				return true;
			} catch (Exception ex) {
				nothinging.remove((Object) target.getEntityId());
				return false;
			}
		}
		
		if (attack == Attacks.PARANOIA) {
			// Immunity + Cast Checks
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Start
			new Thread("Cruelty: PARANOIA (" + target.getName() + ")") {
				private boolean plib;
				private Player target;
				
				@Override
				public void run() {
					Random rand = new Random(System.currentTimeMillis());
					while (target.isOnline()) {
						try {
							// Play Sound
							Location tloc = target.getLocation();
							Location sloc = new Location(tloc.getWorld(), tloc.getX(), tloc.getY(), tloc.getZ());
							sloc.setX(sloc.getX() + (rand.nextInt(8) - 4));
							sloc.setY(sloc.getY() + (rand.nextInt(8) - 4));
							sloc.setZ(sloc.getZ() + (rand.nextInt(8) - 4));
							double dist = Math.abs(tloc.getX() - sloc.getX()) + Math.abs(tloc.getY() - sloc.getY()) + Math.abs(tloc.getZ() - sloc.getZ());
							
							target.playSound(sloc, Sound.FUSE, (float) (32d / dist), 1f);
							target.playEffect(sloc, Effect.SMOKE, 4);
							
							// Show Creeper
							if (plib) {
								// TODO
							}
							
							// Wait
							Thread.sleep(rand.nextInt(1000 * 7) + 500); // 0.5-7.5 seconds
						} catch (Exception ex) {}
					}
				}
				
				public void start(Player target, boolean plib) {
					this.target = target;
					this.plib = plib;
					setDaemon(true);
					start();
				}
			}.start(target, attack.isEnabled());
			return true;
		}
		
		if (attack == Attacks.DENY) {
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Deny
			denied.add(target.getEntityId());
			
			// Return
			return true;
		}
		
		if (attack == Attacks.LAG) {
			/*
			 * The following is handled by the java "preprocessor".
			 * If the constant "BUKKIT_DEV" is set to false, the attack will take place.
			 */
			if (BUKKIT_DEV) {
				throw new UnsupportedOperationException("Bukkit Dev does not allow use of this feature.");
			} else {
				// Dependency check
				if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
				
				// Immunity Check
				if (canbeimmune && attack.isImmune(target)) return false;
				
				// Action - Lag: Set surrounding to portal
				World bwd = target.getWorld();
				for (int x = 0; x < 65; x++) {
					for (int z = 0; z < 65; z++) {
						for (int y = 4; y < 9; y++) {
							if (bwd.getBlockTypeIdAt(target.getLocation().getBlockX() + 32 + x, target.getLocation().getBlockY() + y, target.getLocation().getBlockZ() + 32 + z) == 0) {
								target.sendBlockChange(new Location(bwd, target.getLocation().getBlockX() + 16 + x, target.getLocation().getBlockY() + y, target.getLocation().getBlockZ() + 16 + z), Material.ENDER_PORTAL, (byte) 0);
							}
						}
					}
				}
				
				// Return
				return true;
			}
		}
		
		if (attack == Attacks.ANNOY) {
			// Dependency check
			if (!attack.isEnabled()) throw new RuntimeException("Missing dependency.");
			
			// Immunity Check
			if (canbeimmune && attack.isImmune(target)) return false;
			
			// Action - Annoy
			new Thread("Cruelty: ANNOY (" + target.getName() + ")") {
				private Player target;
				
				@Override
				public void run() {
					Random rand = new Random(System.currentTimeMillis());
					while (target.isOnline()) {
						try {
							// Play Sound
							Location tloc = target.getLocation();
							
							// Get sound locations
							Location door = new Location(tloc.getWorld(), tloc.getX(), tloc.getY(), tloc.getZ());
							door.setX(door.getX() + (rand.nextInt(16) - 8));
							door.setY(door.getY() + (rand.nextInt(16) - 8));
							door.setZ(door.getZ() + (rand.nextInt(16) - 8));
							
							Location chest = new Location(tloc.getWorld(), tloc.getX(), tloc.getY(), tloc.getZ());
							chest.setX(chest.getX() + (rand.nextInt(16) - 8));
							chest.setY(chest.getY() + (rand.nextInt(16) - 8));
							chest.setZ(chest.getZ() + (rand.nextInt(16) - 8));
							
							// Play door
							target.playSound(door, Sound.DOOR_OPEN, 1f, 1f);
							Thread.sleep(250);
							
							// Play chest
							target.playSound(chest, Sound.CHEST_OPEN, 1f, 1f);
							Thread.sleep(250);
							
							// Play door
							target.playSound(door, Sound.DOOR_CLOSE, 1f, 1f);
							Thread.sleep(500);
							
							// Get sound location
							door = new Location(tloc.getWorld(), tloc.getX(), tloc.getY(), tloc.getZ());
							door.setX(door.getX() + (rand.nextInt(16) - 8));
							door.setY(door.getY() + (rand.nextInt(16) - 8));
							door.setZ(door.getZ() + (rand.nextInt(16) - 8));
							
							// Play door
							target.playSound(door, Sound.DOOR_OPEN, 1f, 1f);
							Thread.sleep(250);
							
							// Play chest
							target.playSound(chest, Sound.CHEST_CLOSE, 1f, 1f);
							Thread.sleep(250);
							
							// Play door
							target.playSound(door, Sound.DOOR_CLOSE, 1f, 1f);
							Thread.sleep(500);
						} catch (Exception ex) {}
					}
				}
				
				public void start(Player target) {
					this.target = target;
					setDaemon(true);
					start();
				}
			}.start(target);
			return true;
		}
		return false;
	}
	
	static final void restoreNothingness(Player target) {
		try {
			// Remove
			nothinging.remove((Object) target.getEntityId());
			
			// Feign
			Packet packet1 = new Packet(38);
			packet1.getModifier(int.class).write(0, target.getEntityId());
			packet1.getModifier(byte.class).write(0, (byte) 2);
			
			Packet packet2 = new Packet(8);
			packet2.getModifier(int.class).write(0, 0).write(0, 0);
			packet2.getModifier(float.class).write(0, 0f);
			
			Packet packet3 = new Packet(9);
			packet3.x_setup_9(target.getPlayer().getGameMode(), target.getWorld().getWorldType());
			packet3.getModifier(int.class).write(0, target.getWorld().getEnvironment().getId()).write(1, target.getPlayer().getWorld().getDifficulty().getValue()).write(2, target.getWorld().getMaxHeight());
			
			Packet packet5 = new Packet(8);
			packet5.getModifier(int.class).write(0, target.getHealth()).write(1, target.getFoodLevel());
			packet5.getModifier(float.class).write(0, target.getSaturation());

			Packet packet6 = new Packet(43);
			packet6.getModifier(int.class).write(0, (int) target.getExp()).write(1, target.getLevel());
			packet6.getModifier(float.class).write(0, target.getExp());
			
			// Send Packets
			LProtocol.sendPacket(target, packet1);
			LProtocol.sendPacket(target, packet2);
			LProtocol.sendPacket(target, packet3);
			LProtocol.sendPacket(target, packet5);
			LProtocol.sendPacket(target, packet6);
			
			// Resend Inventory
			resendInventory(target);
			
			// Resend Chunks
			Chunk[][] send = new Chunk[33][33];
			Chunk[] sends = new Chunk[32 * 32 + 1];
			
			// Get
			int pcx = target.getLocation().getChunk().getX();
			int pcy = target.getLocation().getChunk().getZ();
			int hf = 16;
			for (int i = -hf; i < hf; i++) {
				for (int j = -hf; j < hf; j++) {
					send[i + hf][j + hf] = target.getWorld().getChunkAt(pcx + i, pcy + j);
				}
			}
			
			// Spiral
			int x = 0, y = 0, dx = 0, dy = -1;
			int t = 33;
			int origin = (int) Math.floor((t - 1) / 2d);
			int maxI = (t - 1) * (t - 1);
			
			for (int i = 0; i < maxI; i++) {
				if ((-33 / 2 < x && x <= 33 / 2) && (-33 / 2 < y && y <= 33 / 2)) {
					sends[i] = send[origin + x][origin + y];
				}
				
				if ((x == y) || ((x < 0) && (x == -y)) || ((x > 0) && (x == 1 - y))) {
					t = dx;
					dx = -dy;
					dy = t;
				}
				x += dx;
				y += dy;
			}
			
			// Send Chunks
			resendChunks(target, sends);
			
			// Respawn
			Packet packet4 = new Packet(13);
			packet4.getModifier(double.class).write(0, target.getLocation().getX()).write(1, target.getLocation().getY()).write(2, target.getLocation().getZ()).write(3, target.getLocation().getY() + 1.62d);
			packet4.getModifier(float.class).write(0, target.getLocation().getYaw()).write(1, target.getLocation().getPitch());
			packet4.getModifier(boolean.class).write(0, !target.isFlying());
			
			// Send Respawn
			LProtocol.sendPacket(target, packet4);
			
			// Clean
			send = null;
			packet1 = null;
			packet2 = null;
			packet3 = null;
			packet4 = null;
		} catch (Exception ex) {}
	}
	
	static private final void resendInventory(Player target) {
		try {
			target.closeInventory();
			PlayerInventory i = target.getInventory();
			
			// Prepare reflection
			Class<?> class_CraftPlayer = LProtocol.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> class_CraftItemStack = LProtocol.getCraftbukkitClass("inventory.CraftItemStack");
			Class<?> class_Packet103SetSlot = LProtocol.getMinecraftClass("Packet103SetSlot");
			Class<?> class_PlayerConnection = LProtocol.getMinecraftClass("PlayerConnection");
			Class<?> class_ItemStack = LProtocol.getMinecraftClass("ItemStack");
			Class<?> class_Packet = LProtocol.getMinecraftClass("Packet");
			Class<?> class_EntityPlayer = LProtocol.getMinecraftClass("EntityPlayer");
			
			Method methd_getHandle = class_CraftPlayer.getMethod("getHandle");
			Method methd_asNMSCopy = class_CraftItemStack.getMethod("asNMSCopy", ItemStack.class);
			Method methd_sendPacket = class_PlayerConnection.getMethod("sendPacket", class_Packet);
			
			Constructor<?> const_Packet103SetSlot = class_Packet103SetSlot.getConstructor(int.class, int.class, class_ItemStack);
			
			Field field_playerConnection = class_EntityPlayer.getField("playerConnection");
			
			// Prepare
			Object playerConnection = field_playerConnection.get(methd_getHandle.invoke(target));
			
			// Main
			for (int j = 0; j < i.getSize(); j++) {
				ItemStack it = i.getItem(j);
				if (it == null) continue;
				
				Object ist = methd_asNMSCopy.invoke(null, it);
				Object pak;
				if (j < 9) {
					pak = const_Packet103SetSlot.newInstance(0, j + 36, ist);
				} else {
					pak = const_Packet103SetSlot.newInstance(0, j, ist);
				}
				
				methd_sendPacket.invoke(playerConnection, pak);
			}
			
			// Armor
			ItemStack[] a = i.getArmorContents();
			for (int j = 0; j < a.length; j++) {
				ItemStack it = a[a.length - 1 - j];
				if (it == null) continue;

				Object ist = methd_asNMSCopy.invoke(null, it);
				Object pak = const_Packet103SetSlot.newInstance(0, j + 5, ist);
				
				methd_sendPacket.invoke(playerConnection, pak);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static private final void resendChunks(Player target, Chunk... chunks) {
		try {
			// Prepare reflection
			Class<?> class_CraftPlayer = LProtocol.getCraftbukkitClass("entity.CraftPlayer");
			Class<?> class_ChunkCoordIntPair = LProtocol.getMinecraftClass("ChunkCoordIntPair");
			Class<?> class_EntityPlayer = LProtocol.getMinecraftClass("EntityPlayer");
			
			Constructor<?> const_ChunkCoordIntPair = class_ChunkCoordIntPair.getConstructor(int.class, int.class);
			
			Method methd_getHandle = class_CraftPlayer.getMethod("getHandle");
			
			Field field_chunkCoordIntPairQueue = class_EntityPlayer.getField("chunkCoordIntPairQueue");
			
			// Send all chunks
			for (int i = 0; i < chunks.length; i++) {
				if (chunks[i] == null) continue;
				
				// Resend with reflection
				Object location = const_ChunkCoordIntPair.newInstance(chunks[i].getX(), chunks[i].getZ());
				Object player = methd_getHandle.invoke(target);
				
				List queue = (List) field_chunkCoordIntPairQueue.get(player);
				queue.add(location);
			}
			
			// Based off of:
			// ChunkCoordIntPair location = new ChunkCoordIntPair(i, j);
			// ((CraftPlayer) player).getHandle().chunkCoordIntPairQueue.add(location);
		} catch (Exception ex) {
			throw new RuntimeException();
		}
	}
	
	// Resources
	static private CrueltyListener crueltylistener;
	static boolean hide_all_entities = true;
	static ArrayList<Integer> dossing;
	static ArrayList<Integer> nothinging;
	static ArrayList<Integer> denied;
	
	static private final char[] SPAM_NCHRS = "0123456789".toCharArray();
	static private final char[] SPAM_ACHRS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_".toCharArray();
	static private final char[] SPAM_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_!@#$%^&*()-+=[]{}\\|;:'\",<.>/?~".toCharArray();
}
