package dev.minechase.core.api.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ScopedPermission implements IScoped, IExpirable {

    private final String permissionNode;
    private final long sentAt;
    private final long duration;
    private final List<String> scopes = new ArrayList<>();

    public ScopedPermission(String permissionNode, long duration) {
        this.permissionNode = permissionNode;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.scopes.add("GLOBAL");
    }

    public ScopedPermission(String permissionNode, long duration, MultiScope scope) {
        this.permissionNode = permissionNode;
        this.sentAt = System.currentTimeMillis();
        this.duration = duration;
        this.scopes.addAll(scope.getScopes());
    }

}
