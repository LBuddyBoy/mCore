package dev.minechase.core.api.punishment.model;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import dev.minechase.core.api.api.*;
import dev.minechase.core.api.punishment.PunishmentHandler;
import dev.minechase.core.api.util.Symbols;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class Punishment extends Documented implements IRemovable, ISendable, IExpirable, Informable {

    private final UUID id, senderUUID, targetUUID;
    private final PunishmentType type;
    private final long sentAt, duration;
    private final String reason, server, senderIp, targetIp;
    private final boolean shadow, sentSilent, ipRelated;

    private List<PunishmentProof> proof = new ArrayList<>();

    private UUID removedBy = null;
    private String removedReason = null;
    private String removedOn = null;
    private long removedAt = 0L;
    private boolean removedSilent = false;

    public Punishment(UUID senderUUID, UUID targetUUID, PunishmentType type, long duration, String reason, String server, String senderIp, String targetIp, boolean ipRelated, boolean shadow, boolean sentSilent) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
        this.type = type;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.reason = reason;
        this.server = server;
        this.senderIp = senderIp;
        this.targetIp = targetIp;
        this.ipRelated = ipRelated;
        this.shadow = shadow;
        this.sentSilent = sentSilent;
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
        this.senderIp = document.getString("senderIp");
        this.targetIp = document.getString("targetIp");
        this.proof = APIConstants.GSON.fromJson(document.getString("proof"), PunishmentHandler.PROOF.getType());
        this.removedBy = this.deserializeUUID(document.getString("removedBy"));
        this.removedOn = document.getString("removedOn");
        this.removedReason = document.getString("removedReason");
        this.removedAt = document.getLong("removedAt");
        this.shadow = document.getBoolean("shadow", false);
        this.ipRelated = document.getBoolean("ipRelated", false);
        this.sentSilent = document.getBoolean("sentSilent", false);
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
    public boolean isRemovable() {
        return this.type != PunishmentType.KICK;
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
        document.put("senderIp", this.senderIp);
        document.put("targetIp", this.targetIp);
        document.put("proof", APIConstants.GSON.toJson(this.proof, PunishmentHandler.PROOF.getType()));
        document.put("removedBy", this.serializeUUID(this.removedBy));
        document.put("removedOn", this.removedOn);
        document.put("removedReason", this.removedReason);
        document.put("removedAt", this.removedAt);
        document.put("shadow", this.shadow);
        document.put("ipRelated", this.ipRelated);
        document.put("sentSilent", this.sentSilent);

        return document;
    }

    @Override
    public List<String> getBreakDown() {
        List<String> info = new ArrayList<>(Arrays.asList(
                "ID: " + this.id.toString(),
                "Sender: " + this.getSenderName() + (this.senderIp != null ? " (" + this.senderIp + ")" : ""),
                "Target: " + this.getTargetName() + " (" + this.targetIp + ")",
                "Type: " + this.type.name(),
                "Sent At: " + this.getSentAtDate(),
                "Duration: " + (this.isPermanent() ? "Forever" : TimeUtils.formatIntoDetailedString(this.duration)),
                "Reason: " + this.getReason(),
                "Server: " + this.getServer(),
                "Silent: " + (this.isSentSilent() ? "Yes" : "No"),
                "IP Related: " + (this.isIpRelated() ? "Yes" : "No")
        ));

        if (isRemoved()) {
            info.add("Removed At: " + this.getRemovedAtDate());
            info.add("Removed By: " + this.getRemovedByName());
            info.add("Removed For: " + this.getRemovedReason());
            info.add("Removed On: " + this.getRemovedOn());
            info.add("Removed Silently: " + (this.isRemovedSilent() ? "Yes" : "No"));
        }

        return info;
    }

    @Override
    public List<String> getFancyBreakDown() {
        List<String> info = new ArrayList<>(Arrays.asList(
                "&fID&7: &e" + this.id.toString(),
                "&fSender: &e" + this.getSenderName() + (this.senderIp != null ? " (" + this.senderIp + ")" : ""),
                "&fTarget: &e" + this.getTargetName() + " (" + this.targetIp + ")",
                "&fType&7: &e" + this.type.name(),
                "&fSent At&7: &e" + this.getSentAtDate(),
                "&fDuration&7: &e" + (this.isPermanent() ? "Forever" : TimeUtils.formatIntoDetailedString(this.duration)),
                "&fReason&7: &e" + this.getReason(),
                "&fServer&7: &e" + this.getServer(),
                "&fSilent&7: &e" + (this.isSentSilent() ? "Yes" : "No"),
                "&fIP Related&7: &e" + (this.isIpRelated() ? "Yes" : "No")
        ));

        if (isRemoved()) {
            info.add("&cRemoved At: " + this.getRemovedAtDate());
            info.add("&cRemoved By: " + this.getRemovedByName());
            info.add("&cRemoved For: " + this.getRemovedReason());
            info.add("&cRemoved On: " + this.getRemovedOn());
            info.add("&cRemoved Silently: " + (this.isRemovedSilent() ? "Yes" : "No"));
        }

        return info;
    }

    public String getKickMessage() {
        return StringUtils.join(Arrays.asList(
                "&7&m--*-------------------*--",
                "&cYou have been " + (this.ipRelated ? "ip-" : "") + type.getPlural() + " from the MineChase Network",
                "&cDuration " + Symbols.ARROWS_RIGHT + " " + this.getDurationString(),
                "&cReason " + Symbols.ARROWS_RIGHT + " " + this.getReason(),
                "&7&m--*-------------------*--"
        ), "\n");
    }

    public String getAltKickMessage(UUID causedBy) {
        return StringUtils.join(Arrays.asList(
                "&7&m--*-------------------*--",
                "&cYou have been " + (this.ipRelated ? "ip-" : "") + type.getPlural() + " from the MineChase Network",
                "&cDuration " + Symbols.ARROWS_RIGHT + " " + this.getDurationString(),
                "&cReason " + Symbols.ARROWS_RIGHT + " " + this.getReason(),
                " ",
                "&cThis is due to ip relations with: " + UUIDUtils.getName(causedBy),
                "&7&m--*-------------------*--"
        ), "\n");
    }

}
