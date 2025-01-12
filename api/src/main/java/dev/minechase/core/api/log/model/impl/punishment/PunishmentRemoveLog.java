package dev.minechase.core.api.log.model.impl.punishment;

import dev.minechase.core.api.log.model.*;
import dev.minechase.core.api.punishment.model.Punishment;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class PunishmentRemoveLog extends CoreLog implements SenderLog, TargetLog, ScopedLog {

    private final Punishment punishment;

    public PunishmentRemoveLog(String text, Punishment punishment) {
        super(text, CoreLogType.PUNISHMENT_REMOVED);

        this.punishment = punishment;
    }

    public PunishmentRemoveLog(Document document) {
        super(document);
        this.punishment = new Punishment(Document.parse(document.getString("punishment")));
    }

    @Override
    public String getDisplayMaterial() {
        return "RED_DYE";
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("punishment", this.punishment.toDocument().toJson());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        for (String s : this.punishment.getFancyBreakDown()) {
            log.add(" " + s);
        }

        log.add("&7&m-----------------------");

        return log;
    }

    @Override
    public UUID getSenderUUID() {
        return this.punishment.getRemovedBy();
    }

    @Override
    public String getSentOn() {
        return this.punishment.getRemovedOn();
    }

    @Override
    public UUID getTargetUUID() {
        return this.punishment.getTargetUUID();
    }

}
