package dev.minechase.core.api.grant.grant;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.*;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.punishment.model.PunishmentProof;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.rank.model.Rank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class Grant extends Documented implements IScoped, IRemovable, ISendable, IExpirable {

    public static Grant DEFAULT_GRANT(UUID targetUUID) {
        return new Grant(
                null,
                targetUUID,
                CoreAPI.getInstance().getRankHandler().getDefaultRank(),
                -1L,
                "Default Grant",
                new MultiScope("GLOBAL")
        );
    }

    private final UUID id, senderUUID, targetUUID;
    private final UUID rankId;
    private final long sentAt, duration;
    private final List<String> scopes = new ArrayList<>();
    private final String initialRankName, reason;
    private final boolean removable;

    private UUID removedBy = null;
    private String removedReason = null;
    private long removedAt = 0L;

    public Grant(UUID senderUUID, UUID targetUUID, Rank rank, long duration, String reason, MultiScope scope) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
        this.initialRankName = rank.getName();
        this.reason = reason;
        this.rankId = rank.getId();
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.scopes.addAll(scope.getScopes());
        this.removable = !rank.isDefaultRank();
    }

    public Grant(Document document) {
        this.id = this.deserializeUUID(document.getString("id"));
        this.senderUUID = this.deserializeUUID(document.getString("senderUUID"));
        this.targetUUID = this.deserializeUUID(document.getString("targetUUID"));
        this.rankId = this.deserializeUUID(document.getString("rankId"));
        this.reason = document.getString("reason");
        this.initialRankName = document.getString("initialRankName");
        this.sentAt = document.getLong("sentAt");
        this.duration = document.getLong("duration");
        this.removable = document.getBoolean("removable");
        this.scopes.addAll(document.getList("scopes", String.class, new ArrayList<>()));
        this.removedBy = this.deserializeUUID(document.getString("removedBy"));
        this.removedReason = document.getString("removedReason");
        this.removedAt = document.getLong("removedAt");
    }

    public void remove(UUID removedBy, String removedReason) {
        this.removedBy = removedBy;
        this.removedReason = removedReason;
        this.removedAt = System.currentTimeMillis();
    }

    @Override
    public Document toDocument() {
        Document document = new Document();

        document.put("id", this.id.toString());
        document.put("senderUUID", this.serializeUUID(this.senderUUID));
        document.put("targetUUID", this.targetUUID.toString());
        document.put("initialRankName", this.initialRankName);
        document.put("removable", this.removable);
        document.put("reason", this.reason);
        document.put("rankId", this.rankId.toString());
        document.put("sentAt", this.sentAt);
        document.put("duration", this.duration);
        document.put("scopes", this.scopes);
        document.put("removedBy", this.removedBy);
        document.put("removedReason", this.removedReason);
        document.put("removedAt", this.removedAt);

        return document;
    }

    public int getWeight() {
        return getRank() == null ? Integer.MAX_VALUE : getRank().getWeight();
    }

    public Rank getRank() {
        return CoreAPI.getInstance().getRankHandler().getRankById(this.rankId);
    }

    public String getInitialRankName() {
        return this.getRank() == null ? this.initialRankName : this.getRank().getDisplayName();
    }

}
