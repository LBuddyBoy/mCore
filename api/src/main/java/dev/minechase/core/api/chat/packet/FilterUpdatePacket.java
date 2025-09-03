package dev.minechase.core.api.filter.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class FilterUpdatePacket extends ServerResponsePacket {

    private final List<String> filter;
    private final String executeServer;

    public FilterUpdatePacket(List<String> filter) {
        this.filter = filter;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {
        CoreAPI.getInstance().getChatHandler().updateFilter(this.filter);
        CoreAPI.getInstance().getChatHandler().saveFilter(this.filter);
    }

    @Override
    public void onReceiveOtherServer() {
        CoreAPI.getInstance().getChatHandler().updateFilter(this.filter);
    }

}
