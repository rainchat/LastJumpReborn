package com.rainchat.lastjump.plugin.resourse.listeners;

import com.rainchat.lastjump.plugin.managers.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    private final ArenaManager arenaManager;

    public WorldListener(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    @EventHandler
    public void onLoadWorld(WorldLoadEvent event) {
        arenaManager.loadWorldArenas(event.getWorld().getName());
    }
}
