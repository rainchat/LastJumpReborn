package com.rainchat.lastjump.plugin.resourse.listeners;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.ArenaWriter;
import com.rainchat.lastjump.plugin.arena.ArenaLogic;
import com.rainchat.lastjump.plugin.managers.PlayerScoreManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class ArenaListener implements Listener{
	
	private LastJump plugin;
	private final PlayerScoreManager scoreManager;
	
	public ArenaListener(LastJump pl, PlayerScoreManager scoreManager) {
		plugin = pl;
		this.scoreManager = scoreManager;
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (plugin.getArenaManager().getArena(player) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if(plugin.getArenaManager().getArena(player) == null)  return;

		ArenaLogic arena = plugin.getArenaManager().getArena(player);

		if (arena == null) return;
		if (!arena.getFailRegion().contains(player.getLocation())) return;

		arena.playerLeave(player);

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		 scoreManager.getPlayerStats(event.getPlayer().getUniqueId().toString());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		final String uuid = player.getUniqueId().toString();
		scoreManager.removePlayerStats(uuid);

		ArenaWriter.removePlayer(player);

		if(plugin.getArenaManager().getArena(player) == null) return;

		ArenaLogic arena = plugin.getArenaManager().getArena(player);

		arena.playerLeave(player);


	}

}
