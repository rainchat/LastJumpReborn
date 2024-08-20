package com.rainchat.lastjump.plugin.resourse.listeners;

import com.rainchat.lastjump.common.utils.general.ArenaWriter;
import com.rainchat.lastjump.plugin.data.players.SelectPlayer;
import com.rainchat.lastjump.plugin.managers.SelectManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

public class CuboidCreateListener implements Listener {

    private SelectManager selectManager;

    public CuboidCreateListener(SelectManager selectManager) {
        this.selectManager = selectManager;
    }

    @EventHandler
    public void onClickStick(PlayerInteractEvent event) {

        if (event.getItem() == null) {
            return;
        }
        if (!event.getItem().getType().equals(Material.BLAZE_ROD)) {
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        Action action = event.getAction();

        SelectPlayer selectPlayer = selectManager.getSelectPlayer(player);

        Location targetLocation = getTargetBlock(event.getPlayer(),32).getLocation();

        if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            selectPlayer.setPos1(targetLocation);
            player.sendBlockChange(targetLocation, Bukkit.createBlockData(Material.PURPLE_TERRACOTTA));
        }
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            selectPlayer.setPos2(targetLocation);
            player.sendBlockChange(targetLocation, Bukkit.createBlockData(Material.LIME_TERRACOTTA));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!event.getItemDrop().getItemStack().getType().equals(Material.BLAZE_ROD)) {
            return;
        }

        Player player = event.getPlayer();
        SelectPlayer selectPlayer = selectManager.getSelectPlayer(player);

        if (!(selectPlayer.getPos1() != null && selectPlayer.getPos2() != null && selectPlayer.getArenaName() != null)) {
            return;
        }
        event.setCancelled(true);

        ArenaWriter.addPlatform(player, selectPlayer.getArenaName());
    }

    @EventHandler
    public void onSwap(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        SelectPlayer selectPlayer = selectManager.getSelectPlayer(player);
        if (item == null) {
            selectPlayer.clear();
            return;
        }
        if (!item.getType().equals(Material.BLAZE_ROD)) {
            selectPlayer.clear();
        }
    }



    public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType().name().contains("AIR") || lastBlock.getType().name().contains("LIGHT")) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
}
