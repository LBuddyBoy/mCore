package dev.minechase.core.velocity.motd.model;

import dev.lbuddyboy.commons.api.util.TimeUtils;
import lombok.Data;
import org.bson.Document;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.minechase.core.velocity.motd
 * @since 2/16/2024
 */

@Data
public class MOTDTimer {

    public MOTDTimer(String name, String displayName, String context, long duration) {
        this.name = name;
        this.displayName = displayName;
        this.context = context;
        this.duration = duration;
        this.startedAt = System.currentTimeMillis();
    }

    private final String name;
    private String displayName, context;
    private long startedAt, pausedAt, duration;

    public MOTDTimer(Document document) {
        this.name = document.getString("name");
        this.displayName = document.getString("displayName");
        this.context = document.getString("context");
        this.startedAt = document.getLong("startedAt");
        this.pausedAt = document.getLong("pausedAt");
        this.duration = document.getLong("duration");
    }

    public Document toDocument() {
        return new Document()
                .append("name", this.name)
                .append("displayName", this.displayName)
                .append("context", this.context)
                .append("startedAt", this.startedAt)
                .append("pausedAt", this.pausedAt)
                .append("duration", this.duration);
    }

    public long getExpiry() {
        if (isPaused()) {
            return (this.pausedAt + this.duration) - System.currentTimeMillis();
        }
        return (this.startedAt + this.duration) - System.currentTimeMillis();
    }

    public String getContextFormatted() {
        return this.context
                .replaceAll("%timer-display%", this.displayName)
                .replaceAll("%timer-time-left%", TimeUtils.formatIntoDetailedString((int) (getExpiry() / 1000)))
                .replaceAll("%timer-time-left-hhmmss%", TimeUtils.formatIntoHHMMSS((int) (getExpiry() / 1000)))
                .replaceAll("%timer-time-left-short%", TimeUtils.formatIntoDetailedStringShort((int) (getExpiry() / 1000)))
                .replaceAll("%timer-time-left-shorter%", TimeUtils.formatIntoDetailedStringShorter((int) (getExpiry() / 1000)));
    }

    public boolean isActive() {
        if (isPaused()) return false;

        return getExpiry() > 0;
    }

    public boolean isPaused() {
        return this.pausedAt > 0;
    }

    public void resume() {
        this.startedAt = this.pausedAt;
        this.pausedAt = -1;
    }

    public void pause() {
        this.startedAt = -1;
        this.pausedAt = System.currentTimeMillis();
    }

    public void restart() {
        this.restart(this.duration);
    }

    public void restart(long duration) {
        this.startedAt = System.currentTimeMillis();
        this.duration = duration;
    }

}
