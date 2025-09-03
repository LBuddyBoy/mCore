package dev.minechase.core.jda.command.impl;

import dev.minechase.core.api.sync.packet.discord.DiscordSyncInformationRemovePacket;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.jda.CoreBot;
import dev.minechase.core.jda.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Collections;

public class SyncResetCommand extends Command {

    public SyncResetCommand() {
        super("syncreset",
                Collections.emptyList(),
                Collections.singletonList(
                        new OptionData(OptionType.USER, "user", "User that is synced.", true, false)
                ),
                DefaultMemberPermissions.enabledFor(Permission.VIEW_CHANNEL), "Resets your sync information.", " <user>");
    }

    @Override
    public void send(SlashCommandInteractionEvent event) {
        if (event.getName().equals("syncreset")) {
            OptionMapping mentioned = event.getInteraction().getOption("user");

            event.deferReply(true).queue();

            if (mentioned == null) {
                event.getHook().sendMessage("Please provide a user.").setEphemeral(true).queue();
                return;
            }

            User user = mentioned.getAsUser();

            CoreBot.getInstance().getDiscordSyncHandler().getSyncInformation(user.getId()).whenCompleteAsync((information, throwable) -> {
                if (information == null) {
                    event.getHook().sendMessage("That user is not synced.").setEphemeral(true).queue();
                    return;
                }

                event.getHook().sendMessage("You reset " + user.getAsMention() + "'s sync information.").setEphemeral(true).queue();

                new DiscordSyncInformationRemovePacket(information).send();

                CoreBot.getInstance().getUserHandler().getOrCreateAsync(information.getPlayerUUID()).whenCompleteAsync((coreUser, throwable1) -> {
                    if (throwable1 != null) {
                        throwable1.printStackTrace();
                        return;
                    }

                    CoreBot.getInstance().getGrantHandler().onGrantRemoved(coreUser.getActiveGrant());
                });

                new PlayerMessagePacket(Arrays.asList(
                        "&cYour account is no longer synced with " + user.getEffectiveName()
                ), information.getPlayerUUID()).send();
            });
        }
    }
}
