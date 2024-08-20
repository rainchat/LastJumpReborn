package com.rainchat.lastjump.common.utils.general;

import com.rainchat.lastjump.common.utils.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class AUtility {

    private static JavaPlugin INSTANCE;

    /**
     * Gets instance.
     *
     * @return Instance.
     */
    public static JavaPlugin getInstance() {
        return AUtility.INSTANCE;
    }

    public static void initialize(JavaPlugin plugin) {
        if (AUtility.INSTANCE != null) return;
        AUtility.INSTANCE = Objects.requireNonNull(plugin, "plugin cannot be null!");
    }

    public static String getSeconds(int seconds) {
        int minutes = seconds/60;
        return String.valueOf(seconds-minutes*60);
    }

    public static String getMinutes(int seconds) {
        int minutes = seconds/60;
        return String.valueOf(minutes);
    }

    public static Scheduler asyncScheduler() {
        return new Scheduler(AUtility.INSTANCE, true);
    }

    public static Scheduler syncScheduler() {
        return new Scheduler(AUtility.INSTANCE, false);
    }
}
