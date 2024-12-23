package dev.minechase.core.api.punishment.model;

import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
public class PunishmentProof {

    private final UUID senderUUID;
    private String link;
    private long appliedAt;

    public PunishmentProof(UUID senderUUID, String link) {
        this.senderUUID = senderUUID;
        this.link = link;
    }

}
