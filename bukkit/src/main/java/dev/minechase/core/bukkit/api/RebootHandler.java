package dev.minechase.core.bukkit.api;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ReminderTask;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RebootHandler implements IModule {

    private long startedAt, rebootEvery;
    private boolean forceStarted = false;
    private long forceStartedAt, forceStartedDuration;
    private RebootTask rebootTask;

    @Override
    public void load() {
        this.startedAt = System.currentTimeMillis();

        reload();
    }

    @Override
    public void unload() {

    }

    public boolean isForceRebooting() {
        return this.forceStarted;
    }

    public void stopReboot() {
        this.forceStarted = false;
        this.forceStartedAt = -1L;
        this.forceStartedDuration = 0L;
        this.rebootTask.reminded.clear();

        Arrays.asList(
                " ",
                "<blend:&6;&e>&l" + CorePlugin.getInstance().getServerName() + "</>&f reboot has been &c&lCANCELLED",
                " "
        ).forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
    }

    public void forceReboot(long forceStartedDuration) {
        for (long interval : this.rebootTask.intervals) {
            if (interval >= forceStartedDuration) this.rebootTask.reminded.add(interval);
        }

        this.forceStarted = true;
        this.forceStartedAt = System.currentTimeMillis();
        this.forceStartedDuration = forceStartedDuration;

        Arrays.asList(
                " ",
                "<blend:&6;&e>&l" + CorePlugin.getInstance().getServerName() + "</>&f will be &c&lREBOOTING&f in <blend:&6;&e>" + TimeUtils.formatIntoDetailedString(forceStartedDuration) + "</>",
                " "
        ).forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
    }

    @Override
    public void reload() {
        this.rebootEvery = new TimeDuration(CorePlugin.getInstance().getConfig().getString("rebootEvery")).transform();

        this.rebootTask = new RebootTask();
        this.rebootTask.runTaskTimerAsynchronously(CorePlugin.getInstance(), 20, 20);
    }

    public class RebootTask extends BukkitRunnable {

        private final List<Long> reminded = new ArrayList<>();
        private final long[] intervals = new long[] {
                new TimeDuration("1s").transform(),
                new TimeDuration("2s").transform(),
                new TimeDuration("3s").transform(),
                new TimeDuration("4s").transform(),
                new TimeDuration("5s").transform(),
                new TimeDuration("30s").transform(),
                new TimeDuration("1m").transform(),
                new TimeDuration("5m").transform(),
                new TimeDuration("15m").transform(),
                new TimeDuration("30m").transform(),
                new TimeDuration("1h").transform()
        };

        public long getExpiry() {
            if (forceStarted) return (forceStartedAt + forceStartedDuration) - System.currentTimeMillis();

            return (startedAt + rebootEvery) - System.currentTimeMillis();
        }

        @Override
        public void run() {
            for (long interval : this.intervals) {
                if (!this.reminded.contains(interval) && this.getExpiry() <= interval) {
                    this.reminded.add(interval);
                    Arrays.asList(
                            " ",
                            "<blend:&6;&e>&l" + CorePlugin.getInstance().getServerName() + "</>&f will be &c&lREBOOTING&f in <blend:&6;&e>" + TimeUtils.formatIntoDetailedString(interval) + "</>",
                            " "
                    ).forEach((s) -> Bukkit.broadcastMessage(CC.translate(s)));
                }
            }

            if (this.getExpiry() <= 0L) {
                Bukkit.shutdown();
                this.reminded.clear();
            }

        }
    }

}
