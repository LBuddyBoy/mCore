package dev.minechase.core.bukkit.packet;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.GlobalChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GlobalChatMessagePacket extends ServerResponsePacket {

    private final GlobalChatMessage chatMessage;
    private final String executeServer;

    public GlobalChatMessagePacket(GlobalChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        this.executeServer = CoreAPI.getInstance().getServerName();
    }

    @Override
    public void onReceiveExecuteServer() {

    }

    @Override
    public void onReceiveOtherServer() {
        
    }

}
