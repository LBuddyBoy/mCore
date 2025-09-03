package dev.minechase.core.api.chat.model;

import dev.minechase.core.api.api.Documented;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

@Getter
@Setter
public class ChatSettings extends Documented {

    private String serverName;
    private long slowDelay, slowDuration, slowedAt;
    private long muteDuration, mutedAt;

    public ChatSettings(Document document) {
        super(document);

        this.serverName = document.getString("serverName");
        this.slowDelay = document.getLong("slowDelay");
        this.slowDuration = document.getLong("slowDuration");
        this.slowedAt = document.getLong("slowedAt");
        this.muteDuration = document.getLong("muteDuration");
        this.mutedAt = document.getLong("mutedAt");
    }

    public ChatSettings(String serverName) {
        this.serverName = serverName;
        this.slowDelay = 0L;
        this.slowDuration = 0L;
        this.slowedAt = 0L;
        this.muteDuration = 0L;
        this.mutedAt = 0L;
    }

    public boolean isSlowed() {
        return this.slowedAt > 0 && this.slowDuration > 0 && (this.slowedAt + this.slowDuration) - System.currentTimeMillis() > 0;
    }

    public boolean isMuted() {
        return this.mutedAt > 0 && this.muteDuration > 0 && (this.mutedAt + this.muteDuration) - System.currentTimeMillis() > 0;
    }

    @Override
    public Document toDocument() {
        Document document = new Document();

        document.put("server", this.serverName);
        document.put("slowDelay", this.slowDelay);
        document.put("slowDuration", this.slowDuration);
        document.put("slowedAt", this.slowedAt);
        document.put("muteDuration", this.muteDuration);
        document.put("mutedAt", this.mutedAt);

        return document;
    }

}
