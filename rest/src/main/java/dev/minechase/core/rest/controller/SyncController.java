package dev.minechase.core.rest.controller;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.model.WebsiteSyncInformation;
import dev.minechase.core.api.sync.packet.discord.DiscordSyncCodeDeletePacket;
import dev.minechase.core.api.sync.packet.discord.DiscordSyncInformationUpdatePacket;
import dev.minechase.core.api.sync.packet.discord.UserDiscordSyncPacket;
import dev.minechase.core.api.sync.packet.website.WebsiteDiscordSyncPacket;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncCodeDeletePacket;
import dev.minechase.core.api.sync.packet.website.WebsiteSyncInformationUpdatePacket;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    @GetMapping("/code/{codeId}")
    public ResponseEntity<String> code(@PathVariable Integer codeId) {
        try {
            SyncCode code = CoreAPI.getInstance().getWebsiteSyncHandler().getSyncCode(codeId);

            if (code == null) {
                return new ResponseEntity<>("Couldn't locate that code.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(code.toDocument().toJson(), HttpStatus.OK);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>("Invalid code provided", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/synced/{uuidString}")
    public ResponseEntity<String> synced(@PathVariable String uuidString) {
        try {
            UUID playerUUID = UUID.fromString(uuidString);
            WebsiteSyncInformation syncInformation = CoreAPI.getInstance().getWebsiteSyncHandler().getSyncInformation(playerUUID).join();
            JSONObject response = new JSONObject();

            if (syncInformation != null) {
                response.put("synced", true);
                response.put("information", syncInformation.toDocument().toJson());
            } else {
                response.put("synced", false);
            }

            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>("Invalid UUID provided", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{codeId}/{email}/{websiteId}")
    public ResponseEntity<String> secureEndpoint(@PathVariable Integer codeId, @PathVariable String email, @PathVariable String websiteId) {
        try {
            SyncCode code = CoreAPI.getInstance().getWebsiteSyncHandler().getSyncCode(codeId);
            JSONObject response = new JSONObject();

            if (code == null) {
                response.put("message", "Couldn't locate that code.");
                return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
            }

            WebsiteSyncInformation syncInformation = CoreAPI.getInstance().getWebsiteSyncHandler().getSyncInformation(code.getPlayerUUID()).join();

            if (syncInformation != null) {
                response.put("message", "That player is already synced to a website account.");
                return new ResponseEntity<>(response.toString(), HttpStatus.EXPECTATION_FAILED);
            }

            syncInformation = CoreAPI.getInstance().getWebsiteSyncHandler().getSyncInformation(websiteId).join();

            if (syncInformation != null) {
                response.put("message", "Website User is already synced.");
                return new ResponseEntity<>(response.toString(), HttpStatus.EXPECTATION_FAILED);
            }

            syncInformation = new WebsiteSyncInformation(code.getPlayerUUID(), websiteId);

            new WebsiteSyncCodeDeletePacket(code).send();
            new WebsiteSyncInformationUpdatePacket(syncInformation).send();
            new WebsiteDiscordSyncPacket(syncInformation).send();
            new PlayerMessagePacket(Arrays.asList(
                    "&aYour account is now synced with '" + email + "' website account."
            ), code.getPlayerUUID()).send();

            response.put("information", syncInformation.toDocument().toJson());
            response.put("message", "Successfully synced to " + UUIDUtils.getName(syncInformation.getPlayerUUID()) + "!");

            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>("Invalid UUID provided", HttpStatus.BAD_REQUEST);
        }
    }

}
