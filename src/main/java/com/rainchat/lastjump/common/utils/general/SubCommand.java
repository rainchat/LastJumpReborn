package com.rainchat.lastjump.common.utils.general;

import org.bukkit.entity.Player;

public abstract class SubCommand {

    public SubCommand() {
    }

    public abstract void onCommand(Player player, String[] args);

    public abstract String name();

    public abstract String info();

    public abstract String[] aliases();
}