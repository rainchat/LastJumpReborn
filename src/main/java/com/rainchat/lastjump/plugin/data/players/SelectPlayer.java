package com.rainchat.lastjump.plugin.data.players;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SelectPlayer {

    private final Player player;
    private Location pos1;
    private Location pos2;
    private String arenaName;

    public SelectPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }


    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public void setPos1(Location pos1) {
        if (this.pos1 != null) this.pos1.getBlock().getState().update();
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        if (this.pos2 != null) this.pos2.getBlock().getState().update();
        this.pos2 = pos2;
    }

    public void clear() {
        setPos1(null);
        setPos2(null);
        setArenaName(null);
    }
}
