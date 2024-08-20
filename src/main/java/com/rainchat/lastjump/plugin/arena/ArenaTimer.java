package com.rainchat.lastjump.plugin.arena;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.Chat;
import com.rainchat.lastjump.common.utils.general.Message;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaTimer extends BukkitRunnable {

    private ArenaSettings arena;

    private int timer;

    private ArenaLogic jumpPlatformTask;

    public ArenaTimer(ArenaLogic jumpPlatformTask, ArenaSettings arena) {
        this.jumpPlatformTask = jumpPlatformTask;
        this.arena = arena;
        this.timer = arena.getStartTimer();
    }

    @Override
    public void run() {
        if (jumpPlatformTask.canStart()) {
            jumpPlatformTask.sendTitleBar(Message.START.toString(), Chat.color("&a" + timer));
            timer--;
        } else {
            jumpPlatformTask.setStatus(ArenaStatus.UnActive);
            cancel();
        }
        if (timer < 0) {
            jumpPlatformTask.setStatus(ArenaStatus.Active);
            jumpPlatformTask.sendTitleBar(Message.PREPARE_TO_JUMP.toString(),"");

            jumpPlatformTask.refreshArena();
            jumpPlatformTask.runTaskTimer(LastJump.getInstance(), 0,5);
            cancel();
        }
    }

}
