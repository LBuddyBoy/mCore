package dev.minechase.core.bukkit;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ConversationBuilder;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.api.MultiScope;
import dev.minechase.core.api.grant.grant.Grant;
import dev.minechase.core.api.grant.packet.GrantUpdatePacket;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CoreConstants {

    public static String STAFF_PERM = "core.staff";
    public static String ADMIN_PERM = "core.admin";
    public static String QUEUE_BYPASS_PERM = "core.queue.bypass";
    public static List<String> BYPASS_LIST = Arrays.asList(
            "2732a2e3-2641-4888-81e7-de4282debeea"
    );

    public static boolean isBypassed(CommandSender sender) {
        if (sender instanceof Player player) {
            return BYPASS_LIST.contains(player.getUniqueId().toString());
        }

        return true;
    }

    public static String INVALID_NAME(AsyncCorePlayer player) {
        return CC.translate("<blend:&4;&c>No player with the name '" + player.getName() + "' exists.</>");
    }

    public static Conversation getGrantDurationConversation(Player player, List<String> scopes, UUID targetUUID, UUID rankId) {
        return new ConversationBuilder(player)
                .stringPrompt("&aPlease type how long you'd like to grant this rank to " + UUIDUtils.getName(targetUUID) + ". Type 'cancel' to stop this process.", (context, response) -> {
                    if (!response.equalsIgnoreCase("cancel")) {
                        TimeDuration duration = new TimeDuration(response);

                        if (duration.transform() <= 0 && !response.equalsIgnoreCase("perm")) {
                            player.sendMessage(CC.translate("&cInvalid duration... &7(You tried: " + response + ")"));
                            return Prompt.END_OF_CONVERSATION;
                        }

                        /*
                        Start the new scope menu that ends with the reason conversation
                         */

                        Tasks.run(() -> player.beginConversation(getGrantReasonConversation(player, scopes, targetUUID, rankId, duration)));
                    }
                    return Prompt.END_OF_CONVERSATION;
                })
                .echo(false)
                .build();
    }

    public static Conversation getGrantReasonConversation(Player sender, List<String> scopes, UUID target, UUID rankId, TimeDuration duration) {
        return new ConversationBuilder(sender)
                .stringPrompt(CC.translate("&aPlease type the reason for granting this rank."), (ctx, r) -> {
                    Rank rank = CorePlugin.getInstance().getRankHandler().getRankById(rankId);

                    if (rank == null) {
                        return Prompt.END_OF_CONVERSATION;
                    }

                    Tasks.run(() -> CorePlugin.getInstance().getGrantHandler().grant(
                            sender,
                            target,
                            rank,
                            scopes,
                            duration.transform(),
                            r
                    ));

                    return Prompt.END_OF_CONVERSATION;
                }).echo(false).build();
    }

}
