package dev.minechase.core.bukkit.mod.model;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.minechase.core.bukkit.CorePlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

@AllArgsConstructor
@Getter
public enum ModItem {

    FREEZE(new ItemFactory(Material.ICE).displayName("<blend:&6;&e>Freeze Player</>").build(), 1, ((player, target) -> {
        player.chat("/freeze " + target.getName());
    })),

    INSPECT(new ItemFactory(Material.BOOK).displayName("<blend:&6;&e>Inspect Player</>").build(), 2, ((player, target) -> {
        player.chat("/invsee " + target.getName());
    })),

    REPORTS(new ItemFactory(Material.BOOKSHELF).displayName("<blend:&6;&e>Reports</>").build(), 3, ((player, target) -> {
        player.chat("/reports");
    })),

    ONLINE_STAFF(new ItemFactory(Material.PLAYER_HEAD).displayName("<blend:&6;&e>Online Staff</>").build(), 5, ((player, target) -> {
        player.chat("/onlinestaff");
    })),

    THRU_COMPASS(new ItemFactory(Material.COMPASS).displayName("<blend:&6;&e>Thru Compass</>").build(), 7, ((player, target) -> {

    })),

    RANDOM_TP(new ItemFactory(Material.CLOCK).displayName("<blend:&6;&e>Random Teleport</>").build(), 8, ((player, target) -> {
        player.chat("/rtp");
    })),

    SPECTATOR(new ItemFactory(Material.SPYGLASS).displayName("<blend:&6;&e>Spectator Mode</>").build(), 9, ((player, target) -> {
        player.setGameMode(GameMode.SPECTATOR);
        player.sendMessage(CC.translate("<blend:&2;&a>Spectator mode is now active. Run /staff again to get back to normal.</>"));
    }));

    private final ItemStack item;
    private final int slot;
    private final BiConsumer<Player, Player> consumer;

}
