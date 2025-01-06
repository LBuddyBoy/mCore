package dev.minechase.core.api.log.model.impl;

import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import dev.minechase.core.api.punishment.model.Punishment;
import lombok.Getter;
import org.bson.Document;

import java.util.List;

@Getter
public class GrantCreationLog extends CoreLog {

    private final Grant grant;

    public GrantCreationLog(Grant grant) {
        super("&6" + grant.getSenderName() + "&e granted &6" + grant.getTargetName() + "&e the &6" + grant.getInitialRankName() + "&e rank.", CoreLogType.GRANT_CREATED);

        this.grant = grant;
    }

    public GrantCreationLog(Document document) {
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
