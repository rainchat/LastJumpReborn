package com.rainchat.lastjump.plugin.arena;

import com.rainchat.lastjump.plugin.data.regions.PlatformJump;
import com.rainchat.lastjump.plugin.data.regions.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class ArenaSettings {

	//ArenaSettings
	private String name;
	private double speed = 1;
	private double speedInc = 0.3;
	private int minPlayers = 1;
	private int maxPlayers = 2;
	private List<PlatformJump> platformJumpBlocks;
	private Region failRegion;
	private Location leaveLoctaion;
	private int startTimer;

	public ArenaSettings(String name) {
		this.name = name;
		this.startTimer = 10;
		platformJumpBlocks = new ArrayList<>();
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void addPlatform(PlatformJump region) {
		platformJumpBlocks.add(region);
	}

	public void removePlayer(Player player) {
		player.teleport(leaveLoctaion);
	}
	public Location getLeaveLoctaion() {
		return leaveLoctaion;
	}

	public void setLeaveLoctaion(Location leaveLoctaion) {
		this.leaveLoctaion = leaveLoctaion;
	}

	public void setStartTimer(int startTimer) {
		this.startTimer = startTimer;
	}

	public int getStartTimer() {
		return startTimer;
	}

	public double getSpeed() {
		return speed;
	}

	public String getName() {
		return name;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public double getSpeedInc() {
		return speedInc;
	}

	public Region getFailRegion() {
		return failRegion;
	}

	public List<PlatformJump> getPlatforms() {
		return platformJumpBlocks;
	}

	public void setSpeedInc(double speedInc) {
		this.speedInc = speedInc;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public void setFailRegion(Region region) {
		this.failRegion = region;

	}

}
