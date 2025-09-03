package dev.minechase.core.api.api;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter
@Setter
public class ScopedPermission extends Documented implements IScoped, ISendable, IExpirable, IRemovable, Informable {

    private final UUID id, senderUUID, targetUUID;
    private final String permissionNode;
    private final long sentAt;
    private final long duration;
    private final List<String> scopes = new ArrayList<>();
    private final String server;
    private final String reason;

    private UUID removedBy = null;
    private String removedReason = null;
    private String removedOn = null;
    private long removedAt = Long.MAX_VALUE;

    public ScopedPermission(UUID senderUUID, UUID targetUUID, String permissionNode, long duration, String server, String reason) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
        this.permissionNode = permissionNode;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.server = server;
        this.reason = reason;
        this.scopes.add("GLOBAL");
    }

    public ScopedPermission(UUID senderUUID, UUID targetUUID, String permissionNode, long duration, String server, String reason, MultiScope scope) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
        this.permissionNode = permissionNode;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.server = server;
        this.reason = reason;
        this.scopes.addAll(scope.getScopes());
    }

    public ScopedPermission(Document document) {
        this.id = this.deserializeUUID(document.getString("id"));
        this.senderUUID = this.deserializeUUID(document.getString("senderUUID"));
        this.targetUUID = this.deserializeUUID(document.getString("targetUUID"));
        this.permissionNode = document.getString("permissionNode");
        this.server = document.getString("server");
        this.reason = document.getString("reason");
        this.sentAt = document.getLong("sentAt");
        this.duration = document.getLong("duration");
        this.scopes.addAll(document.getList("scopes", String.class, new ArrayList<>()));
        this.removedBy = this.deserializeUUID(document.getString("removedBy"));
        this.removedReason = document.getString("removedReason");
        this.removedOn = document.getString("removedOn");
        this.removedAt = document.getLong("removedAt");
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("id", this.id.toString())
                .append("targetUUID", this.serializeUUID(this.targetUUID))
                .append("permissionNode", this.permissionNode)
                .append("sentAt", this.sentAt)
                .append("duration", this.duration)
                .append("scopes", this.scopes)
                .append("removedBy", this.serializeUUID(this.removedBy))
                .append("removedReason", this.removedReason)
                .append("removedOn", this.removedOn)
                .append("removedAt", this.removedAt)
                ;
    }

    @Override
    public List<String> getBreakDown() {
        List<String> info = new ArrayList<>(Arrays.asList(
                "ID: " + this.id.toString(),
                "Sender: " + this.getSenderName(),
                "Target: " + this.getTargetName(),
                "Permission: " + this.getPermissionNode(),
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
                "&fPermission&7: &e" + this.getPermissionNode(),
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
