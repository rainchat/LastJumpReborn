package com.rainchat.lastjump.plugin.managers;

import com.rainchat.lastjump.plugin.data.players.SelectPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectManager {

    private final Map<UUID, SelectPlayer> selectPlayer = new HashMap<>();

    public void removeSelectPlayer(Player player) {
        selectPlayer.remove(player.getUniqueId());
    }

    public SelectPlayer getSelectPlayer(Player player) {
        return selectPlayer.computeIfAbsent(player.getUniqueId(), k -> new SelectPlayer(player));
    }
}
