package com.rainchat.lastjump.common.utils.general;

import org.bukkit.ChatColor;

public class Chat {

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String format(String string) {
        return color(Message.PREFIX.toString() + string);
    }
}
