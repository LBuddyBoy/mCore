package dev.minechase.core.bukkit.listener;

import com.google.gson.JsonArray;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.api.event.AsyncCoreChatEvent;
import dev.minechase.core.api.sync.model.GlobalChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCoreChat(AsyncCoreChatEvent event) {
        Player player = event.getPlayer();
        String serverName = CorePlugin.getInstance().getServerName();

        CompletableFuture.runAsync(() -> CoreAPI.getInstance().getWebsiteSyncHandler().postChatMessage(
                player.getUniqueId(),
                player.getName(),
                serverName,
                event.getMessage()
        ), CoreAPI.POOL);
    }

}
