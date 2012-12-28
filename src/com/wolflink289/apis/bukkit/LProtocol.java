package com.wolflink289.apis.bukkit;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;

class LProtocol {
	static private ProtocolManager manager;
	
	/**
	 * Reload the library.
	 */
	static void reload() {
		manager = ProtocolLibrary.getProtocolManager();
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
	
	/**
	 * Send a Packet.
	 */
	static void sendPacket(Player target, Packet packet) throws InvocationTargetException {
		manager.sendServerPacket(target, packet.under);
	}
}
