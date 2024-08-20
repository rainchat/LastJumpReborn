package com.rainchat.lastjump.common.utils.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Scheduler {

    private final JavaPlugin plugin;
    private boolean async;
    private boolean cancel;
    private long after;
    private Long every;

    private BukkitRunnable bukkitRunnable;

    /**
     * Creates new instance of this class.
     *
     * @param plugin Plugin.
     * @param async  Async.
     */
    public Scheduler(JavaPlugin plugin, boolean async) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null!");
        this.async = async;
        this.cancel = false;
    }

    /**
     * Checks will scheduler run as async?
     *
     * @return If scheduler run as async, returns true.
     */
    public boolean isAsync() {
        return this.async;
    }

    /**
     * Sets async mode of scheduler.
     *
     * @param async Async mode.
     * @return This class.
     */
    public Scheduler async(boolean async) {
        this.async = async;
        return this;
    }

    /**
     * Runs how many ticks later.
     *
     * @param after Ticks.
     * @return This class.
     */
    public Scheduler after(long after) {
        this.after = after;
        return this;
    }

    /**
     * Runs how many ticks later.
     *
     * @param after Ticks.
     * @return This class.
     */
    public Scheduler addAfter(long after) {
        this.after += after;
        return this;
    }


    /**
     * Runs every how many ticks.
     *
     * @param every Ticks.
     * @return This class.
     */
    public Scheduler every(long every) {
        this.every = every;
        return this;
    }

    /**
     * Checks will scheduler is cancel?
     *
     * @return If scheduler is cancel, returns true.
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * Sets calncel mode of scheduler.
     *
     * @return This class.
     */
    public void setCancel(boolean cancel) {
        this.cancel = cancel;
        this.bukkitRunnable.cancel();
    }

    /**
     * Runs how many ticks later.
     *
     * @param after    Ticks.
     * @param timeUnit Time unit.
     * @return This class.
     */

    public Scheduler after(int after, TimeUnit timeUnit) {
        this.after = timeUnit.toSeconds(after) / 20;
        return this;
    }

    /**
     * Runs every how many ticks.
     *
     * @param every    Ticks.
     * @param timeUnit Time unit.
     * @return This class.
     */

    public Scheduler every(int every, TimeUnit timeUnit) {
        this.every = timeUnit.toSeconds(every) / 20;
        return this;
    }

    /**
     * Starts to scheduler.
     *
     * @param runnable Callback.
     * @return This class.
     */

    public synchronized Scheduler run(Runnable runnable) {
        return this.run(consumer -> runnable.run());
    }

    /**
     * Starts to scheduler.
     *
     * @param taskConsumer Callback.
     * @return This class.
     */

    public synchronized Scheduler run(Consumer<BukkitRunnable> taskConsumer) {

        this.bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                taskConsumer.accept(this);
            }
        };

        if (cancel) return this;

        if (this.async) {
            if (this.every == null) bukkitRunnable.runTaskLaterAsynchronously(this.plugin, this.after);
            else bukkitRunnable.runTaskTimerAsynchronously(this.plugin, this.after, this.every);
        } else {
            if (this.every == null) bukkitRunnable.runTaskLater(this.plugin, this.after);
            else bukkitRunnable.runTaskTimer(this.plugin, this.after, this.every);
        }

        return this;
    }
}
