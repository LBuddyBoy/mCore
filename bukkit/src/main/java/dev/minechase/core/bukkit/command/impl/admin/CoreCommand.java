package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.api.util.TimeDuration;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("core|mcore")
public class CoreCommand extends BaseCommand {

    private static final UUID SENDER_UUID = null;
    private static final UUID TARGET_UUID = UUID.fromString("2732a2e3-2641-4888-81e7-de4282debeea");
    private static final String TARGET_IP = "testing_ip";

    @Subcommand("test punishments save")
    @CommandPermission("core.owner")
    public void testPunishmentsSave(CommandSender sender, @Name("amount") int amount) {
        long startedAt = System.currentTimeMillis();

        sender.sendMessage(CC.translate("&aExecuting punishments save test..."));

        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < amount; i++) {
                Punishment punishment = new Punishment(
                        sender instanceof Player senderPlayer ? senderPlayer.getUniqueId() : null,
                        TARGET_UUID,
                        PunishmentType.values()[ThreadLocalRandom.current().nextInt(PunishmentType.values().length)],
                        new TimeDuration("15s").transform(),
                        "Punishments Testing",
                        CorePlugin.getInstance().getServerName(),
                        (sender instanceof Player player ? player.getAddress().getHostName() : null),
                        TARGET_IP,
                        false,
                        false,
                        false
                );


                new PunishmentUpdatePacket(punishment).send();
            }

            sender.sendMessage(CC.translate("&aFinished punishments save test in " + (System.currentTimeMillis() - startedAt) + " ms"));
        }, CoreAPI.POOL);
    }

    @Subcommand("test punishments fetch")
    @CommandPermission("core.owner")
    public void testPunishmentsFetch(CommandSender sender) {
        long startedAt = System.currentTimeMillis();

        sender.sendMessage(CC.translate("&aExecuting punishments fetch test..."));

        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getPunishmentHandler().fetchAllPunishments().whenCompleteAsync((punishments, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                punishments.forEach(punishment -> System.out.println(punishment.getId()));

                sender.sendMessage(CC.translate("&aFinished punishments fetch test in " + (System.currentTimeMillis() - startedAt) + " ms"));
            });
        }, CoreAPI.POOL);
    }

    @Subcommand("test punishments fetchrelating")
    @CommandPermission("core.owner")
    public void testPunishmentsFetchRelating(CommandSender sender) {
        long startedAt = System.currentTimeMillis();

        sender.sendMessage(CC.translate("&aExecuting punishments fetch relating test..."));

        CompletableFuture.runAsync(() -> {
            CorePlugin.getInstance().getPunishmentHandler().fetchPunishmentsRelating(TARGET_UUID, TARGET_IP).whenCompleteAsync((punishments, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                }

                punishments.forEach(punishment -> System.out.println(punishment.getId()));

                sender.sendMessage(CC.translate("&aFinished punishments fetch relating test in " + (System.currentTimeMillis() - startedAt) + " ms"));
            });
        }, CoreAPI.POOL);
    }

}
