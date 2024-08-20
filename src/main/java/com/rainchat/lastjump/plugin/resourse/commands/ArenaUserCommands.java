package com.rainchat.lastjump.plugin.resourse.commands;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.AUtility;
import com.rainchat.lastjump.common.utils.general.Message;
import com.rainchat.lastjump.plugin.arena.ArenaLogic;
import com.rainchat.lastjump.plugin.arena.ArenaStatus;
import com.rainchat.lastjump.plugin.data.players.PlayerScore;
import com.rainchat.lastjump.plugin.managers.ArenaManager;
import com.rainchat.lastjump.plugin.managers.PlayerScoreManager;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@Command("LastJump")
@Alias({"jump"})
public class ArenaUserCommands  extends CommandBase {

    private final PlayerScoreManager playerScoreManager;
    private final ArenaManager arenaManager;

    public ArenaUserCommands(PlayerScoreManager playerScoreManager, ArenaManager arenaManager) {
        this.playerScoreManager = playerScoreManager;
        this.arenaManager = arenaManager;
    }

    @SubCommand("join")
    @Permission("lastjump.user.join")
    public void joinCommand(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;

        ArenaLogic check = arenaManager.getArena(player);
        if (check != null) {
            player.sendMessage(Message.ALREADY_IN_ARENA.toString());
            return;
        }
        ArenaLogic arena = LastJump.getInstance().getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage(Message.NO_ARENA.toString().replace("{0}", arenaName));
        } else {
            if (arena.getStatus().equals(ArenaStatus.Active)) {
                player.sendMessage(Message.FULL_ARENA.toString());
            } else {
                arena.playerJoin(player);
            }
        }
    }

    @SubCommand("leave")
    @Permission("lastjump.user.leave")
    public void leaveComman(final CommandSender commandSender) {
        Player player = (Player) commandSender;

        ArenaLogic check = LastJump.getInstance().getArenaManager().getArena(player);
        if(check == null) {
            player.sendMessage(Message.NOT_IN_ARENA.toString());
        }else {
            check.playerLeave(player);
        }
    }

    @SubCommand("top")
    @Permission("lastjump.user.top")
    public void topCommand(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;

        player.sendMessage(Message.ARENA_TOP_TITLE.toString().replace("%arena_name%", arenaName));
        for (int i = 0; i<=5; i++) {
            player.sendMessage(replaceScore(Message.ARENA_TOP_SCORE.toString()
                            .replace("{0}", Integer.toString(i+1))
                            .replace("%player_name%", arenaManager.getTopScore(i,true, arenaName)),
                    arenaManager.getTopScore(i,false, arenaName))
            );
        }
    }

    public String replaceScore(String text, String score) {
        if (score == "null")
            return text
                    .replace("%arena_score_min%", "0")
                    .replace("%arena_score_sec%", "0");
        return text
                .replace("%arena_score_min%", AUtility.getMinutes(Integer.parseInt(score)))
                .replace("%arena_score_sec%", AUtility.getSeconds(Integer.parseInt(score)));
    }

    @SubCommand("stats")
    @Permission("lastjump.user.stats")
    public void statsCommand(final CommandSender commandSender) {
        Player player = (Player) commandSender;

        player.sendMessage(Message.PLAYER_STATS_TITLE.toString());
        PlayerScore playerScore = playerScoreManager.getPlayerStats(player.getUniqueId().toString());
        int i = 1;
        for (Map.Entry<String,Integer> score: playerScore.getArenaScore().entrySet()) {
            player.sendMessage(replaceScore(Message.PLAYER_STATS_SCORE.toString()
                    .replace("{0}", Integer.toString(i))
                    .replace("%arena_name%", score.getKey()),
                    Integer.toString(score.getValue())));
            i++;
        }
    }
}
