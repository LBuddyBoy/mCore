package dev.minechase.core.api.iphistory.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.iphistory.model.HistoricalIP;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HistoricalIPUpdatePacket extends ServerResponsePacket {

    private final HistoricalIP ip;
    private final String executeServer;

    public HistoricalIPUpdatePacket(HistoricalIP ip) {
        this.ip = ip;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getIpHistoryHandler().update(this.ip);
        CoreAPI.getInstance().getIpHistoryHandler().save(this.ip, true);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getIpHistoryHandler().update(this.ip);
    }

}
