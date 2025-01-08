package dev.minechase.core.bukkit.command.impl.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentSnapshot;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.user.model.User;
import dev.minechase.core.api.util.Symbols;
import dev.minechase.core.bukkit.CoreConstants;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.model.AsyncCorePlayer;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CommandAlias("alts")
@CommandPermission("core.command.alts")
public class AltsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void def(CommandSender sender, @Name("player") AsyncCorePlayer player) {
        player.getUser().whenCompleteAsyncExcept(user -> {
            sender.sendMessage(CC.translate("&aLoading " + player.getName() + "'s alts, this may take a few seconds..."));

            CorePlugin.getInstance().getUserHandler().fetchAlts(user).whenCompleteAsync(((users, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                    sender.sendMessage(CC.translate("&cError loading " + player.getName() + "'s alts, check console for more info..."));
                    return;
                }

                FancyBuilder headerBuilder = new FancyBuilder("");
                FancyBuilder altsBuilder = new FancyBuilder("");

                for (PunishmentType type : PunishmentType.values()) {
                    String plural = CC.blend(WordUtils.capitalize(type.getPlural()), type.getPrimaryColor(), type.getSecondaryColor());

                    headerBuilder.append(plural)
                            .hover("&7Player is actively " + type.getPlural() + ".")
                            .append(" ");
                }

                if (users.isEmpty()) {
                    altsBuilder.append("&cNone");
                } else {
                    List<String> alts = new ArrayList<>();
                    int index = 0;

                    for (User alt : users) {
                        boolean isLast = index + 1 >= users.size();

                        CorePlugin.getInstance().getPunishmentHandler().getPunishments(alt.getUniqueId()).whenCompleteAsync((punishments, altThrowable) -> {
                            String name = "&7" + alt.getName();
                            List<String> info = new ArrayList<>();

                            punishments = punishments.stream().sorted(Comparator.comparingInt(punishment -> punishment.getType().ordinal())).toList();

                            for (Punishment punishment : punishments) {
                                if (!punishment.isActive()) continue;
                                if (punishment.getType() == PunishmentType.WARN || punishment.getType() == PunishmentType.KICK) continue;

                                String plural = CC.blend(WordUtils.capitalize(punishment.getType().getPlural()), punishment.getType().getPrimaryColor(), punishment.getType().getSecondaryColor());

                                name = CC.blend(alt.getName(), punishment.getType().getPrimaryColor(), punishment.getType().getSecondaryColor());
                                info.add(plural + "&7: &aYes &7");
                                info.add("&7" + Symbols.ARROWS_RIGHT + " &fDuration&7: &e" + punishment.getDurationString());
                                info.add(" ");
                            }

                            if (info.isEmpty()) info.add("&7No additional info...");

                            alts.add(name);

                            altsBuilder.append(name).hover(info).append(isLast ? "" : "&7, ");
                        });

                        index++;
                    }

                    altsBuilder.append(StringUtils.join(alts, "&7, "));
                }

                sender.sendMessage(" ");
                headerBuilder.send(sender);
                altsBuilder.send(sender);
                sender.sendMessage(" ");
            }));

        }, (throwable -> sender.sendMessage(CoreConstants.INVALID_NAME(player))));
    }

}
