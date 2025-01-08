package dev.minechase.core.api.log.model.impl;

import dev.minechase.core.api.iphistory.model.HistoricalIP;
import dev.minechase.core.api.log.model.CoreLog;
import dev.minechase.core.api.log.model.CoreLogType;
import dev.minechase.core.api.log.model.SenderLog;
import dev.minechase.core.api.log.model.TargetLog;
import dev.minechase.core.api.util.UUIDUtils;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.UUID;

@Getter
public class IPChangedLog extends CoreLog implements TargetLog {

    private final HistoricalIP historicalIp;

    public IPChangedLog(HistoricalIP historicalIp) {
        super("&6" + UUIDUtils.getName(historicalIp.getPlayerUUID()) + " &dchanged&e ips!", CoreLogType.IP_CHANGED);

        this.historicalIp = historicalIp;
    }

    public IPChangedLog(Document document) {
        super(document);

        this.historicalIp = new HistoricalIP(Document.parse(document.getString("historicalIp")));
    }

    @Override
    public String getDisplayMaterial() {
        return "STICK";
    }

    @Override
    public Document toDocument() {
        Document document = super.toDocument();

        document.put("historicalIp", this.historicalIp.toDocument().toJson());

        return document;
    }

    @Override
    public List<String> getLog() {
        List<String> log = super.getLog();

        for (String s : this.historicalIp.getFancyBreakDown()) {
            log.add(" " + s);
        }

        log.add("&7&m-----------------------");

        return log;
    }

    @Override
    public UUID getTargetUUID() {
        return this.historicalIp.getPlayerUUID();
    }
}
