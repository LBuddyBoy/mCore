package dev.minechase.core.api.permission.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.packet.ServerResponsePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PermissionUpdatePacket extends ServerResponsePacket {

    private final ScopedPermission permission;
    private final String executeServer;

    public PermissionUpdatePacket(ScopedPermission permission) {
        this.permission = permission;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getPermissionHandler().update(this.permission);
        CoreAPI.getInstance().getPermissionHandler().updatePermissions(this.permission.getTargetUUID());
        CoreAPI.getInstance().getPermissionHandler().save(this.permission, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getPermissionHandler().update(this.permission);
        CoreAPI.getInstance().getPermissionHandler().updatePermissions(this.permission.getTargetUUID());
    }

}
