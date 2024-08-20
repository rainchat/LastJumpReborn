package com.rainchat.lastjump.plugin.data.regions;


import com.rainchat.lastjump.common.utils.general.AUtility;
import com.rainchat.lastjump.common.utils.general.PersistentDataSaver;
import com.rainchat.lastjump.common.utils.scheduler.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PlatformJump extends Region {

    private final List<Material> materials;
    private boolean isActive;


    public PlatformJump(String platform) {
        super(platform);
        this.materials = new ArrayList<>();
        String[] platSplice = platform.split(",");
        if (platSplice.length < 10) {
            for (BlockState blockState: getBlocks()) {
                materials.add(blockState.getType());
            }
        } else {
            materials.add(Material.valueOf(platSplice[7].toUpperCase()));
            materials.add(Material.valueOf(platSplice[8].toUpperCase()));
            materials.add(Material.valueOf(platSplice[9].toUpperCase()));
            materials.add(Material.valueOf(platSplice[10].toUpperCase()));
        }

        isActive = true;
    }

    public PlatformJump(Location loc1, Location loc2) {
        super(loc1, loc2);
        this.materials = new ArrayList<>();
        for (BlockState blockState: getBlocks()) {
            materials.add(blockState.getType());
        }
        isActive = true;
    }

    public PlatformJump(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        super(world.getName(), x1, y1, z1, x2, y2, z2);
        this.materials = new ArrayList<>();
        for (BlockState blockState: getBlocks()) {
            materials.add(blockState.getType());
        }
        isActive = true;
    }

    public PlatformJump(Region region) {
        super(region.getWorld().getName(), region.getMaxX(), region.getMaxY(), region.getMaxZ(), region.getMinX(), region.getMinY(), region.getMinZ());
        this.materials = new ArrayList<>();
        for (BlockState blockState: getBlocks()) {
            materials.add(blockState.getType());
        }
        isActive = true;
    }

    public void clearArea() {
        setActive(false);
        for (BlockState blockState: getBlocks()) {
            FallingBlock fall = blockState.getLocation().getWorld().spawnFallingBlock(blockState.getLocation().add(0.5,0.5,0.5),blockState.getBlockData());
            fall.setDropItem(false);
            double x = (blockState.getX() - getCenter().getX())*0.1;
            double y = 0.10;
            double z = (blockState.getZ() - getCenter().getZ())*0.1;
            fall.setVelocity(new Vector(x, y, z));
            PersistentDataContainer container = fall.getPersistentDataContainer();
            container.set(PersistentDataSaver.JUMP_NAME_SPACE, PersistentDataType.INTEGER, 1);
            blockState.getBlock().setType(Material.AIR);
            AUtility.syncScheduler().after(20).run(fall::remove);

        }

    }

    public void regenArea() {
        setActive(true);
        List<BlockState> blockStates = getBlocks();
        for (int i = 0; i < materials.size(); i++) {
            if (!blockStates.get(i).getBlock().getChunk().isLoaded()) continue;
            if (materials.get(i).isAir()) {
                blockStates.get(i).getBlock().setType(Material.STONE);
                continue;
            }
            blockStates.get(i).getBlock().setType(materials.get(i));
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toSave() {
        StringBuilder text = new StringBuilder();
        for (Material material: materials) {
            text.append(",").append(material);
        }
        return getWorld().getName() +
                "," + getMinX() +
                "," + getMinY() +
                "," + getMinZ() +
                "," + getMaxX() +
                "," + getMaxY() +
                "," + getMaxZ() +
                text.toString();
    }
}
