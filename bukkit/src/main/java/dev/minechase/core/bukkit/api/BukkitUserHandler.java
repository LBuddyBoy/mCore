package dev.minechase.core.bukkit.api;

import dev.iiahmed.disguise.Disguise;
import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.SkinAPI;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.UserHandler;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitUserHandler extends UserHandler {

    private final DisguiseProvider provider = DisguiseManager.getProvider();

    public void disguise(Player player, Disguise.Builder builder, Rank rank) {
        Tasks.runAsync(() -> {
            User user = this.getUser(player.getUniqueId());
            Disguise disguise = builder.build();

            user.disguise(disguise.getName(), disguise.getTextures(), disguise.getSignature(), rank);
            Tasks.run(() -> provider.disguise(player, disguise));
            user.save(true);
        });
    }

    public void updateDisguise(Player player) {
        User user = this.getUser(player.getUniqueId());
        if (!user.isDisguised()) return;

        Tasks.runAsync(() -> {
            Disguise.Builder builder = Disguise.builder()
                    .setName(user.getDisguiseName())
                    .setSkin(user.getDisguiseSkinTextures(), user.getDisguiseSkinSignature());

            Tasks.run(() -> provider.disguise(player, builder.build()));
        });
    }

    public void undisguise(Player player) {
        User user = this.getUser(player.getUniqueId());

        user.undisguise();
        user.save(true);

        Tasks.run(() -> provider.undisguise(player));
    }

}
