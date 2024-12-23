package dev.minechase.core.api.user.model;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.rank.model.Rank;
import lombok.Data;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class User {

    private final UUID uniqueId;
    private String name;
    private long firstJoinAt;
    private Grant activeGrant;

    public User(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public User(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public boolean hasPlayedBefore() {
        return this.firstJoinAt > 0;
    }

    public Rank getRank() {
        return this.activeGrant.getRank();
    }

    public void updateActiveGrant() {
        CoreAPI.getInstance().getGrantHandler().getGrants(this.activeGrant.getTargetUUID()).whenCompleteAsync((grants, throwable) -> {
            List<Grant> sortedGrants = grants.stream().filter(
                    other -> other.isValidLocal() && !other.isRemoved() && !other.isExpired()
            ).sorted(Comparator.comparingInt(Grant::getWeight)).toList();

            if (sortedGrants.isEmpty()) {
                setActiveGrant(Grant.DEFAULT_GRANT(this.uniqueId));
                save(true);
                return;
            }

            Grant selected = sortedGrants.getFirst();

            if (this.getActiveGrant().equals(selected)) return;

            setActiveGrant(selected);
            save(true);
        });
    }

    public void save(boolean async) {
        if (async) {
            CompletableFuture.runAsync(() -> save(true), CoreAPI.POOL);
            return;
        }

        CoreAPI.getInstance().getUserHandler().saveUser(this);
    }

}
