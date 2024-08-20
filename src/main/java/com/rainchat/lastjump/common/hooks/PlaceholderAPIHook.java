package com.rainchat.lastjump.common.hooks;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.AUtility;
import com.rainchat.lastjump.plugin.arena.ArenaLogic;
import com.rainchat.lastjump.plugin.managers.ArenaManager;
import com.rainchat.lastjump.plugin.managers.SelectManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final LastJump plugin;
    private final ArenaManager arenaManager;
    private final SelectManager selectManager;

    public PlaceholderAPIHook(LastJump plugin, ArenaManager arenaManager, SelectManager selectManager) {
        this.selectManager = selectManager;
        this.plugin = plugin;
        this.arenaManager = arenaManager;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "lastjump";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String[] split = identifier.split("_");

        if (split.length == 0) return "err";

        ArenaLogic arena = arenaManager.getArena(split[1]);

        // %lastjump_top_<arena>_<number>_sec%
        // %lastjump_top_<arena>_<number>_min%
        // %lastjump_top_<arena>_<number>_player%

        if (split[0].equalsIgnoreCase("top")) {
            if (split[3].equalsIgnoreCase("sec")) {
                String score = arenaManager.getTopScore(Integer.parseInt(split[2]), false, arena.getName());
                if (score.equalsIgnoreCase("null")) {
                    return "null";
                }
                return AUtility.getSeconds(Integer.parseInt(score));
            }
            if (split[3].equalsIgnoreCase("min")) {
                String score = arenaManager.getTopScore(Integer.parseInt(split[2]), false, arena.getName());
                if (score.equalsIgnoreCase("null")) {
                    return "null";
                }
                return AUtility.getMinutes(Integer.parseInt(score));
            }
            if (split[3].equalsIgnoreCase("player")) {
                return arenaManager.getTopScore(Integer.parseInt(split[2]), true, arena.getName());
            }
        }

        // %lastjump_get_<arena>_<player>_sec%
        // %lastjump_get_<arena>_<player>_min%
        // %lastjump_get_<arena>_<player>_player%
        if (split[0].equalsIgnoreCase("get")) {
            if (split[3].equalsIgnoreCase("sec")) {
                String score = arenaManager.getTopScore(Integer.parseInt(split[2]), false, arena.getName());
                if (score.equalsIgnoreCase("null")) {
                    return "null";
                }
                return AUtility.getSeconds(Integer.parseInt(score));
            }
            if (split[3].equalsIgnoreCase("min")) {
                String score = arenaManager.getTopScore(Integer.parseInt(split[2]), false, arena.getName());
                if (score.equalsIgnoreCase("null")) {
                    return "null";
                }
                return AUtility.getMinutes(Integer.parseInt(score));
            }
            if (split[3].equalsIgnoreCase("player")) {
                return arenaManager.getTopScore(Integer.parseInt(split[2]), true, arena.getName());
            }
        }

        if (arena == null) return "";

        if (split[1].equalsIgnoreCase("name")) {
            return arena.getName();
        }
        if (split[1].equalsIgnoreCase("isActive")) {
            return arena.getStatus()+"";
        }
        if (split[1].equalsIgnoreCase("max_Score")) {
            return arena.getStatus()+"";
        }


        return "";
    }
}
