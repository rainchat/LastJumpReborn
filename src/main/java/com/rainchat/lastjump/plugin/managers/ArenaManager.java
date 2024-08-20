package com.rainchat.lastjump.plugin.managers;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.AUtility;
import com.rainchat.lastjump.common.utils.general.LocationUtil;
import com.rainchat.lastjump.common.utils.storage.YAML;
import com.rainchat.lastjump.plugin.arena.ArenaLogic;
import com.rainchat.lastjump.plugin.arena.ArenaSettings;
import com.rainchat.lastjump.plugin.data.database.Database;
import com.rainchat.lastjump.plugin.data.regions.PlatformJump;
import com.rainchat.lastjump.plugin.data.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;


public class ArenaManager {
	
	private final LastJump plugin;
	private final YAML yaml;
	private final Database database;
	private HashMap<String, LinkedHashMap<String,Integer>> arenaTop;
	private List<ArenaSettings> arenaList = new ArrayList();
	private HashMap<String, ArenaLogic> arenaLogics = new HashMap<>();
	private HashMap<String, Set<String>> notLoadArenas = new HashMap<>();

	public ArenaManager(LastJump plugin, YAML yaml, Database database) {
		this.plugin = plugin;
		this.yaml = yaml;
		this.arenaTop = new LinkedHashMap<>();
		this.database = database;

		start();

		AUtility.asyncScheduler().every(20*60*5).run(this::restartTop);
	}

	public void closeServer() {
		for (ArenaSettings arena: arenaList) {
			saveArenaToFile(arena);
		}
		for (ArenaLogic arenaLogic: arenaLogics.values()) {
			arenaLogic.refreshArena();
		}
	}

	public void start() {
		try {
			loadAllArenasFromFile();
		}catch(Exception ignore) {}

		for (ArenaLogic arenaLogic: arenaLogics.values()) {
			arenaLogic.refreshArena();
		}
	}

	public LinkedHashMap<String, Integer> getScore(String arena) {
		return arenaTop.getOrDefault(arena, new LinkedHashMap<>());
	}

	public List<ArenaSettings> getArenaList(){
		return arenaList;
	}

	 // Loads all arenas
	public void loadAllArenasFromFile() {
		arenaList.clear();
		FileConfiguration c = yaml.getFileConfiguration();
		for(String arenaName : c.getConfigurationSection("Arenas").getKeys(false)) {
			loadArena(arenaName);
		}
	}

	public ArenaLogic loadArena(String arenaName) {
		FileConfiguration c = yaml.getFileConfiguration();

		ArenaSettings arena = new ArenaSettings(arenaName);
		arena.setFailRegion(new Region(Objects.requireNonNull(c.getString("Arenas." + arenaName + ".failRegion"))));

		List<String> platforms = c.getStringList("Arenas." + arenaName + ".platforms");
		for (String platform: platforms) {
			if (Bukkit.getWorld(platform.split(",")[0]) == null) {
				Set<String> arenas = notLoadArenas.getOrDefault(platform.split(",")[0], new HashSet<>());
				arenas.add(arenaName);
				notLoadArenas.put(arenaName, arenas);
				continue;
			}
			PlatformJump region = new PlatformJump(platform);
			arena.addPlatform(region);
		}
		arena.setSpeed(c.getDouble("Arenas." + arenaName + ".speed", c.getDouble("Settings.defaultSpeed")));
		arena.setSpeedInc(c.getDouble("Arenas." + arenaName + ".speedInc", c.getDouble("Settings.defaultIncrease")));
		arena.setMinPlayers(c.getInt("Arenas." + arenaName + ".minPlayers", 1));
		arena.setMaxPlayers(c.getInt("Arenas." + arenaName + ".maxPlayers", 2));
		arena.setLeaveLoctaion(LocationUtil.getLocationString(c.getString("Arenas." + arenaName + ".leaveLocation", null)));

        return createArena(arena);
	}

	public void loadWorldArenas(String name) {
		Set<String> arenaList = notLoadArenas.getOrDefault(name, new HashSet<>());
		arenaList.forEach(s -> loadArena(s).refreshArena());
	}

	public void saveArenaToFile(ArenaSettings arena) {
		FileConfiguration c = yaml.getFileConfiguration();
		String path = "Arenas." + arena.getName();

		if (arena.getFailRegion() != null) {
			c.set(path + ".failRegion", arena.getFailRegion().toSave());
		}
		List<String> platforms = new ArrayList<>();
		for (PlatformJump platformJump: arena.getPlatforms()) {
			platforms.add(platformJump.toSave());
		}
		c.set(path + ".platforms", platforms);
		c.set(path + ".speed", arena.getSpeed());
		c.set(path + ".speedInc", arena.getSpeedInc());
		c.set(path + ".minPlayers", arena.getMinPlayers());
		c.set(path + ".maxPlayers", arena.getMaxPlayers());
		if (arena.getLeaveLoctaion() != null) {
			c.set(path + ".leaveLocation", LocationUtil.getStringLocation(arena.getLeaveLoctaion()));
		}

		yaml.save();
	}

	public ArenaLogic createArena(ArenaSettings arena) {
		arenaList.add(arena);
		ArenaLogic arenaLogic = new ArenaLogic(arena, plugin);
		arenaLogics.put(arena.getName(), arenaLogic);
		return arenaLogic;
	}

	public void removeArena(String arena) {
		FileConfiguration c = yaml.getFileConfiguration();
		c.set("Arenas." + arena, null);

		yaml.save();
	}

	public ArenaSettings getArenaSettings(String name) {
		for(ArenaSettings arena : arenaList) {
			if(Objects.equals(arena.getName(), name)) {
				return arena;
			}
		}
		return null;
	}

	public void rebutArena(String name) {
		arenaLogics.remove(name);
		arenaLogics.put(name,new ArenaLogic(getMainArena(name), plugin));
		getArena(name).refreshArena();
	}

	public ArenaLogic getArena(String name) {
		return arenaLogics.getOrDefault(name, null);
	}

	public ArenaSettings getMainArena(String name) {
		for(ArenaSettings arena : arenaList) {
			if(Objects.equals(arena.getName(), name)) {
				return arena;
			}
		}
		return null;
	}

	public ArenaLogic getArena(Player player) {
		for(ArenaLogic arena : arenaLogics.values()) {
			if(arena.hasPlayer(player)) {
				return arena;
			}
		}
		return null;
	}

	//ArenaScore
	public void restartTop() {
		for (ArenaSettings arena: arenaList) {
			arenaTop.put(arena.getName(),database.getArenaValues(arena.getName()));
		}
	}

	public String getTopScore(int number, boolean isName, String arena) {
		return getTop(number, isName, getScore(arena));
	}

	private String getTop(int number, boolean key, LinkedHashMap<String, Integer> topMap) {
		if (topMap.isEmpty()) {
			return "null";
		}
		if (key) {
			ArrayList<String> map = new ArrayList<>(topMap.keySet());
			if (map.size() > number) {
				return plugin.getServer().getOfflinePlayer(UUID.fromString(map.get(number))).getName();
			} else {
				return org.bukkit.ChatColor.translateAlternateColorCodes('&', "null");
			}
		} else {
			ArrayList<Integer> map = new ArrayList<>(topMap.values());
			if (map.size() > number) {
				return String.valueOf(map.get(number));
			} else {
				return org.bukkit.ChatColor.translateAlternateColorCodes('&', "null");
			}
		}
	}

}
