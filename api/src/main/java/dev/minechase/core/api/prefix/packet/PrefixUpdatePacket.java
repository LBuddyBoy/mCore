package dev.minechase.core.api.prefix.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.prefix.model.Prefix;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PrefixUpdatePacket extends ServerResponsePacket {

    private final Prefix prefix;
    private final String executeServer;

    public PrefixUpdatePacket(Prefix prefix) {
        this.prefix = prefix;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getPrefixHandler().updatePrefix(this.prefix);
        CoreAPI.getInstance().getPrefixHandler().savePrefix(this.prefix);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getPrefixHandler().updatePrefix(this.prefix);
    }

}
