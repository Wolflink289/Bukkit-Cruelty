package com.wolflink289.apis.bukkit;

import java.util.Random;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;

class CrueltyListener implements Listener {
	
	static private final Random rand = new Random(System.currentTimeMillis());
	
	/*
	 * For DENY attack.
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) return;
		
		// Is player denied?
		if (Cruelty.denied.isEmpty()) return;
		if (!Cruelty.denied.contains(event.getPlayer().getEntityId())) return;
		
		// Should be cancelled?
		if (rand.nextInt(10) == 5) event.setCancelled(true);
	}
	
	/*
	 * For DENY attack.
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		
		// Is player denied?
		if (Cruelty.denied.isEmpty()) return;
		if (!Cruelty.denied.contains(event.getPlayer().getEntityId())) return;
		
		// Should be cancelled?
		if (rand.nextInt(3) == 2) event.setCancelled(true);
	}
	
	/*
	 * For DENY attack.
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		
		// Is player denied?
		if (Cruelty.denied.isEmpty()) return;
		if (!Cruelty.denied.contains(event.getPlayer().getEntityId())) return;
		
		// Should be cancelled?
		if (rand.nextInt(3) == 1) event.setCancelled(true);
	}
}
