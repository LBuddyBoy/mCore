package dev.minechase.core.jda.command.impl;

import dev.minechase.core.api.sync.model.SyncCode;
import dev.minechase.core.api.sync.model.SyncInformation;
import dev.minechase.core.api.sync.packet.SyncInformationUpdatePacket;
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
            OptionMapping mentioned = event.getInteraction().getOption("code");

            System.out.println("test 1");
            event.deferReply(true).queue();

            if (mentioned == null) {
                event.getHook().sendMessage("Code error.").setEphemeral(true).queue();
                return;
            }

            int code = mentioned.getAsInt();
            SyncCode syncCode = CoreBot.getInstance().getSyncHandler().getSyncCode(code);

            if (syncCode == null) {
                event.getHook().sendMessage("Code error couldn't find a code.").setEphemeral(true).queue();
                return;
            }

            event.getHook().sendMessage("Found code for " + UUIDUtils.getName(syncCode.getPlayerUUID()) + ".").setEphemeral(true).queue();

            new SyncInformationUpdatePacket(new SyncInformation(
                    syncCode.getPlayerUUID(),
                    event.getMember().getId()
            )).send();

            new PlayerMessagePacket(Arrays.asList(
                    "&aYour account is now synced with " + event.getMember().getEffectiveName()
            ), syncCode.getPlayerUUID()).send();

        }
    }
}
