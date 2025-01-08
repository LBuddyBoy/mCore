package dev.minechase.core.api.iphistory.model;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.api.Documented;
import dev.minechase.core.api.api.Informable;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@AllArgsConstructor
@Getter
public class HistoricalIP extends Documented implements Informable {

    private final UUID playerUUID;
    private final String ipAddress;
    private final List<Long> logins;
    private final long originalChangedAt;
    @Setter private long lastChangedAt;

    public HistoricalIP(UUID playerUUID, String ipAddress) {
        this.playerUUID = playerUUID;
        this.ipAddress = ipAddress;
        this.originalChangedAt = System.currentTimeMillis();
        this.lastChangedAt = System.currentTimeMillis();
        this.logins = new ArrayList<>();
    }

    public HistoricalIP(Document document) {
        this.playerUUID = this.deserializeUUID(document.getString("playerUUID"));
        this.ipAddress = document.getString("ipAddress");
        this.originalChangedAt = document.getLong("originalChangedAt");
        this.lastChangedAt = document.getLong("lastChangedAt");
        this.logins = document.getList("logins", Long.class, new ArrayList<>());
    }

    public String getOriginalChangeDate() {
        return APIConstants.SDF.format(new Date(this.originalChangedAt));
    }

    public String getLastChangeDate() {
        return APIConstants.SDF.format(new Date(this.lastChangedAt));
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("playerUUID", this.serializeUUID(this.playerUUID))
                .append("ipAddress", this.ipAddress)
                .append("originalChangedAt", this.originalChangedAt)
                .append("lastChangedAt", this.lastChangedAt)
                .append("logins", this.logins)
                ;
    }

    @Override
    public List<String> getBreakDown() {
        return Arrays.asList(
                "Player: " + UUIDUtils.getName(this.playerUUID),
                "IP: " + this.ipAddress,
                "Originally Changed: " + this.getOriginalChangeDate(),
                "Last Changed At: " + this.getLastChangeDate(),
                "Login Count: " + APIConstants.formatNumber(this.getLogins().size())
        );
    }

    @Override
    public List<String> getFancyBreakDown() {
        return Arrays.asList(
                "&fPlayer&7: &e" + UUIDUtils.getName(this.playerUUID),
                "&fIP&7: &e" + this.ipAddress,
                "&fOriginally Changed&7: &e" + this.getOriginalChangeDate(),
                "&fLast Changed At&7: &e" + this.getLastChangeDate(),
                "&fLogin Count&7: &e" + APIConstants.formatNumber(this.getLogins().size())
        );
    }

}
