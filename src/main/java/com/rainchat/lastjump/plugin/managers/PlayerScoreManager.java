package com.rainchat.lastjump.plugin.managers;

import com.rainchat.lastjump.plugin.data.database.Database;
import com.rainchat.lastjump.plugin.data.players.PlayerScore;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerScoreManager {

    private final Map<String, PlayerScore> playerStats = new HashMap<>();
    private final Database database;

    public PlayerScoreManager(Database database) {
        this.database = database;
    }


    public Set<String> listPlayerStats() {
        return playerStats.keySet();
    }

    public void unloadPlayerStats(String uuid) {
        PlayerScore playerStats = this.playerStats.remove(uuid);
        if (playerStats != null) {
            save(playerStats);
        }
    }

    public void removePlayerStats(String uuid) {
        playerStats.remove(uuid);
    }

    public PlayerScore getPlayerStats(String uuid) {
        if (playerStats.containsKey(uuid)) {
            return playerStats.get(uuid);
        }
        PlayerScore playerStats = new PlayerScore(uuid, new HashMap<>());

        playerStats.setArenaScore(database.getValues(uuid));

        this.playerStats.put(uuid, playerStats);
        return playerStats;
    }

    public void save(PlayerScore playerStats) {
        for (Map.Entry<String,Integer> arenaScore: playerStats.getArenaScore().entrySet()) {
            database.setValues(playerStats.getPlayerUUID(), arenaScore.getKey(), arenaScore.getValue());
        }
    }

    public void save(Player player, String arena, int score) {
        PlayerScore playerScore = getPlayerStats(player.getUniqueId().toString());
        if (playerScore.addArenaScore(arena,score)) {
            database.setValues(player.getUniqueId().toString(), arena, score);
        }
    }
}
