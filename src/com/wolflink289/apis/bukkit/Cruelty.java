package com.wolflink289.apis.bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.wolflink289.bukkit.cruelty.CrueltyPermissions;
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
		 * Spam the player's speakers with mob death sounds.
		 */
		SCREAM(CrueltyPermissions.SCREAM),
		
		/**
		 * Trick a player's client into thinking they are in the end. While in this state, the player cannot see any other entities. <br>
		 * <b>Dependencies: </b> ProtocolLib
		 */
		NOTHINGNESS(CrueltyPermissions.NOTHINGNESS);
		
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
		
		CrueltyLibs.reload();
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
			if (!attack.isEnabled()) {
				Packet packet1 = new Packet(41);
				packet1.getModifier(int.class).write(0, target.getEntityId());
				packet1.getModifier(byte.class).write(0, (byte) PotionEffectType.CONFUSION.getId());
				packet1.getModifier(byte.class).write(1, (byte) 100);
				packet1.getModifier(short.class).write(0, time);
				
				Packet packet2 = new Packet(41);
				packet2.getModifier(int.class).write(0, target.getEntityId());
				packet2.getModifier(byte.class).write(0, (byte) PotionEffectType.SLOW_DIGGING.getId());
				packet2.getModifier(byte.class).write(1, (byte) 50);
				packet2.getModifier(short.class).write(0, time);
				
				LProtocol.sendPacket(target, packet1);
				LProtocol.sendPacket(target, packet2);
			} else {
				target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, time, 10));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time, 10));
			}
			
			target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time, 10));
			target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 10));
			
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
				packet3.x_setup_9(target.getPlayer().getGameMode(), target.getWorld().getWorldType());
				packet3.getModifier(int.class).write(0, 1).write(1, target.getPlayer().getWorld().getDifficulty().getValue()).write(2, target.getWorld().getMaxHeight());
				
				// Send Packets
				LProtocol.sendPacket(target, packet1);
				LProtocol.sendPacket(target, packet2);
				LProtocol.sendPacket(target, packet3);
				
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
			}
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
			
			// Send Packets
			LProtocol.sendPacket(target, packet1);
			LProtocol.sendPacket(target, packet2);
			LProtocol.sendPacket(target, packet3);
			
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
	static ArrayList<Integer> dossing;
	static ArrayList<Integer> nothinging;
	
	static private final char[] SPAM_NCHRS = "0123456789".toCharArray();
	static private final char[] SPAM_ACHRS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_".toCharArray();
	static private final char[] SPAM_ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_!@#$%^&*()-+=[]{}\\|;:'\",<.>/?~".toCharArray();
}
