package dev.minechase.core.api.rank.model;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.Documented;
import dev.minechase.core.api.api.IScoped;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.rank.RankHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class Rank extends Documented implements IScoped {

    private final UUID id;
    private String name, displayName, primaryColor, secondaryColor;
    private int weight;
    private boolean staffRank, defaultRank;
    private String materialString;
    private final List<UUID> inheritedRanks = new ArrayList<>();
    private final List<ScopedPermission> permissions = new ArrayList<>();
    private final List<String> scopes = new ArrayList<>();;

    public Rank(String name) {
        this.id = UUID.randomUUID();
        this.name = name.toLowerCase();
        this.displayName = this.name;
        this.primaryColor = "&7";
        this.secondaryColor = "&f";
        this.weight = 1000;
        this.staffRank = false;
        this.defaultRank = false;
        this.materialString = "WHITE_WOOL";
        this.scopes.add("GLOBAL");
    }

    public Rank(Document document) {
        super(document);

        this.id = this.deserializeUUID(document.getString("id"));
        this.name = document.getString("name");
        this.displayName = document.getString("displayName");
        this.primaryColor = document.getString("primaryColor");
        this.secondaryColor = document.getString("secondaryColor");
        this.weight = document.getInteger("weight");
        this.materialString = document.getString("materialString");
        this.staffRank = document.getBoolean("staffRank", false);
        this.defaultRank = document.getBoolean("defaultRank", false);
        this.permissions.addAll(APIConstants.GSON.fromJson(document.getString("permissions"), RankHandler.SCOPED_PERMISSIONS.getType()));
        this.inheritedRanks.addAll(document.getList("inheritedRanks", String.class, new ArrayList<>()).stream().map(UUID::fromString).toList());
        this.scopes.addAll(document.getList("scopes", String.class, new ArrayList<>()));
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("id", this.id.toString())
                .append("name", this.name)
                .append("displayName", this.displayName)
                .append("primaryColor", this.primaryColor)
                .append("secondaryColor", this.secondaryColor)
                .append("materialString", this.materialString)
                .append("staffRank", this.staffRank)
                .append("defaultRank", this.defaultRank)
                .append("weight", this.weight)
                .append("permissions", APIConstants.GSON.toJson(this.permissions, RankHandler.SCOPED_PERMISSIONS.getType()))
                .append("inheritedRanks", this.inheritedRanks.stream().map(UUID::toString).toList())
                .append("scopes", this.scopes)
                ;
    }

    public ScopedPermission getPermission(String permissionNode) {
        return this.permissions.stream().filter(permission -> permission.getPermissionNode().equals(permissionNode)).findFirst().orElse(null);
    }

    public List<ScopedPermission> getActivePermissions() {
        return this.permissions.stream().filter(permission -> !permission.isExpired()).toList();
    }

    public boolean hasPermission(String permissionNode) {
        return this.getActivePermissions().stream().anyMatch(permission -> permission.getPermissionNode().equals(permissionNode));
    }

    public List<UUID> getValidInheritedRanks() {
        return this.inheritedRanks.stream().filter(rankId -> CoreAPI.getInstance().getRankHandler().getRanks().containsKey(rankId)).toList();
    }

}
