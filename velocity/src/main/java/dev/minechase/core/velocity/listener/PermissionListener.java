package dev.minechase.core.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import dev.minechase.core.velocity.api.CorePermissionProvider;

public class PermissionListener {

    private final CorePermissionProvider permissionProvider;

    public PermissionListener() {
        this.permissionProvider = new CorePermissionProvider();
    }

    @Subscribe
    public void onPermSetup(PermissionsSetupEvent event) {
        event.setProvider(this.permissionProvider);
    }

}
