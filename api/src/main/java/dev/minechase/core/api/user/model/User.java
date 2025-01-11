package dev.minechase.core.api.user.model;

import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.tag.model.Tag;
import lombok.Data;
import org.bson.Document;

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
    private UserMetadata persistentMetadata = new UserMetadata();

    private transient boolean changedIps;
    private transient UserMetadata localMetadata = new UserMetadata();

    public User(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public User(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.activeGrant = Grant.DEFAULT_GRANT(this.uniqueId);
    }

    public boolean hasPlayedBefore() {
        return this.firstJoinAt > 0;
    }

    public Tag getActiveTag() {
        UUID activeTag = this.persistentMetadata.getUUID(ACTIVE_TAG_KEY);

        return CoreAPI.getInstance().getTagHandler().getLocalTags().get(activeTag);
    }

    public Rank getRank() {
        if (this.activeGrant == null) return CoreAPI.getInstance().getRankHandler().getDefaultRank();

        return this.activeGrant.getRank();
    }

    public String getColoredName() {
        Rank rank = this.getRank();

        return rank == null ? "&f" + this.name : "<blend:" + rank.getPrimaryColor() + ";" + rank.getSecondaryColor() + ">" + this.name + "</>";
    }

    public String getDisplayName() {
        Rank rank = this.getRank();

        return rank == null ? "&f" + this.name : rank.getPrefix() + this.name + rank.getSuffix();
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

            CoreAPI.getInstance().getGrantHandler().onRankChange(this, this.activeGrant, selected);
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

    public void load(Document document) {
        this.firstJoinAt = document.getLong("firstJoinedAt");
        this.activeGrant = new Grant(Document.parse(document.getString("activeGrant")));
        this.currentIpAddress = document.getString("currentIpAddress");
        this.persistentMetadata = APIConstants.GSON.fromJson(document.getString("persistentMetadata"), METADATA.getType());

        this.updateActiveGrant();
    }

    public Document toDocument() {
        Document document = new Document();

        document.put("uniqueId", this.uniqueId.toString());
        document.put("name", this.name);
        document.put("currentIpAddress", this.currentIpAddress);
        document.put("firstJoinedAt", this.firstJoinAt);
        document.put("activeGrant", this.activeGrant.toDocument().toJson());
        document.put("persistentMetadata", APIConstants.GSON.toJson(this.persistentMetadata, METADATA.getType()));

        return document;
    }

    public static final String ACTIVE_TAG_KEY = "active_tag";
    public static final TypeToken<UserMetadata> METADATA = new TypeToken<>() {};

}
