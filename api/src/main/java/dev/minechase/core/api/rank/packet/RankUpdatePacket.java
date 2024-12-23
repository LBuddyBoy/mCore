package dev.minechase.core.api.rank.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.rank.model.Rank;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RankUpdatePacket extends ServerResponsePacket {

    private final Rank rank;

    @Override
    public String getExecuteServer() {
        return CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getRankHandler().updateRank(this.rank);
        CoreAPI.getInstance().getRankHandler().saveRank(this.rank);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getRankHandler().updateRank(this.rank);
    }

}
