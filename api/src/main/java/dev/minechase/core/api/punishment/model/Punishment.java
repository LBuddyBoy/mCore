package dev.minechase.core.api.punishment.model;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.api.Documented;
import dev.minechase.core.api.api.IExpirable;
import dev.minechase.core.api.api.IRemovable;
import dev.minechase.core.api.api.ISendable;
import dev.minechase.core.api.punishment.PunishmentHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class Punishment extends Documented implements IRemovable, ISendable, IExpirable {

    private final UUID id, senderUUID, targetUUID;
    private final PunishmentType type;
    private final long sentAt, duration;
    private final String reason, server;

    private List<PunishmentProof> proof = new ArrayList<>();

    private UUID removedBy = null;
    private String removedReason = null;
    private long removedAt = 0L;

    public Punishment(UUID senderUUID, UUID targetUUID, PunishmentType type, long duration, String reason, String server) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
        this.type = type;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.reason = reason;
        this.server = server;
    }

    public Punishment(Document document) {
        this.id = this.deserializeUUID(document.getString("id"));
        this.senderUUID = this.deserializeUUID(document.getString("senderUUID"));
        this.targetUUID = this.deserializeUUID(document.getString("targetUUID"));
        this.type = PunishmentType.valueOf(document.getString("type"));
        this.sentAt = document.getLong("sentAt");
        this.duration = document.getLong("duration");
        this.server = document.getString("server");
        this.reason = document.getString("reason");
        this.proof = APIConstants.GSON.fromJson(document.getString("proof"), PunishmentHandler.PROOF.getType());
        this.removedBy = this.deserializeUUID(document.getString("removedBy"));
        this.removedReason = document.getString("removedReason");
        this.removedAt = document.getLong("removedAt");
    }

    public void supplyProof(UUID senderUUID, String link) {
        this.proof.add(new PunishmentProof(senderUUID, link));
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
        document.put("type", this.type.name());
        document.put("sentAt", this.sentAt);
        document.put("duration", this.duration);
        document.put("reason", this.reason);
        document.put("server", this.server);
        document.put("proof", APIConstants.GSON.toJson(this.proof, PunishmentHandler.PROOF.getType()));
        document.put("removedBy", this.removedBy);
        document.put("removedReason", this.removedReason);
        document.put("removedAt", this.removedAt);

        return document;
    }
}
