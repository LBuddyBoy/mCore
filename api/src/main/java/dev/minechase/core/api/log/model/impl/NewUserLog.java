package dev.minechase.core.api.log.model.impl;

import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class NewUserLog extends CoreLog {

    private final UUID actorUUID;

    public NewUserLog(UUID actorUUID) {
        super("&6" + UUIDUtils.getName(actorUUID) + " &ajoined&e the server for the first time!", CoreLogType.NEW_USER);

        this.actorUUID = actorUUID;
    }

    public NewUserLog(Document document) {
        super(document);
        this.actorUUID = this.deserializeUUID(document.getString("actorUUID"));
    }

    @Override
    public String getDisplayMaterial() {
        return "PLAYER_HEAD";
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("actorUUID", this.serializeUUID(this.actorUUID));

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dPlayer UUID&7: &f" + UUIDUtils.getName(this.actorUUID));
        log.add("&7&m-----------------------");

        return log;
    }

}
