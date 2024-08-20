package com.rainchat.lastjump.common.utils.general;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class PersistentDataSaver {

    public static NamespacedKey JUMP_NAME_SPACE;

    public PersistentDataSaver(Plugin plugin) {
        JUMP_NAME_SPACE = new NamespacedKey(plugin, "lastJump");
    }
}
