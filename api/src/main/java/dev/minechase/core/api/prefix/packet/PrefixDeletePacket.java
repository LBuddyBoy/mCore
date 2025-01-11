package dev.minechase.core.api.prefix.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.prefix.model.Prefix;
import dev.minechase.core.api.tag.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PrefixDeletePacket extends ServerResponsePacket {

    private final Prefix prefix;
    private final String executeServer;

    public PrefixDeletePacket(Prefix prefix) {
        this.prefix = prefix;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getPrefixHandler().removePrefix(this.prefix);
        CoreAPI.getInstance().getPrefixHandler().deletePrefix(this.prefix);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getPrefixHandler().removePrefix(this.prefix);
    }

}
