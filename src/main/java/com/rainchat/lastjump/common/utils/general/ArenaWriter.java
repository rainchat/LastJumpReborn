package com.rainchat.lastjump.common.utils.general;

import com.rainchat.lastjump.plugin.arena.ArenaLogic;
import com.rainchat.lastjump.plugin.arena.ArenaSettings;
import com.rainchat.lastjump.plugin.data.players.SelectPlayer;
import com.rainchat.lastjump.plugin.data.regions.PlatformJump;
import com.rainchat.lastjump.plugin.data.regions.Region;
import com.rainchat.lastjump.plugin.managers.ArenaManager;
import com.rainchat.lastjump.plugin.managers.PlayerScoreManager;
import com.rainchat.lastjump.plugin.managers.SelectManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public class ArenaWriter {

    private static ArenaManager arenaManager;
    private static SelectManager selectManager;
    private static PlayerScoreManager playerScoreManager;

    public static void setup(ArenaManager arena, SelectManager select, PlayerScoreManager scoreManager) {
        arenaManager = arena;
        selectManager = select;
        playerScoreManager = scoreManager;
    }

    public static void createArena(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.create")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }

        ArenaSettings arena = new ArenaSettings(arenaName);
        arenaManager.createArena(arena);
        arenaManager.saveArenaToFile(arena);
        player.sendMessage(Message.CREATE_ARENA.toString());
    }

    public static void leaveLocationSet(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.setleave")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getMainArena(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        arena.setLeaveLoctaion(player.getLocation());
        arenaManager.saveArenaToFile(arena);
        player.sendMessage(Message.SET_LEAVE.toString());
    }

    public static void removeArena(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.remove")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaLogic arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        arenaManager.removeArena(arena.getName());
        player.sendMessage(Message.REMOVE_ARENA.toString().replace("{0}", arenaName));
    }

    public static void setFailArea(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.setfailregion")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        SelectPlayer selectPlayer = selectManager.getSelectPlayer(player);
        if (selectPlayer.getPos1() != null && selectPlayer.getPos2() != null) {
            Region region = new Region(selectPlayer.getPos1(), selectPlayer.getPos2());
            arena.setFailRegion(region);

            player.sendMessage(Message.SET_FAIL_REGION.toString().replace("{0}", arenaName));
        }
        arenaManager.saveArenaToFile(arena);
    }

    public static void removePlayer(Player player) {
        selectManager.removeSelectPlayer(player);
    }

    public static void addPlatform(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.addplatforms")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        SelectPlayer selectPlayer = selectManager.getSelectPlayer(player);
        if (selectPlayer.getPos1() != null && selectPlayer.getPos2() != null) {
            Region region = new Region(selectPlayer.getPos1(), selectPlayer.getPos2());
            if (region.isPlatform()) {
                arena.addPlatform(new PlatformJump(selectPlayer.getPos1(), selectPlayer.getPos2()));
                player.sendMessage(ChatColor.GREEN + "Set platform region!");
                arenaManager.saveArenaToFile(arena);
            } else {
                player.sendMessage(Message.NOT_PLATFORM.toString().replace("{0}", arenaName));
            }
        }


    }

    public static void addScore(Player player, String arena, int score) {
        playerScoreManager.save(player, arena,score);
    }

    public static void addPlatforms(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.addplatforms")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        SelectPlayer selectPlayer = selectManager.getSelectPlayer(player);
        Region arenaPlatform = new Region(selectPlayer.getPos1(), selectPlayer.getPos2());

        arena.getPlatforms().clear();
        int count = 0;
        for (int i = arenaPlatform.getMinX(); i <= arenaPlatform.getMaxX(); i++) {
            for (int j = arenaPlatform.getMinZ(); j <= arenaPlatform.getMaxZ(); j++) {
                for (int k = arenaPlatform.getMinY(); k <= arenaPlatform.getMaxY(); k++) {
                    if (checkPlatforms(new Location(selectPlayer.getPos1().getWorld(), i,k,j))) {
                        Location location1 = new Location(selectPlayer.getPos1().getWorld(), i,k,j);
                        Location location2 = new Location(selectPlayer.getPos1().getWorld(), i+1,k,j+1);
                        arena.addPlatform(new PlatformJump(location1, location2));
                        count++;
                    }
                }
            }
        }
        arenaManager.getArena(arenaName).refreshArena();
        arenaManager.saveArenaToFile(arena);
        player.sendMessage(ChatColor.GREEN + "Успешно найдено {0} платформ!".replace("{0}", String.valueOf(count)));
    }

    private static boolean checkPlatforms(Location location) {
        if (location.getBlock().isEmpty()) {
            return false;
        }
        location.add(0,0,1);
        if (location.getBlock().isEmpty()) {
            return false;
        }
        location.add(1,0,0);
        if (location.getBlock().isEmpty()) {
            return false;
        }
        location.add(0,0,-1);
        return !location.getBlock().isEmpty();
    }

    public static void visualPlatforms(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.platforms")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        for (Region jumpBlocks: arena.getPlatforms()) {
            for (BlockState blockState: jumpBlocks.getBlocks()) {
                player.sendBlockChange(blockState.getLocation(), Bukkit.createBlockData(Material.GREEN_STAINED_GLASS));
            }
        }

        arenaManager.saveArenaToFile(arena);
    }

    public static void setSelectArena(Player player, String arenaName) {
        if (!player.hasPermission("ljump.arenas.setselect")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        selectManager.getSelectPlayer(player).setArenaName(arenaName);
        player.sendMessage(Message.SET_SELECT.toString().replace("{0}", arenaName));

    }

    public static void setSpeed(Player player, String arenaName, String number) {
        if (!player.hasPermission("ljump.arenas.setspeed")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        arena.setSpeed(Double.parseDouble(number));
        player.sendMessage(Message.SET_SPEED.toString().replace("{0}", number));

        arenaManager.saveArenaToFile(arena);
    }

    public static void setSpeedInc(Player player, String arenaName, String number) {
        if (!player.hasPermission("ljump.arenas.setspeedinc")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        arena.setSpeedInc(Double.parseDouble(number));
        player.sendMessage(Message.SET_SPEED_INC.toString().replace("{0}", number));

        arenaManager.saveArenaToFile(arena);
    }

    public static void setMinPlayers(Player player, String arenaName, String number) {
        if (!player.hasPermission("ljump.arenas.setminplayers")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        arena.setMinPlayers(Integer.parseInt(number));
        player.sendMessage(Message.SET_MIN_PLAYERS.toString().replace("{0}", number));

        arenaManager.saveArenaToFile(arena);
    }

    public static void setMaxPlayers(Player player, String arenaName, String number) {
        if (!player.hasPermission("ljump.arenas.setmaxplayers")) {
            player.sendMessage(Message.NO_PERMISSION.toString().replace("{0}", ""));
            return;
        }
        ArenaSettings arena = arenaManager.getArenaSettings(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
            return;
        }

        arena.setMaxPlayers(Integer.parseInt(number));
        player.sendMessage(Message.SET_MAX_PLAYERS.toString().replace("{0}", number));

        arenaManager.saveArenaToFile(arena);
    }
}
