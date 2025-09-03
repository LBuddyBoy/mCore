package dev.minechase.core.rest.controller;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/{name}/{uuidString}")
    public ResponseEntity<String> secureEndpoint(@PathVariable String name, @PathVariable String uuidString) {
        try {
            UUID playerUUID = UUID.fromString(uuidString);
            User user = CoreAPI.getInstance().getUserHandler().findUser(playerUUID, name);

            if (user == null) {
                return new ResponseEntity<>("Couldn't locate a user.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(user.toDocument().toJson(), HttpStatus.OK);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>("Invalid UUID provided", HttpStatus.BAD_REQUEST);
        }
    }

}
