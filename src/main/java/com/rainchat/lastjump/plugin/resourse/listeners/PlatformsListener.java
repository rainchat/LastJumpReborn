package com.rainchat.lastjump.plugin.resourse.listeners;

import com.rainchat.lastjump.common.utils.general.PersistentDataSaver;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.persistence.PersistentDataType;


/**
 * Disable tnt explosions
 * and Disable tnt push tnt
 */

public class PlatformsListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getPersistentDataContainer().get(PersistentDataSaver.JUMP_NAME_SPACE, PersistentDataType.INTEGER) == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity.getPersistentDataContainer().get(PersistentDataSaver.JUMP_NAME_SPACE, PersistentDataType.INTEGER) == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onFallBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) { return; };
        if (event.getBlock().getType() != ((FallingBlock) event.getEntity()).getMaterial()) { return; }
        Entity entity = event.getEntity();
        if (entity.getPersistentDataContainer().get(PersistentDataSaver.JUMP_NAME_SPACE, PersistentDataType.INTEGER) == null) return;

        event.setCancelled(true);
    }

}
