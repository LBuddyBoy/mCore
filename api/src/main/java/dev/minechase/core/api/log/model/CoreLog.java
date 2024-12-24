package dev.minechase.core.api.log.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.Documented;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.*;

@Getter
public abstract class CoreLog extends Documented {

    private final UUID id;
    private final String action;
    private final CoreLogType type;
    private final long loggedAt;

    public CoreLog(String action, CoreLogType type) {
        this.action = action;
        this.id = UUID.randomUUID();
        this.type = type;
        this.loggedAt = System.currentTimeMillis();
    }

    public CoreLog(Document document) {
        this.action = document.getString("action");
        this.id = UUID.fromString(document.getString("id"));
        this.type = CoreLogType.valueOf(document.getString("type"));
        this.loggedAt = document.getLong("loggedAt");
    }

    public abstract String getDisplayMaterial();

    @Override
    public Document toDocument() {
        Document document = new Document();

        document.put("id", this.id.toString());
        document.put("action", this.action);
        document.put("type", this.type.name());
        document.put("loggedAt", this.loggedAt);

        return document;
    }

    public String getTitle() {
        return "&8[&7CORE LOG&8] " + this.type.getDisplayName();
    }

    public List<String> getLog() {
        return new ArrayList<>(Arrays.asList(
                "&7&m-----------------------",
                " &dAction&7: &f" + this.action,
                " &dDate&7: &f" + APIConstants.SDF.format(new Date(this.loggedAt))
        ));
    }

    public void createLog() {
        CoreAPI.getInstance().getLogHandler().saveLog(this);
    }

}
