package dev.minechase.core.bukkit.packet;

import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.packet.ServerResponsePacket;
import dev.minechase.core.api.sync.model.GlobalChatMessage;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.settings.model.impl.GlobalChatSetting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

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
        CoreAPI.getInstance().getWebsiteSyncHandler().postChatMessage(
                chatMessage.getPlayerUUID(),
                chatMessage.getPlayerName(),
                chatMessage.getServerName(),
                chatMessage.getMessage()
        );

        CoreAPI.getInstance().getUserHandler().getOrCreateAsync(this.chatMessage.getPlayerUUID()).whenCompleteAsync((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            ChatColor chatColor = ChatColor.of(user.getPersistentMetadata().getOrDefault("chatcolor", "WHITE"));
            GlobalChatSetting setting = CorePlugin.getInstance().getSettingsHandler().getSetting(GlobalChatSetting.class);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!setting.isEnabled(online.getUniqueId())) continue;

                online.sendMessage(CC.translate(user.getChatDisplay() + "&7: " + chatColor) + chatMessage.getMessage());
            }
        });
    }

    @Override
    public void onReceiveOtherServer() {
        
    }

}
