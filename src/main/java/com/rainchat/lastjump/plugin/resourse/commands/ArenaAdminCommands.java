package com.rainchat.lastjump.plugin.resourse.commands;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.AUtility;
import com.rainchat.lastjump.common.utils.general.ArenaWriter;
import com.rainchat.lastjump.common.utils.general.Message;
import com.rainchat.lastjump.plugin.managers.ArenaManager;
import com.rainchat.lastjump.plugin.managers.PlayerScoreManager;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

@Command("aJump")
public class ArenaAdminCommands extends CommandBase {


    private final PlayerScoreManager playerScoreManager;
    private final ArenaManager arenaManager;

    public ArenaAdminCommands(PlayerScoreManager playerScoreManager, ArenaManager arenaManager) {
        this.playerScoreManager = playerScoreManager;
        this.arenaManager = arenaManager;
    }

    @SubCommand("setLeaveLocation")
    @Permission("lastjump.admin.setLeaveLocation")
    public void setLeave(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        ArenaWriter.leaveLocationSet(player, arenaName);
    }

    @SubCommand("create")
    @Permission("lastjump.admin.create")
    public void createArena(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        ArenaWriter.createArena(player, arenaName);
    }

    @SubCommand("remove")
    @Permission("lastjump.admin.remove")
    public void removeArena(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        ArenaWriter.removeArena(player, arenaName);
    }
    @SubCommand("select")
    @Permission("lastjump.admin.select")
    public void selectArena(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        ArenaWriter.setSelectArena(player, arenaName);
    }
    @SubCommand("platforms")
    @Permission("lastjump.admin.platforms")
    public void platforms(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        ArenaWriter.visualPlatforms(player, arenaName);
    }

    @SubCommand("setPlatforms")
    @Permission("lastjump.admin.setPlatforms")
    public void addPlatforms(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        ArenaWriter.addPlatforms(player, arenaName);
    }

    @SubCommand("updateTop")
    @Permission("lastjump.admin.updatetop")
    public void updateTop(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        AUtility.asyncScheduler().run(arenaManager::restartTop);
        player.sendMessage(Message.UPDATE_TOP.toString());
    }

    @SubCommand("setFail")
    @Permission("lastjump.admin.setfail")
    public void setFail(final CommandSender commandSender, String arenaName) {
        Player player = (Player) commandSender;
        ArenaWriter.setFailArea(player, arenaName);
    }

    @SubCommand("setSpeed")
    @Permission("lastjump.admin.setfail")
    public void setSpeed(final CommandSender commandSender, String arenaName, String number) {
        Player player = (Player) commandSender;
        ArenaWriter.setSpeed(player, arenaName, number);
    }

    @SubCommand("setSpeedInc")
    @Permission("lastjump.admin.setSpeedInc")
    public void setSpeedInc(final CommandSender commandSender, String arenaName, String number) {
        Player player = (Player) commandSender;
        ArenaWriter.setSpeedInc(player, arenaName, number);
    }

    @SubCommand("setMinPlayers")
    @Permission("lastjump.admin.setMinPlayers")
    public void setMinPlayers(final CommandSender commandSender, String arenaName, String number) {
        Player player = (Player) commandSender;
        ArenaWriter.setMinPlayers(player, arenaName, number);
    }

    @SubCommand("setMaxPlayers")
    @Permission("lastjump.admin.setMaxPlayers")
    public void setMaxPlayers(final CommandSender commandSender, String arenaName, String number) {
        Player player = (Player) commandSender;
        ArenaWriter.setMaxPlayers(player, arenaName, number);
    }

    @SubCommand("reload")
    @Permission("lastjump.admin.reload")
    public void reload(final CommandSender commandSender) throws SQLException {
        LastJump.getInstance().onReload();
        commandSender.sendMessage(Message.RELOAD.toString());
    }

}
