package dev.minechase.core.api.rank.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.grant.packet.GrantUpdatePacket;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.rank.model.Rank;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RankDeletePacket extends ServerResponsePacket {

    private final Rank rank;

    @Override
    public String getExecuteServer() {
        return CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getRankHandler().removeRank(this.rank);
        CoreAPI.getInstance().getRankHandler().deleteRank(this.rank);

        CoreAPI.getInstance().getGrantHandler().fetchAllGrants().whenCompleteAsync((grants, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            grants.forEach(grant -> {
                if (grant.getRank() != null) return;

                grant.setRemovedAt(System.currentTimeMillis());
                grant.setRemovedReason("Rank Deleted");
                grant.setRemovedBy(null);

                new GrantUpdatePacket(grant).send();
            });
        });
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getRankHandler().removeRank(this.rank);
    }

}
