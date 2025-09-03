package dev.minechase.core.rest.controller;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.chat.model.ChatSettings;
import dev.minechase.core.api.util.FilterUtil;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.rest.service.ChatProcessingService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatProcessingService chatService;

    public ChatController(ChatProcessingService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("")
    public ResponseEntity<String> secureEndpoint(@RequestBody JSONObject body) {
        try {
            UUID playerUUID = UUID.fromString(body.getString("playerUUID"));
            String playerName = body.getString("playerName");
            String serverName = body.getString("serverName");
            String chatMessage = body.getString("chatMessage");

            return chatService.processChatAsync(playerUUID, playerName, serverName, chatMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<String> filter(@RequestBody JSONObject body) {
        try {
            String message = body.getString("message");
            boolean disallowed = FilterUtil.isDisallowed(message);

            return ResponseEntity.status(disallowed ? HttpStatus.BAD_REQUEST : HttpStatus.OK)
                    .body(disallowed ? "Contains Filtered Words" : "Clean Message");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<String> settings(@RequestBody JSONObject body) {
        try {
            String serverName = body.getString("serverName");
            ChatSettings settings = CoreAPI.getInstance().getChatHandler().getSettings(serverName);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(settings.toDocument().toJson());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        }
    }

}
