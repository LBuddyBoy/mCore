package dev.minechase.core.rest.service;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.FilterUtil;
import dev.minechase.core.bukkit.packet.StaffMessagePacket;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatProcessingService {

    @Async
    public ResponseEntity<String> processChatAsync(UUID playerUUID, String playerName, String serverName, String chatMessage) {
        User user = CoreAPI.getInstance().getUserHandler().findUser(playerUUID, playerName);

        if (FilterUtil.isDisallowed(chatMessage)) {
            new StaffMessagePacket("&4[Web Filtered Message] &c(" + serverName + ") &b" + user.getName() + "&7: " + chatMessage).send();
            return new ResponseEntity<>("Your message was filtered.", HttpStatus.BAD_REQUEST);
        }

        if (user == null) {
            return new ResponseEntity<>("Error loading your profile.", HttpStatus.BAD_REQUEST);
        }

        CoreAPI.getInstance().getWebsiteSyncHandler().postChatMessage(
                playerUUID,
                playerName,
                serverName,
                chatMessage
        );

        return new ResponseEntity<>("Message posted successfully.", HttpStatus.OK);
    }
}
