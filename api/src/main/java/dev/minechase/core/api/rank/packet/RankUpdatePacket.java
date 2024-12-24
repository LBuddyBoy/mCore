package dev.minechase.core.api.rank.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.rank.model.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RankUpdatePacket extends ServerResponsePacket {

    private final Rank rank;
    private final String executeServer;

    public RankUpdatePacket(Rank rank) {
        this.rank = rank;
        this.executeServer = CoreAPI.getInstance().getServerName();
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
