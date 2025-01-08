package dev.minechase.core.api.log.model.impl.rank;

import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import dev.minechase.core.api.log.model.SenderLog;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class RankCreationLog extends CoreLog implements SenderLog {

    private final UUID actorUUID;
    private final String initialRankName;
    private final UUID rankId;

    public RankCreationLog(UUID actorUUID, Rank rank) {
        super("&6" + UUIDUtils.getName(actorUUID) + " &acreated &ethe '" + rank.getName() + "' rank!", CoreLogType.RANK_CREATED);

        this.actorUUID = actorUUID;
        this.rankId = rank.getId();
        this.initialRankName = rank.getName();
    }

    public RankCreationLog(Document document) {
        super(document);
        this.actorUUID = this.deserializeUUID(document.getString("actorUUID"));
        this.rankId = this.deserializeUUID(document.getString("rankId"));
        this.initialRankName = document.getString("initialRankName");
    }

    @Override
    public String getDisplayMaterial() {
        return "LIME_DYE";
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("actorUUID", this.serializeUUID(this.actorUUID));
        document.put("rankId", this.serializeUUID(this.rankId));
        document.put("initialRankName", this.initialRankName);

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        log.add(" &dSender&7: &f" + UUIDUtils.getName(this.actorUUID));
        log.add(" &dRank Name&7: &f" + this.initialRankName);
        log.add(" &dRank ID&7: &f" + this.rankId);
        log.add("&7&m-----------------------");

        return log;
    }

    @Override
    public UUID getSenderUUID() {
        return this.actorUUID;
    }
}
