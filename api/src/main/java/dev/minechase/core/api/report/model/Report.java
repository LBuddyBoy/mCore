package dev.minechase.core.api.report.model;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.Documented;
import dev.minechase.core.api.api.IRemovable;
import dev.minechase.core.api.api.ISendable;
import lombok.Getter;
import org.bson.Document;

import java.util.Arrays;
import java.util.UUID;

@Getter
public class Report extends Documented implements ISendable, IRemovable {

    private final UUID id, senderUUID;
    private final String server, reason;
    private final long sentAt;
    private UUID targetUUID;

    private UUID removedBy;
    private long removedAt;
    private String removedReason;
    private String removedOn;

    /**
     *
     * Use this constructor for requests
     *
     * @param senderUUID player uuid sending the report
     * @param reason reason for sending the report
     */

    public Report(UUID senderUUID, String server, String reason) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.server = server;
        this.reason = reason;
        this.sentAt = System.currentTimeMillis();
    }

    /**
     *
     * Use this constructor for reports
     *
     * @param senderUUID player uuid sending the report
     * @param reason reason for sending the report
     * @param targetUUID target being reported
     */

    public Report(UUID senderUUID, String server, String reason, UUID targetUUID) {
        this.id = UUID.randomUUID();
        this.senderUUID = senderUUID;
        this.server = server;
        this.reason = reason;
        this.targetUUID = targetUUID;
        this.sentAt = System.currentTimeMillis();
    }

    public Report(Document document) {
        this.id = this.deserializeUUID(document.getString("id"));
        this.senderUUID = this.deserializeUUID(document.getString("senderUUID"));
        this.server = document.getString("server");
        this.reason = document.getString("reason");
        this.targetUUID = this.deserializeUUID(document.getString("targetUUID"));
        this.sentAt = document.getLong("sentAt");
        this.removedAt = document.getLong("removedAt");
        this.removedBy = this.deserializeUUID(document.getString("removedBy"));
        this.removedReason = document.getString("removedReason");
        this.removedOn = document.getString("removedOn");
    }

    public boolean isReport() {
        return this.targetUUID != null;
    }

    public boolean isRequest() {
        return this.targetUUID == null;
    }

    public String getType() {
        return this.isReport() ? "report" : "request";
    }

    public void remove(UUID removedBy, String removedReason) {
        this.removedBy = removedBy;
        this.removedReason = removedReason;
        this.removedAt = System.currentTimeMillis();
        this.removedOn = CoreAPI.getInstance().getServerName();

        CoreAPI.getInstance().getUserHandler().getOrCreateAsync(this.senderUUID).whenCompleteAsync((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            user.getPendingMessages().add(StringUtils.join(Arrays.asList(
                    " ",
                    "&eYour &c" + this.getType() + "&e for &6'" + this.reason + "'&e has been resolved.",
                    "&eMessage&7: &c" + this.removedReason,
                    " "
            ), "\n"));
        });
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("id", this.serializeUUID(this.id))
                .append("senderUUID", this.serializeUUID(this.senderUUID))
                .append("server", this.server)
                .append("reason", this.reason)
                .append("targetUUID", this.serializeUUID(this.targetUUID))
                .append("sentAt", this.sentAt)
                .append("removedBy", this.serializeUUID(this.removedBy))
                .append("removedAt", this.removedAt)
                .append("removedReason", this.removedReason)
                .append("removedOn", this.removedOn)
                ;
    }

}
