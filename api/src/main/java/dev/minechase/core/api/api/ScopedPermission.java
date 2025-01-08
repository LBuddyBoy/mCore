package dev.minechase.core.api.api;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ScopedPermission extends Documented implements IScoped, IExpirable, IRemovable {

    private final UUID id, targetUUID;
    private final String permissionNode;
    private final long sentAt;
    private final long duration;
    private final List<String> scopes = new ArrayList<>();

    private UUID removedBy = null;
    private String removedReason = null;
    private String removedOn = null;
    private long removedAt = 0L;

    public ScopedPermission(UUID targetUUID, String permissionNode, long duration) {
        this.id = UUID.randomUUID();
        this.targetUUID = targetUUID;
        this.permissionNode = permissionNode;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.scopes.add("GLOBAL");
    }

    public ScopedPermission(UUID targetUUID, String permissionNode, long duration, MultiScope scope) {
        this.id = UUID.randomUUID();
        this.targetUUID = targetUUID;
        this.permissionNode = permissionNode;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.scopes.addAll(scope.getScopes());
    }

    public ScopedPermission(Document document) {
        this.id = this.deserializeUUID(document.getString("id"));
        this.targetUUID = this.deserializeUUID(document.getString("targetUUID"));
        this.permissionNode = document.getString("permissionNode");
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
}
