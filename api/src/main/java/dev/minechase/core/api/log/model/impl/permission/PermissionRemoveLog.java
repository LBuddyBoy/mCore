package dev.minechase.core.api.log.model.impl.permission;

import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.log.model.*;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class PermissionRemoveLog extends CoreLog implements SenderLog, TargetLog, ScopedLog {

    private final ScopedPermission permission;

    public PermissionRemoveLog(ScopedPermission permission) {
        super("&6" + permission.getSenderName() + "&e removed &6" + permission.getTargetName() + "'s&e &6" + permission.getPermissionNode() + "&e permission.", CoreLogType.PERMISSION_REMOVED);

        this.permission = permission;
    }

    public PermissionRemoveLog(Document document) {
        super(document);
        this.permission = new ScopedPermission(Document.parse(document.getString("permission")));
    }

    @Override
    public String getDisplayMaterial() {
        return "BOOK";
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("permission", this.permission.toDocument().toJson());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        for (String s : this.permission.getFancyBreakDown()) {
            log.add(" " + s);
        }

        log.add("&7&m-----------------------");

        return log;
    }

    @Override
    public UUID getSenderUUID() {
        return this.permission.getRemovedBy();
    }

    @Override
    public String getSentOn() {
        return this.permission.getRemovedOn();
    }

    @Override
    public UUID getTargetUUID() {
        return this.permission.getTargetUUID();
    }

}
