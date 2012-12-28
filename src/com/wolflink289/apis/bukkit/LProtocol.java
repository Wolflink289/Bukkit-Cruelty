package com.wolflink289.apis.bukkit;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
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
		manager.addPacketListener(new PacketAdapter(Bukkit.getPluginManager().getPlugin("Cruelty"), ConnectionSide.CLIENT_SIDE, ListenerPriority.NORMAL, Packets.getClientRegistry().values()) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				if (Cruelty.dossing.isEmpty()) return;
				if (Cruelty.dossing.contains(event.getPlayer().getEntityId())) event.setCancelled(true);
			}
		});
		
		manager.addPacketListener(new PacketAdapter(Bukkit.getPluginManager().getPlugin("Cruelty"), ConnectionSide.SERVER_SIDE, ListenerPriority.NORMAL, Packets.getServerRegistry().values()) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (Cruelty.dossing.isEmpty()) return;
				if (Cruelty.dossing.contains(event.getPlayer().getEntityId())) event.setCancelled(true);
			}
		});
	}
	
	/**
	 * Clear the hooks for Cruelty.
	 */
	static void clearHooks() {
		manager.removePacketListeners(Bukkit.getPluginManager().getPlugin("Cruelty"));
	}
}
