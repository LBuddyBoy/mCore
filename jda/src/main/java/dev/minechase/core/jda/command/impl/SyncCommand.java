package dev.minechase.core.jda.command.impl;

import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.model.SyncInformation;
import dev.minechase.core.api.sync.packet.SyncCodeDeletePacket;
import dev.minechase.core.api.sync.packet.SyncInformationUpdatePacket;
import dev.minechase.core.api.sync.packet.UserSyncPacket;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.packet.PlayerMessagePacket;
import dev.minechase.core.jda.CoreBot;
import dev.minechase.core.jda.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.Collections;

public class SyncCommand extends Command {

    public SyncCommand() {
        super("sync",
                Collections.emptyList(),
                Collections.singletonList(
                        new OptionData(OptionType.INTEGER, "code", "Code provided ingame to sync.", true, false)
                ),
                DefaultMemberPermissions.enabledFor(Permission.VIEW_CHANNEL), "Syncs your discord to your minecraft account", " <code>");
    }

    @Override
    public void send(SlashCommandInteractionEvent event) {
        if (event.getName().equals("sync")) {
            String userId = event.getUser().getId();
            OptionMapping mentioned = event.getInteraction().getOption("code");

            event.deferReply(true).queue();

            if (mentioned == null) {
                event.getHook().sendMessage("Code error.").setEphemeral(true).queue();
                return;
            }

            CoreBot.getInstance().getSyncHandler().getSyncInformation(userId).whenCompleteAsync((information, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    event.getHook().sendMessage("Code error.").setEphemeral(true).queue();
                    return;
                }

                if (information != null) {
                    event.getHook().sendMessage("You are already synced to " + UUIDUtils.getName(information.getPlayerUUID()) + ".").setEphemeral(true).queue();
                    return;
                }

                int code = mentioned.getAsInt();
                SyncCode syncCode = CoreBot.getInstance().getSyncHandler().getSyncCode(code);

                if (syncCode == null) {
                    event.getHook().sendMessage("Code error couldn't find a code.").setEphemeral(true).queue();
                    return;
                }

                event.getHook().sendMessage("Found code for " + UUIDUtils.getName(syncCode.getPlayerUUID()) + ".").setEphemeral(true).queue();

                information = new SyncInformation(
                        syncCode.getPlayerUUID(),
                        event.getMember().getId()
                );

                new SyncCodeDeletePacket(syncCode).send();
                new SyncInformationUpdatePacket(information).send();
                new UserSyncPacket(information).send();
                new PlayerMessagePacket(Arrays.asList(
                        "&aYour account is now synced with " + event.getMember().getEffectiveName()
                ), syncCode.getPlayerUUID()).send();
            });
        }
    }
}
