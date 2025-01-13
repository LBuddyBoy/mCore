package dev.minechase.core.jda.api;

import dev.minechase.core.api.grant.GrantHandler;
import dev.minechase.core.api.grant.model.Grant;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.jda.CoreBot;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;

public class JDAGrantHandler extends GrantHandler {

    @Override
    public void onRankChange(User user, Grant previousGrant, Grant newGrant) {
        CoreBot.getInstance().getSyncHandler().getSyncInformation(user.getUniqueId()).whenCompleteAsync(((information, throwable1) -> {
            if (throwable1 != null) {
                throwable1.printStackTrace();
                return;
            }
            if (information == null) {

                return;
            }

            Rank newRank = newGrant.getRank();

            if (previousGrant != null) {
                Rank oldRank = previousGrant.getRank();

                if (oldRank != null && oldRank.getDiscordRoleId() != null && !oldRank.getDiscordRoleId().isEmpty()) {
                    Role oldRole = CoreBot.getInstance().getGuild().getRoleById(oldRank.getDiscordRoleId());

                    if (oldRole != null) {
                        CoreBot.getInstance().getGuild().removeRoleFromMember(UserSnowflake.fromId(information.getDiscordMemberId()), oldRole).queue();
                    } else {
                        System.out.println("Tried updating " + user.getName() + "'s discord roles, but the old role doesn't exist.");
                    }
                }
            }

            if (newRank != null && newRank.getDiscordRoleId() != null && !newRank.getDiscordRoleId().isEmpty()) {
                Role newRole = CoreBot.getInstance().getGuild().getRoleById(newRank.getDiscordRoleId());

                if (newRole != null) {
                    CoreBot.getInstance().getGuild().addRoleToMember(UserSnowflake.fromId(information.getDiscordMemberId()), newRole).queue();
                } else {
                    System.out.println("Tried updating " + user.getName() + "'s discord roles, but the new role doesn't exist.");
                }
            }
        }));
    }

    @Override
    public void onGrantRemoved(Grant grant) {
        CoreBot.getInstance().getUserHandler().getOrCreateAsync(grant.getTargetUUID()).whenCompleteAsync(((user, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                return;
            }

            CoreBot.getInstance().getSyncHandler().getSyncInformation(grant.getTargetUUID()).whenCompleteAsync(((information, throwable1) -> {
                if (throwable1 != null) {
                    throwable1.printStackTrace();
                    return;
                }
                if (information == null) {

                    return;
                }

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

                CoreBot.getInstance().getGuild().removeRoleFromMember(UserSnowflake.fromId(information.getDiscordMemberId()), role).queue();
            }));
        }));
    }
}
