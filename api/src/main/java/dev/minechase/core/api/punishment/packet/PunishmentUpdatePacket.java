package dev.minechase.core.api.punishment.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PunishmentUpdatePacket extends ServerResponsePacket {

    private final Punishment punishment;
    private final String executeServer;

    public PunishmentUpdatePacket(Punishment punishment) {
        this.punishment = punishment;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getPunishmentHandler().updatePunishment(this.punishment);
        CoreAPI.getInstance().getPunishmentHandler().savePunishment(this.punishment, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getPunishmentHandler().updatePunishment(this.punishment);
    }

}
