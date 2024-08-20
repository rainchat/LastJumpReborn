package com.rainchat.lastjump.plugin.arena;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.AUtility;
import com.rainchat.lastjump.common.utils.general.ArenaWriter;
import com.rainchat.lastjump.common.utils.general.Message;
import com.rainchat.lastjump.common.utils.general.PersistentDataSaver;
import com.rainchat.lastjump.plugin.data.regions.PlatformJump;
import com.rainchat.lastjump.plugin.data.regions.Region;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaLogic extends BukkitRunnable {

    private ArenaSettings arena;
    private Plugin plugin;
    private ArenaTimer arenaScheduler;
    private List<PlatformJump> allPlatforms;
    private int tiks = 0;
    private int realTik = 0;
    private int amount = 0;
    private int tntCount = 1;
    private double speed;
    private double speedInc;

    private ArenaStatus status;

    private int score;
    private List<Player> players;


    public ArenaLogic(ArenaSettings arena, Plugin plugin) {
        this.plugin = plugin;
        this.arena = arena;

        this.speed = arena.getSpeed();
        this.speedInc = arena.getSpeedInc();

        this.status = ArenaStatus.UnActive;
        this.players = new ArrayList<>();
        score = 0;

    }

    @Override
    public void run() {
        tiks += 5;
        realTik += 5;

        if (realTik%20 == 0) {
            score++;
            sendToAllPlayersActionBar(replaceScore(Message.SCORE_COUNT.toString()));
            realTik = 0;
        }

        if (tiks >= 40 / speed*tntCount) {
            tiks = 0;

            speedLogic();

            for (int i = 0; i < tntCount; i++) {
                List<PlatformJump> activePlatforms = new ArrayList<>();
                for (PlatformJump platformJump : allPlatforms)
                    if (platformJump.isActive()) activePlatforms.add(platformJump);
                if (activePlatforms.size() <= 1) return;

                if (new Random().nextBoolean()) {
                    Random random = new Random();
                    int index = random.nextInt(players.size());
                    Player player = players.get(index);
                    LinkedHashMap<PlatformJump, Double> distance = new LinkedHashMap<>();
                    for (PlatformJump active: activePlatforms) {
                        distance.put(active, player.getLocation().distance(active.getCenter()));
                    }
                    tntTrow(new ArrayList<>(getSortedMap(distance).keySet()).get(0));
                    continue;
                }
                Random random = new Random();
                int index = random.nextInt(activePlatforms.size());


                tntTrow(activePlatforms.get(index));

            }
        }
    }

    private LinkedHashMap<PlatformJump, Double> getSortedMap(Map<PlatformJump, Double> map) {
        return map.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    public void speedLogic() {
        amount+=1;

        if (10 > 30 / speed*tntCount) {
            tntCount++;
        }

        if (amount%20 == 0) {
            speed = speed + speedInc;
            sendToAllPlayers(Message.SPEED.toString().replace("{0}", String.format("%.1f",speed)));
        }
    }

    public void tntTrow(PlatformJump platform) {
        TNTPrimed tnt = (TNTPrimed) platform.getWorld().spawnEntity(platform.getCenter().add(0,3,0), EntityType.PRIMED_TNT);
        tnt.setFuseTicks(50);
        PersistentDataContainer container = tnt.getPersistentDataContainer();
        container.set(PersistentDataSaver.JUMP_NAME_SPACE, PersistentDataType.INTEGER, 1);
        platform.setActive(false);

        LastJump.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                LastJump.getInstance(),
               () -> {
                    if (getStatus().equals(ArenaStatus.Active)) {
                        platform.clearArea();
                    }
               },
                50);

        LastJump.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                LastJump.getInstance(),
                () -> {
                    if (getStatus().equals(ArenaStatus.Active)) {
                        platform.regenArea();
                    }
                },
                20L*9);
    }

    public void playerJoin(Player player) {
        if (allPlatforms.size() == 0) {
            player.sendMessage(Message.NOT_BUIlD.toString());
            return;
        }
        if (canJoin(players.size()) && !getStatus().equals(ArenaStatus.Active)) {
            player.sendMessage(Message.JOIN_ARENA.toString().replace("{0}", arena.getName()));
            players.add(player);
            player.setFlying(false);
            player.setAllowFlight(false);
            Random random = new Random();
            int index = random.nextInt(allPlatforms.size());
            player.teleport(allPlatforms.get(index).getCenter().add(0,1,0));
        }

        if (getStatus().equals(ArenaStatus.UnActive) && canStart()) {
            setStatus(ArenaStatus.Prepare);
            new ArenaTimer(this, arena).runTaskTimer(plugin, 0,20);
        }

    }

    public void playerLeave(Player player) {
        if (status != ArenaStatus.Active) {
            players.remove(player);
        } else {
            players.remove(player);
            gameLeavePlayer(player);
        }

        if (arena.getLeaveLoctaion() != null) player.teleport(arena.getLeaveLoctaion());
        else player.teleport(Objects.requireNonNull(player.getLocation().getWorld()).getSpawnLocation());
    }

    public void gameLeavePlayer(Player player) {
        if (players.isEmpty()) {
            ArenaWriter.addScore(player, arena.getName(), score);
            status = ArenaStatus.UnActive;
            player.sendMessage(replaceScore(Message.WIN.toString()));
            cancel();
            refreshArena();
            LastJump.getInstance().getArenaManager().rebutArena(arena.getName());
            return;
        }
        player.sendMessage(replaceScore(Message.LOSE.toString()));
    }

    public String replaceScore(String text) {
        return text
                .replace("%arena_score_min%", AUtility.getMinutes(score))
                .replace("%arena_score_sec%", AUtility.getSeconds(score));
    }

    public void refreshArena() {
        this.allPlatforms = new ArrayList<>();
        for (PlatformJump region: arena.getPlatforms()) {
            this.allPlatforms.add(new PlatformJump(region.toSave()));
        }
        this.speed = arena.getSpeed();
        this.speedInc = arena.getSpeedInc();
        score = 0;

        for (PlatformJump platformJump : allPlatforms) {
            platformJump.regenArea();
        }
    }

    public void sendTitleBar(String title, String subTitle) {
        for (Player player : players) {
            player.sendTitle(title, subTitle);
        }
    }
    public void sendToAllPlayersActionBar(String message) {
        for (Player player: players) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    public void sendToAllPlayers(String message) {
        for (Player player: players) {
            player.sendMessage(message);
        }
    }


    public boolean canStart() {
        return (players.size() >= arena.getMinPlayers() && players.size() <= arena.getMaxPlayers());
    }

    public boolean canJoin(int size) {
        return size < arena.getMaxPlayers();
    }

    public void setStatus(ArenaStatus status) {
        this.status = status;
    }

    public ArenaStatus getStatus() {
        return status;
    }

    public boolean hasPlayer(Player player) {
        for (Player user: players) {
            if (user == player) {
                return true;
            }
        }
        return false;
    }

    public Region getFailRegion() {
        return arena.getFailRegion();
    }

    public String getName() {
        return arena.getName();
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

}
