package dev.minechase.core.bukkit.command.impl.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.commons.component.FancyBuilder;
import dev.lbuddyboy.commons.util.CC;
import dev.minechase.core.bukkit.CorePlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("filter")
@CommandPermission("core.command.filter")
public class FilterCommand extends BaseCommand {

    @Subcommand("add")
    public void add(CommandSender sender, @Name("word") String word) {
        List<String> filter = new ArrayList<>(CorePlugin.getInstance().getConfig().getStringList("filter"));

        word = word.toLowerCase();

        if (filter.contains(word)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>'" + word + "' is already filtered.</>"));
            return;
        }

        filter.add(word);
        CorePlugin.getInstance().getConfig().set("filter", filter);
        CorePlugin.getInstance().saveConfig();
        sender.sendMessage(CC.translate("<blend:&2;&a>Added '" + word + "' to the filter list.</>"));
    }

    @Subcommand("remove")
    public void remove(CommandSender sender, @Name("word") String word) {
        List<String> filter = new ArrayList<>(CorePlugin.getInstance().getConfig().getStringList("filter"));

        word = word.toLowerCase();

        if (!filter.contains(word)) {
            sender.sendMessage(CC.translate("<blend:&4;&c>'" + word + "' is not filtered.</>"));
            return;
        }

        filter.remove(word);
        CorePlugin.getInstance().getConfig().set("filter", filter);
        CorePlugin.getInstance().saveConfig();
        sender.sendMessage(CC.translate("<blend:&2;&a>Removed '" + word + "' from the filter list.</>"));
    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        List<String> filter = CorePlugin.getInstance().getConfig().getStringList("filter");

        sender.sendMessage(CC.translate("<blend:&6;&e>&lFilter List</>"));

        if (filter.isEmpty()) {
            sender.sendMessage(CC.translate("&cNone"));
        } else {
            for (String word : CorePlugin.getInstance().getConfig().getStringList("filter")) {
                new FancyBuilder("&e- " + word + " ")
                        .append("&c[Remove]")
                        .hover("&cClick to remove this from the filter.")
                        .click(ClickEvent.Action.RUN_COMMAND, "/filter remove " + word)
                        .send(sender);
            }
        }
    }

}
