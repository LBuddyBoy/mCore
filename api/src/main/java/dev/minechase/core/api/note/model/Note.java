package dev.minechase.core.api.note.model;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.*;
import dev.minechase.core.api.rank.model.Rank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class Note extends Documented implements IScoped, IRemovable, ISendable, IExpirable, Informable {

    private final UUID id, senderUUID, targetUUID;
    private final String reason;
    private final long sentAt, duration;
    private final List<String> scopes = new ArrayList<>();
    private final String sentOn;

    private UUID removedBy = null;
    private String removedReason = null;
    private String removedOn = null;
    private long removedAt = Long.MAX_VALUE;

    public Note(UUID senderUUID, UUID targetUUID, String reason) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
        this.reason = reason;
        this.sentOn = CoreAPI.getInstance().getServerName();
        this.sentAt = System.currentTimeMillis();
        this.duration = new TimeDuration("perm").transform();
        this.scopes.add("GLOBAL");
    }

    public Note(Document document) {
        this.id = this.deserializeUUID(document.getString("id"));
        this.senderUUID = this.deserializeUUID(document.getString("senderUUID"));
        this.targetUUID = this.deserializeUUID(document.getString("targetUUID"));
        this.sentOn = document.getString("sentOn");
        this.reason = document.getString("reason");
        this.sentAt = document.getLong("sentAt");
        this.duration = document.getLong("duration");
        this.scopes.addAll(document.getList("scopes", String.class, new ArrayList<>()));
        this.removedBy = this.deserializeUUID(document.getString("removedBy"));
        this.removedOn = document.getString("removedOn");
        this.removedReason = document.getString("removedReason");
        this.removedAt = document.getLong("removedAt");
    }

    public void remove(UUID removedBy, String removedReason) {
        this.removedBy = removedBy;
        this.removedReason = removedReason;
        this.removedAt = System.currentTimeMillis();
        this.removedOn = CoreAPI.getInstance().getServerName();
    }

    @Override
    public Document toDocument() {
        Document document = new Document();

        document.put("id", this.id.toString());
        document.put("senderUUID", this.serializeUUID(this.senderUUID));
        document.put("targetUUID", this.targetUUID.toString());
        document.put("sentOn", this.sentOn);
        document.put("reason", this.reason);
        document.put("sentAt", this.sentAt);
        document.put("duration", this.duration);
        document.put("scopes", this.scopes);
        document.put("removedBy", this.serializeUUID(this.removedBy));
        document.put("removedOn", this.removedOn);
        document.put("removedReason", this.removedReason);
        document.put("removedAt", this.removedAt);

        return document;
    }

    @Override
    public List<String> getBreakDown() {
        List<String> info = new ArrayList<>(Arrays.asList(
                "ID: " + this.id.toString(),
                "Sender: " + this.getSenderName(),
                "Target: " + this.getTargetName(),
                "Sent At: " + this.getSentAtDate(),
                "Scopes&7: " + StringUtils.join(this.getScopes(), ", "),
                "Duration: " + (this.isPermanent() ? "Forever" : TimeUtils.formatIntoDetailedString(this.duration)),
                "Reason: " + this.getReason()
        ));

        if (isRemoved()) {
            info.add("Removed At: " + this.getRemovedAtDate());
            info.add("Removed By: " + this.getRemovedByName());
            info.add("Removed For: " + this.getRemovedReason());
            info.add("Removed On: " + this.getRemovedOn());
        }

        return info;
    }

    @Override
    public List<String> getFancyBreakDown() {
        List<String> info = new ArrayList<>(Arrays.asList(
                "&fID&7: &e" + this.id.toString(),
                "&fSender&7: &e" + this.getSenderName(),
                "&fTarget&7: &e" + this.getTargetName(),
                "&fSent At&7: &e" + this.getSentAtDate(),
                "&fScopes&7: &e" + StringUtils.join(this.getScopes(), ", "),
                "&fDuration&7: &e" + (this.isPermanent() ? "Forever" : TimeUtils.formatIntoDetailedString(this.duration)),
                "&fReason&7: &e" + this.getReason()
        ));

        if (isRemoved()) {
            info.add("&cRemoved At: " + this.getRemovedAtDate());
            info.add("&cRemoved By: " + this.getRemovedByName());
            info.add("&cRemoved For: " + this.getRemovedReason());
            info.add("&cRemoved On: " + this.getRemovedOn());
        }

        return info;
    }

}
