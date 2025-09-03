package dev.minechase.core.rest.controller;

import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/rank")
public class RankController {

    @GetMapping("/{id}")
    public ResponseEntity<String> secureEndpoint(@PathVariable String id) {
        try {
            UUID rankUUID = UUID.fromString(id);
            Rank rank = CoreAPI.getInstance().getRankHandler().getRankById(rankUUID);

            if (rank == null) {
                return new ResponseEntity<>("Couldn't locate a rank.", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(rank.toDocument().toJson(), HttpStatus.OK);
        } catch (IllegalArgumentException ignored) {
            return new ResponseEntity<>("Invalid ID provided", HttpStatus.BAD_REQUEST);
        }

    }

}
