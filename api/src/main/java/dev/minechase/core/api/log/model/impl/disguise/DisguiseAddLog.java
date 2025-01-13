package dev.minechase.core.api.log.model.impl.disguise;

import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import dev.minechase.core.api.log.model.TargetLog;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class DisguiseAddLog extends CoreLog implements TargetLog {

    private final UUID actorUUID;
    private final Rank disguiseRank;
    private final String disguiseName;

    public DisguiseAddLog(UUID actorUUID, String disguiseName, Rank rank) {
        super("&6" + UUIDUtils.getName(actorUUID) + " &adisguised&e as &6" + disguiseName + "&e with the &6" + rank.getName() + "&e rank!", CoreLogType.DISGUISE_ADDED);

        this.actorUUID = actorUUID;
        this.disguiseName = disguiseName;
        this.disguiseRank = rank;
    }

    public DisguiseAddLog(Document document) {
        super(document);
        this.actorUUID = this.deserializeUUID(document.getString("actorUUID"));
        this.disguiseRank = new Rank(Document.parse(document.getString("disguiseRank")));
        this.disguiseName = document.getString("disguiseName");
    }

    @Override
    public String getDisplayMaterial() {
        return "LIME_WOOL";
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("actorUUID", this.serializeUUID(this.actorUUID));
        document.put("disguiseRank", this.disguiseRank.toDocument().toJson());
        document.put("disguiseName", this.disguiseName);

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dPlayer UUID&7: &f" + UUIDUtils.getName(this.actorUUID));
        log.add(" &dDisguise Name&7: &f" + this.disguiseName);
        log.add(" &dDisguise Rank&7: &f" + this.disguiseRank.getName() + " &7(" + this.disguiseRank.getId() + ")");
        log.add("&7&m-----------------------");

        return log;
    }

    @Override
    public UUID getTargetUUID() {
        return this.actorUUID;
    }
}
