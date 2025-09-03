package dev.minechase.core.api.user.model;

import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.log.model.impl.disguise.DisguiseAddLog;
import dev.minechase.core.api.log.model.impl.disguise.DisguiseRemoveLog;
import dev.minechase.core.api.prefix.model.Prefix;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.tag.model.Tag;
import dev.minechase.core.api.user.packet.UserUpdatePacket;
import lombok.Data;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class User {

    private final UUID uniqueId;
    private String name, currentIpAddress, currentServer;
    private long firstJoinAt = Long.MAX_VALUE;
    private Grant activeGrant;
    private UserMetadata persistentMetadata = new UserMetadata();
    private List<String> pendingMessages = new ArrayList<>();
    private UUID disguiseRank;
    private String disguiseName, disguiseSkinTextures, disguiseSkinSignature;

    private transient boolean changedIps;
    private transient UserMetadata localMetadata = new UserMetadata();

    public User(Document document) {
        this.uniqueId = UUID.fromString(document.getString("uniqueId"));
        this.name = document.getString("name");

        load(document);
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

    public Prefix getActivePrefix() {
        UUID activePrefix = this.persistentMetadata.getUUID(ACTIVE_PREFIX_KEY);

        return CoreAPI.getInstance().getPrefixHandler().getLocalPrefixes().get(activePrefix);
    }

    public Rank getRank() {
        if (this.activeGrant == null) return CoreAPI.getInstance().getRankHandler().getDefaultRank();

        return this.activeGrant.getRank();
    }

    public String getColoredName() {
        Rank rank = this.getRank();

        return rank == null ? "&f" + this.name : "<blend:" + rank.getPrimaryColor() + ";" + rank.getSecondaryColor() + ">" + this.getEditedName() + "</>";
    }

    public String getEditedName() {
        return isDisguised() ? this.disguiseName : this.name;
    }

    public boolean isDisguised() {
        return this.disguiseName != null && this.disguiseRank != null && this.disguiseSkinTextures != null && this.disguiseSkinSignature != null;
    }

    public String getDisplayName() {
        Rank rank = this.getRank();

        return rank == null ? "&f" + this.name : rank.getPrefix() + this.name + rank.getSuffix();
    }

    public String getChatDisplay() {
        Rank rank = this.getRank();

        if (isDisguised() && CoreAPI.getInstance().getRankHandler().getRankById(this.disguiseRank) != null) {
            rank = CoreAPI.getInstance().getRankHandler().getRankById(this.disguiseRank);
        }

        String displayName = rank.getPrefix() + this.getEditedName() + rank.getSuffix();
        String tag = this.getActiveTag() == null ? "" : this.getActiveTag().getSuffix();
        String prefix = this.getActivePrefix() == null ? "" : this.getActivePrefix().getPrefix();

        return prefix + displayName + tag;
    }

    public void undisguise() {
        Rank rank = CoreAPI.getInstance().getRankHandler().getRankById(this.disguiseRank);

        if (rank != null) {
            new DisguiseRemoveLog(this.uniqueId, this.disguiseName, rank).createLog();
        }

        this.disguiseName = null;
        this.disguiseRank = null;
        this.disguiseSkinSignature = null;
        this.disguiseSkinTextures = null;
    }

    public void disguise(String name, String disguiseSkinTextures, String disguiseSkinSignature, Rank rank) {
        this.disguiseName = name;
        this.disguiseRank = rank.getId();
        this.disguiseSkinTextures = disguiseSkinTextures;
        this.disguiseSkinSignature = disguiseSkinSignature;

        new DisguiseAddLog(this.uniqueId, name, rank).createLog();
    }

    public CompletableFuture<Grant> updateActiveGrant() {
        return CoreAPI.getInstance().getGrantHandler().getGrants(this.uniqueId).thenApplyAsync(grants -> {
            List<Grant> sortedGrants = grants.stream().filter(
                    other -> other.isValidLocal() && !other.isRemoved() && !other.isExpired()
            ).sorted(Comparator.comparingInt(Grant::getWeight)).toList();

            if (sortedGrants.isEmpty()) {
                if (this.activeGrant != null && !this.activeGrant.isRemoved()) {
                    return this.getActiveGrant();
                }

                Grant grant = Grant.DEFAULT_GRANT(this.uniqueId);
                setActiveGrant(grant);
                save(true);
                CoreAPI.getInstance().getPermissionHandler().updatePermissions(this.uniqueId);
                return grant;
            }

            Grant selected = sortedGrants.getFirst();

            if (this.getActiveGrant().getRankId().equals(selected.getRankId())) return this.getActiveGrant();

            CoreAPI.getInstance().getGrantHandler().onRankChange(this, this.activeGrant, selected);
            setActiveGrant(selected);
            save(true);
            CoreAPI.getInstance().getPermissionHandler().updatePermissions(this.uniqueId);

            return selected;
        });
    }

    public void update() {
        CoreAPI.getInstance().getUserHandler().getCache().save(this.uniqueId, toDocument().toJson());
    }

    public void save(boolean async) {
        if (CoreAPI.getInstance().isProxy()) {
            if (async) {
                CompletableFuture.runAsync(() -> save(false), CoreAPI.POOL);
                return;
            }

            CoreAPI.getInstance().getUserHandler().saveUser(this);
            return;
        }

        new UserUpdatePacket(this).send();
    }

    public void save() {
        this.save(false);
    }

    public void load(Document document) {
        this.firstJoinAt = document.getLong("firstJoinedAt");
        this.activeGrant = new Grant(Document.parse(document.getString("activeGrant")));
        this.currentIpAddress = document.getString("currentIpAddress");
        this.persistentMetadata = APIConstants.GSON.fromJson(document.getString("persistentMetadata"), METADATA.getType());
        this.pendingMessages = document.getList("pendingMessages", String.class, new ArrayList<>());
        this.disguiseRank = (document.getString("disguiseRank") == null ? null : UUID.fromString(document.getString("disguiseRank")));
        this.disguiseSkinTextures = document.getString("disguiseSkinTextures");
        this.disguiseSkinSignature = document.getString("disguiseSkinSignature");
        this.disguiseName = document.getString("disguiseName");
        this.currentServer = document.getString("currentServer");

        this.updateActiveGrant();
    }

    public Document toDocument() {
        Document document = new Document();

        document.put("uniqueId", this.uniqueId.toString());
        document.put("name", this.name);
        document.put("currentServer", this.currentServer);
        document.put("currentIpAddress", this.currentIpAddress);
        document.put("firstJoinedAt", this.firstJoinAt);
        document.put("pendingMessages", this.pendingMessages);
        document.put("activeGrant", this.activeGrant.toDocument().toJson());
        document.put("persistentMetadata", APIConstants.GSON.toJson(this.persistentMetadata, METADATA.getType()));
        document.put("disguiseRank", (this.disguiseRank == null ? null : this.disguiseRank.toString()));
        document.put("disguiseSkinTextures", this.disguiseSkinTextures);
        document.put("disguiseSkinSignature", this.disguiseSkinSignature);
        document.put("disguiseName", this.disguiseName);

        return document;
    }

    public String getHeadTexture() {
        return this.persistentMetadata.getOrDefault(
                HEAD_TEXTURE_KEY,
                "eyJ0aW1lc3RhbXAiOjE1ODc4MjU0NzgwNDcsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2E1ODg4YWEyZDdlMTk5MTczYmEzN2NhNzVjNjhkZTdkN2Y4NjJiMzRhMTNiZTMyNDViZTQ0N2UyZjIyYjI3ZSJ9fX0="
        );
    }

    public String getHeadSignature() {
        return this.persistentMetadata.getOrDefault(
                HEAD_SIGNATURE_KEY,
                "Yt6VmTAUTbpfGQoFneECtoYcbu7jcARAwZu2LYWv3Yf1MJGXv6Bi3i7Pl9P8y1ShB7V1Q2HyA1bce502x1naOKJPzzMJ0jKZfEAKXnzaFop9t9hXzgOq7PaIAM6fsapymYhkkulRIxnJdMrMb2PLRYfo9qiBJG+IEbdj8MTSvWJO10xm7GtpSMmA2Xd0vg5205hsj0OxSdgxf1uuWPyRaXpPZYDUU05/faRixDKti86hlkBs/v0rttU65r1UghkftfjK0sJoPpk9hABvkw4OjXVFb63wcb27KPhIiSHZzTooSxjGNDniauCsF8Je+fhhMebpXeba1R2lZPLhkHwazNgZmTCKbV1M/a8BDHN24HH9okJpQOR9SPCPOJrNbK+LTPsrR06agj+H/yvYq0ZMJTF6IE6C3KJqntPJF1NQvJM0/YegPPtzpbT/7O1cd4JBCVmguhadOFYvrxqCKHcmaYdkyMJtnGub/5sCjJAG7fZadACftwLnmdBZoQRcNKQMubpdUjuzF8g6C03MiZkeNBUgqkfVjXi7DqpmB0ZvTttp34vy7EIBCo3Hfj15779nGs8SoTw9V2zZc+LgiVPjWF6tffjWkgzLq8K2Cndu6RDlWGJWmrztN/X9lIiLdn8GEfSSGY983n0C91x8mkpOKSfAWPnSZd7NuHU5GaoMvyE="
        );
    }

    public static final String SYNCED_KEY = "discord_synced";
    public static final String ACTIVE_TAG_KEY = "active_tag";
    public static final String ACTIVE_PREFIX_KEY = "active_prefix";
    public static final String HEAD_TEXTURE_KEY = "head_texture";
    public static final String HEAD_SIGNATURE_KEY = "head_signature";
    public static final TypeToken<UserMetadata> METADATA = new TypeToken<>() {
    };

}
