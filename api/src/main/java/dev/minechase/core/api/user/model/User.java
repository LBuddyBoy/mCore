package dev.minechase.core.api.user.model;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.rank.model.Rank;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class User {

    private final UUID uniqueId;
    private String name, currentIpAddress;
    private long firstJoinAt;
    private Grant activeGrant;
    private List<String> ipHistory = new ArrayList<>();
    private UserMetadata persistentMetadata = new UserMetadata();

    private transient boolean changedIps;
    private transient UserMetadata localMetadata = new UserMetadata();

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
        if (this.activeGrant == null) return CoreAPI.getInstance().getRankHandler().getDefaultRank();

        return this.activeGrant.getRank();
    }

    public String getColoredName() {
        return this.getRank() == null ? "&f" + this.name : "<blend:" + this.getRank().getPrimaryColor() + ";" + this.getRank().getSecondaryColor() + ">" + this.name + "</>";
    }

    public void updateActiveGrant() {
        CoreAPI.getInstance().getGrantHandler().getGrants(this.uniqueId).whenCompleteAsync((grants, throwable) -> {
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
            CompletableFuture.runAsync(() -> save(false), CoreAPI.POOL);
            return;
        }

        CoreAPI.getInstance().getUserHandler().saveUser(this);
    }

}
