package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ConversationBuilder;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.report.model.Report;
import dev.minechase.core.api.report.packet.ReportDeletePacket;
import dev.minechase.core.api.report.packet.ReportUpdatePacket;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.util.HeadUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewReportsMenu extends IPagedMenu {

    private boolean viewingRemoved = false;

    @Override
    public String getPageTitle(Player player) {
        return "Reports";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Report report : this.getReports()) {
            buttons.add(new ReportButton(report));
        }

        return buttons;
    }

    @Override
    public Map<Integer, IButton> getGlobalButtons(Player player) {
        return new HashMap<>(){{
            put(5, new SwitchButton());
        }};
    }

    public List<Report> getReports() {
        return CorePlugin.getInstance().getReportHandler().getReports().values()
                .stream()
                .filter(report -> (viewingRemoved == report.isRemoved()))
                .toList();
    }

    @AllArgsConstructor
    public class SwitchButton extends IButton {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemFactory(viewingRemoved ? HeadUtil.DARK_GREEN_BASE_64 : HeadUtil.DARK_RED_BASE_64)
                    .displayName(viewingRemoved ? "<blend:&6;&e>&lView Active Reports</>" : "<blend:&4;&c>&lView Resolved Reports</>")
                    .lore("<blend:&7;&f>&o(( Click to switch views ))</>")
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            viewingRemoved = !viewingRemoved;
            updateMenu(player, true);
        }
    }

    @AllArgsConstructor
    public class ReportButton extends IButton {

        private final Report report;

        @Override
        public ItemStack getItem(Player player) {
            List<String> lore = new ArrayList<>();

            lore.add("&fSender&7: &e" + report.getSenderName());

            if (this.report.isReport()) {
                lore.add("&fTarget&7: &e" + report.getTargetName());
            }

            lore.add("&fReason&7: &e" + report.getReason());
            lore.add("&fSent At&7: &e" + APIConstants.SDF.format(report.getSentAt()));
            lore.add("&fServer&7: &e" + report.getServer());

            lore.add(" ");

            if (report.isRemoved()) {
                lore.add("&cRemoved By: " + report.getRemovedByName());
                lore.add("&cRemoved At: " + report.getRemovedAtDate());
                lore.add("&cRemoved Reason: " + report.getRemovedReason());
                lore.add("&cRemoved On: " + report.getRemovedOn());
                lore.add(" ");
                lore.add("<blend:&7;&f>&o(( Click to permanently delete this report. ))</>");
            } else {
                lore.add("<blend:&7;&f>&o(( Click to resolve this report. ))</>");
            }

            lore.add(" ");

            return new ItemFactory(report.isReport() ? Material.BOOK : Material.PAPER)
                    .displayName("<blend:&6;&e>&l" + this.report.getSenderName() + "'s " + this.report.getType() + "</>")
                    .lore(lore)
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (this.report.isRemoved()) {
                new ReportDeletePacket(this.report).send();
                updateMenu(player, true);
                return;
            }

            player.closeInventory();
            player.beginConversation(new ConversationBuilder(player).stringPrompt("&aType 'cancel' to cancel this process, otherwise type the message you'd like to provide to the player.", (ctx, response) -> {

                if (!response.equalsIgnoreCase("cancel")) {
                    this.report.remove(player.getUniqueId(), response);

                    new ReportUpdatePacket(this.report).send();
                } else {
                    ctx.getForWhom().sendRawMessage(CC.translate("&cProcess cancelled."));
                }

                Tasks.run(() -> openMenu(player));

                return Prompt.END_OF_CONVERSATION;
            }).echo(false).build());
        }
    }

}
