package dev.minechase.core.api.punishment.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PunishmentUpdatePacket extends ServerResponsePacket {

    private final Punishment punishment;

    @Override
    public String getExecuteServer() {
        return CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getPunishmentHandler().updatePunishment(this.punishment);
        CoreAPI.getInstance().getPunishmentHandler().savePunishment(this.punishment);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getPunishmentHandler().updatePunishment(this.punishment);
    }

}
