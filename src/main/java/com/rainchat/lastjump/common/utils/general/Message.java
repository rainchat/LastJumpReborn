package com.rainchat.lastjump.common.utils.general;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Message {
    PREFIX("Messages.prefix", "&7[&dLastJump&7]"),

    RELOAD("Messages.reload", "&aПлагин успешно перезагружен"),
    NO_COMMAND_PERMISSION("Messages.no-command-permission", "You do not have permissions for that command."),
    NO_PERMISSION("Messages.no-permission", "You do not have permissions."),
    PLAYER_OFFLINE("Messages.player-offline", "The player &b{0} &7does not seem to be online."),
    NOT_PLAYER("Messages.not-player", "&cYou must be a Player to do that!"),
    JOIN_ARENA("Messages.join", "&7Successfully joined &a{0}"),
    UPDATE_TOP("Messages.update-top", "&7Топы на аренах успешно перезагружены"),
    LEAVE_ARENA("Messages.leave","&aYou left the arena."),
    ALREADY_IN_ARENA("Messages.already-in-arena", "&cYou are already in a arena! You cannot join another one yet. Finish your current arena first!"),
    FULL_ARENA("Messages.full-arena", "&cThat arena is not available right now! (It is start already)"),
    NOT_IN_ARENA("Messages.not-in-arena","&cYou are not in a jump arena, so you cannot leave one!"),
    START("Messages.start", "&7The game will begin through."),
    SCORE_COUNT("Messages.score-count", "&7Вы продержались на арене &a%arena_score%"),
    PREPARE_TO_JUMP("Messages.prepare-to-jump", "&bPREPARE TO START!"),
    WIN("Messages.win", "&7You &awon &7the jump! You score: &e{0}"),
    LOSE("Messages.lose", "&7You &clost &7the jump! You score: &e{0}"),
    SPEED("Messages.speed", "&7Platform speed increased: &a{0}&7!."),


    NOT_BUIlD("Messages.not-build", "&cАрена не доконца построена, обратитесь к администратору!"),
    CREATE_ARENA("Messages.create", "&aSuccessfully created an arena! Do not forget to set platforms and failZone!"),
    NOT_PLATFORM("Messages.no-platform", "&7This is not a platform, the size of the platform must be &c&l2x2x1&7 block!"),
    REMOVE_ARENA("Messages.remove", "&7You remove arena &c{0}."),
    NO_ARENA("Messages.no-arena", "&cThere is no arena called &e{0}&c!"),
    SET_LEAVE("Messages.set-leave-arena", "&cВы успешно установивили точку покидания арены&c!"),
    SET_SPEED("Messages.set-speed", "&7Successfully set speed &a{0}"),
    SET_SELECT("Messages.set-select", "&7Successfully set selected arena &a{0}"),
    SET_SPEED_INC("Messages.set-speedinc", "&7Successfully set speed increment &a{0}"),
    SET_MIN_PLAYERS("Messages.set-min-players", "&7Successfully set min players to &a{0}"),
    SET_MAX_PLAYERS("Messages.set-max-players", "&7Successfully set max players to &a{0}"),
    SET_FAIL_REGION("Messages.set-set-fail-region", "&7Successfully set fail region in arena:&a{0}"),
    ARENA_TOP_TITLE("Messages.arena-stats-title", "&7Лучшие игроки на арене %arena_name%"),
    ARENA_TOP_SCORE("Messages.arena-stats-score", "&c{0}&7. %player_name%: &a%player_score%"),
    PLAYER_STATS_TITLE("Messages.player-stats-title", "&7Ваша статистика на аренах"),
    PLAYER_STATS_SCORE("Messages.player-stats-score", "&c{0}&7. %arena_name%: &a%player_score%");


    private String path, def;
    private List<String> list;
    private static FileConfiguration configuration;

    Message(String path, String def) {
        this.path = path;
        this.def = def;
    }

    Message(String path, List<String> list) {
        this.path = path;
        this.list = list;
    }

    public String getDef() {
        return configuration.getString(path, def);
    }

    @Override
    public String toString() {
        return Chat.color(configuration.getString(path, def));
    }

    public List<String> toList() {
        return configuration.getStringList(path);
    }

    public static void setConfiguration(FileConfiguration configuration) {
        Message.configuration = configuration;
    }

    public String getPath() {
        return path;
    }

    public List<String> getList() {
        return list;
    }
}
