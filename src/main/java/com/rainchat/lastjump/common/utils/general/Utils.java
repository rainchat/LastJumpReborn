package com.rainchat.lastjump.common.utils.general;

import com.rainchat.lastjump.LastJump;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static String prefix() {
        return "[" + "FunJump" + "]";
    }

    public static void info(String text) {
        Bukkit.getLogger().info(prefix() + " " + text);
    }

    public static void warning(String text) {
        Bukkit.getLogger().warning(prefix() + " " + text);
    }

    public static void error(String text) {
        Bukkit.getLogger().severe(prefix() + " " + text);
    }

    public static void exception(StackTraceElement[] stackTraceElement, String text) {
        info("(!) " + prefix() + " has being encountered an error, pasting below for support (!)");
        for (StackTraceElement traceElement : stackTraceElement) {
            error(traceElement.toString());
        }
        info("Message: " + text);
        info(prefix() + " version: " + LastJump.getInstance().getDescription().getVersion());
        info("Please report this error to me on spigot");
        info("(!) " + prefix() + " (!)");
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isString(String text) {
        return text.matches("^[a-zA-Z]*$");
    }

    public static long randomNumber(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    public static long[] getDurability(ItemStack itemStack) {
        if (itemStack != null && isValid(itemStack.getType().name())) {
            short max = itemStack.getType().getMaxDurability();
            return new long[]{max - itemStack.getDurability(), max};
        }
        return new long[]{0, 0};
    }

    private static boolean isValid(String name) {
        return name.endsWith("_HELMET")
                || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS")
                || name.endsWith("_BOOTS")
                || name.endsWith("_SWORD")
                || name.endsWith("_PICKAXE")
                || name.endsWith("_AXE")
                || name.endsWith("_SHOVEL")
                || name.endsWith("SHIELD");
    }

    public static ItemStack getHandItemStack(Player player, boolean main) {
        ItemStack itemStack;
        try {
            if (main) {
                itemStack = player.getInventory().getItemInMainHand();
            } else {
                itemStack = player.getInventory().getItemInOffHand();
            }
        } catch (NoSuchMethodError error) {
            itemStack = player.getInventory().getItemInHand();
        }
        return itemStack;
    }

}
