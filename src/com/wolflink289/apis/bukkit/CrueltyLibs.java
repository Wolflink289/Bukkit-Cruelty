package com.wolflink289.apis.bukkit;

import com.comphenix.protocol.ProtocolManager;
import com.wolflink289.bukkit.cruelty.CrueltyPlugin;

class CrueltyLibs {
	static ProtocolManager d_protocol;
	
	static boolean[] dependencies = new boolean[1];
	static boolean dependencies_loaded = false;
	
	/**
	 * <b>PRIVATE: </b> Dependencies
	 */
	static enum Depend {
		PROTOCOLLIB;
		
		private Depend() {
			dependencies[this.ordinal()] = false;
		}
		
		public boolean check() {
			if (!dependencies_loaded) reload();
			return dependencies[this.ordinal()];
		}
	}
	
	static void disable() {
		dependencies_loaded = false;
		
		// ProtocolLib
		try {
			LProtocol.disable();
		} catch (Throwable t) {}
		
		// Finish
		for (int i = 0; i < dependencies.length; i++) {
			dependencies[i] = false;
		}
	}
	
	static void reload() {
		dependencies_loaded = true;
		CrueltyPlugin.getPluginLogger().info("Loading dependencies...");
		
		// ProtocolLib
		try {
			LProtocol.reload();
			dependencies[Depend.PROTOCOLLIB.ordinal()] = true;
		} catch (Throwable t) {
			CrueltyPlugin.getPluginLogger().warning("Unable to load dependency: ProtocolLib.");
			dependencies[Depend.PROTOCOLLIB.ordinal()] = false;
		}
		
		// Finish
		for (int i = 0; i < dependencies.length; i++) {
			if (!dependencies[i]) {
				CrueltyPlugin.getPluginLogger().warning("One or more dependencies failed to load. Some features may be disabled.");
				break;
			}
		}
		
		CrueltyPlugin.getPluginLogger().info("Loaded dependencies!");
	}
}
