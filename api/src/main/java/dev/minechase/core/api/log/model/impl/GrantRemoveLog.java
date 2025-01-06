package dev.minechase.core.api.log.model.impl;

import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import lombok.Getter;
import org.bson.Document;

import java.util.List;

@Getter
public class GrantRemoveLog extends CoreLog {

    private final Grant grant;

    public GrantRemoveLog(Grant grant) {
        super("&6" + grant.getSenderName() + "&e removed &6" + grant.getTargetName() + "'s&e &6" + grant.getInitialRankName() + "&e grant.", CoreLogType.GRANT_CREATED);

        this.grant = grant;
    }

    public GrantRemoveLog(Document document) {
        super(document);
        this.grant = new Grant(Document.parse(document.getString("grant")));
    }

    @Override
    public String getDisplayMaterial() {
        return "BOOK";
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("grant", this.grant.toDocument().toJson());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        for (String s : this.grant.getFancyBreakDown()) {
            log.add(" " + s);
        }

        log.add("&7&m-----------------------");

        return log;
    }

}
