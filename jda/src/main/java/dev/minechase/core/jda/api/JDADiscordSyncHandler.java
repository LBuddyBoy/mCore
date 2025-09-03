package dev.minechase.core.jda.api;

import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.sync.DiscordSyncHandler;
import dev.minechase.core.api.sync.model.SyncInformation;
import dev.minechase.core.jda.CoreBot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;

public class JDADiscordSyncHandler extends DiscordSyncHandler {

    @Override
    public void onUserSynced(SyncInformation information) {
        CoreBot.getInstance().getUserHandler().getOrCreateAsync(information.getPlayerUUID()).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            Grant grant = user.getActiveGrant();
            Rank rank = grant.getRank();

            if (rank == null || rank.getDiscordRoleId() == null || rank.getDiscordRoleId().isEmpty()) {
                System.out.println("Tried updating " + user.getName() + "'s discord roles, but their rank doesn't have an id");
                return;
            }

            Role role = CoreBot.getInstance().getGuild().getRoleById(rank.getDiscordRoleId());

            if (role == null) {
                System.out.println("Tried updating " + user.getName() + "'s discord roles, but that role doesn't exist.");
                return;
            }

            CoreBot.getInstance().getGuild().addRoleToMember(UserSnowflake.fromId(information.getDiscordMemberId()), role).queue();
        }));
    }
}
