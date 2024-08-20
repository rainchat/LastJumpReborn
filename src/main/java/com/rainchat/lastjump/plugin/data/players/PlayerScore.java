package com.rainchat.lastjump.plugin.data.players;

import java.util.HashMap;

public class PlayerScore {

    private final String playerUUID;
    private HashMap<String, Integer> arenaScore;

    public PlayerScore(String playerUUID, HashMap<String,Integer> arenaScore) {
        this.playerUUID = playerUUID;
        this.arenaScore = arenaScore;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public int getArenaScore(String arena) {
        return this.arenaScore.getOrDefault(arena, 0);
    }
    public HashMap<String, Integer> getArenaScore() {
        return arenaScore;
    }

    public void setArenaScore(HashMap<String, Integer> arenaScore) {
        this.arenaScore = arenaScore;
    }

    public boolean addArenaScore(String arena, int score) {
        int simpleScore = this.arenaScore.getOrDefault(arena, 0);
        if (score > simpleScore) {
            arenaScore.put(arena, score);
            return true;
        }
        return false;
    }

}
