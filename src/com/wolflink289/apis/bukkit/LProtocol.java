package com.wolflink289.apis.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;

class LProtocol {
	static private ProtocolManager manager;
	
	/**
	 * Reload the library.
	 */
	static void reload() {
		manager = ProtocolLibrary.getProtocolManager();
		setupHooks();
	}
	
	/**
	 * Disable the library.
	 */
	static void disable() {
		clearHooks();
		manager = null;
	}
	
	/**
	 * Wrapper for PacketContainer.
	 */
	static class Packet {
		protected PacketContainer under;
		
		public Packet(int ID) {
			under = manager.createPacket(ID);
		}
		
		public <T> Mod<T> getModifier(Class<T> type) {
			return new Mod<T>(under.getSpecificModifier(type));
		}
		
		/**
		 * Wrapper for StructureModifier.
		 */
		class Mod<T> {
			protected StructureModifier<T> under;
			
			protected Mod(StructureModifier<T> und) {
				under = und;
			}
			
			Mod<T> write(int index, T value) {
				under.write(index, value);
				return this;
			}
		}
		
		// Specific Ones
		public void x_setup_9(GameMode mode, WorldType type) {
			under.getWorldTypeModifier().write(0, type);
			
			// What a bitch...
			@SuppressWarnings("rawtypes")
			StructureModifier<Enum> enumModifier = under.getSpecificModifier(Enum.class);
			Enum<?> value = (Enum<?>) enumModifier.getField(0).getType().getEnumConstants()[mode.getValue()];
			enumModifier.write(0, value);
		}
	}
	
	static class Dossed {
		int EID;
		long Started;
	}
	
	/**
	 * Send a Packet.
	 */
	static void sendPacket(Player target, Packet packet) throws InvocationTargetException {
		manager.sendServerPacket(target, packet.under);
	}
	
	/**
	 * Setup the hooks for Cruelty.
	 */
	static void setupHooks() {
		// DoS
		manager.addPacketListener(new PacketAdapter(Bukkit.getPluginManager().getPlugin("Cruelty"), ConnectionSide.CLIENT_SIDE, ListenerPriority.NORMAL, Packets.getClientRegistry().values()) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				if (Cruelty.dossing.isEmpty()) return;
				
				try {
					if (Cruelty.dossing.contains(event.getPlayer().getEntityId())) event.setCancelled(true);
				} catch (Exception ex) {}
			}
		});
		
		manager.addPacketListener(new PacketAdapter(Bukkit.getPluginManager().getPlugin("Cruelty"), ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, Packets.getServerRegistry().values()) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (Cruelty.dossing.isEmpty()) return;
				
				try {
					if (Cruelty.dossing.contains(event.getPlayer().getEntityId())) event.setCancelled(true);
				} catch (Exception ex) {}
			}
		});
		
		// Nothingness
		Set<Integer> packets = new HashSet<Integer>();
		packets.add(3);
		packets.add(5);
		packets.add(9);
		packets.add(20);
		packets.add(22);
		packets.add(23);
		packets.add(24);
		packets.add(26);
		packets.add(28);
		packets.add(30);
		packets.add(31);
		packets.add(32);
		packets.add(33);
		packets.add(34);
		packets.add(35);
		packets.add(38);
		packets.add(39);
		packets.add(40);
		packets.add(41);
		packets.add(42);
		packets.add(55);
		packets.add(61);
		packets.add(62);
		
		manager.addPacketListener(new PacketAdapter(Bukkit.getPluginManager().getPlugin("Cruelty"), ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, packets) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (Cruelty.nothinging.isEmpty()) return;
				if (!Cruelty.nothinging.contains(event.getPlayer().getEntityId())) return;
				
				try {
					int id = event.getPacketID();
					
					// Blocked Packets
					if (id == 3 || id == 20 || id == 24 || id == 25 || id == 26 || id == 38 || id == 55) {
						event.setCancelled(true);
						return;
					}
					
					// Respawn
					if (id == 9) {
						event.getPacket().getSpecificModifier(int.class).write(0, 1);
						return;
					}
					
					// Item Entity
					if (id == 23) {
						int type = event.getPacket().getSpecificModifier(int.class).read(9);
						if (type != 50 && type != 70 && type != 71 && type != 90) {
							event.setCancelled(true);
						}
						return;
					}
					
					// Sound Packet
					if (id == 61) {
						int ef = event.getPacket().getSpecificModifier(int.class).read(0);
						
						if (ef > 1007 && ef < 2000) {
							event.setCancelled(true);
						}
						
						return;
					}
					
					if (id == 62) {
						if (event.getPacket().getStrings().read(0).startsWith("mob")) {
							event.setCancelled(true);
						}
						
						return;
					}
					
					// Special Cases
					/*if (id == 22) {
						if (event.getPacket().getSpecificModifier(int.class).read(1) != event.getPlayer().getEntityId()) {
							event.setCancelled(true);
						}
					}*/
					
					// Entity-Ish
					if (event.getPacket().getSpecificModifier(int.class).read(0) != event.getPlayer().getEntityId()) {
						event.setCancelled(true);
					}
					return;
				} catch (Exception ex) {
				}
			}
		});
	}
	
	/**
	 * Clear the hooks for Cruelty.
	 */
	static void clearHooks() {
		manager.removePacketListeners(Bukkit.getPluginManager().getPlugin("Cruelty"));
	}
	
	/**
	 * Get the net.minecraft.server.v[VERSION HERE] package.
	 * 
	 * @return the Minecraft package.
	 */
	static public String getMinecraftPackage() {
		return MinecraftReflection.getMinecraftPackage();
	}
	
	/**
	 * Get the org.bukkit.craftbukkit.v[VERSION HERE] package.
	 * 
	 * @return the CraftBukkit package.
	 */
	static public String getCraftbukkitPackage() {
		return MinecraftReflection.getCraftBukkitPackage();
	}
	
	/**
	 * Get a Minecraft class dynamically.
	 * 
	 * @param name the name of the Minecraft class.
	 * @return the Minecraft class, or null.
	 */
	static public Class<?> getMinecraftClass(String name) {
		try {
			return MinecraftReflection.getMinecraftClass(name);
		} catch (Exception ex) {
			return null;
		}
	}
	
	/**
	 * Get a CraftBukkit class dynamically.
	 * 
	 * @param name the name of the CraftBukkit class.
	 * @return the CraftBukkit class, or null.
	 */
	static public Class<?> getCraftbukkitClass(String name) {
		try {
			// Special Cases
			if (name.equals("entity.CraftPlayer")) return MinecraftReflection.getCraftPlayerClass();
			
			// Get
			return MinecraftReflection.getMinecraftClass(name);
		} catch (Exception ex) {
			return null;
		}
	}
}
