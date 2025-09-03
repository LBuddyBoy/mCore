package dev.minechase.core.rest.controller;

import com.google.gson.JsonObject;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.UUIDUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class APIController {

    @GetMapping("/secure")
    public String secureEndpoint() {
        return "✅ Access granted to secure endpoint!";
    }

    @GetMapping("/cache/name/{uuidString}")
    public ResponseEntity<String> secureEndpoint(@PathVariable String uuidString) {
        try {
            UUID playerUUID = UUID.fromString(uuidString);
            String playerName = UUIDUtils.fetchName(playerUUID).getFuture().join();
            JSONObject response = new JSONObject();

            if (playerName == null) {
                response.put("message", "Couldn't locate a player with that uuid.");
                return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
            }

            response.put("message", "Successfully located '" + playerName + "'");
            response.put("playerName", playerName);
            response.put("playerUUID", playerUUID.toString());

            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>("Invalid UUID provided", HttpStatus.BAD_REQUEST);
        }
    }

}
