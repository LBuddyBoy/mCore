package dev.minechase.core.bukkit.menu.punishments;

import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ConversationBuilder;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.log.model.impl.permission.PermissionRemoveLog;
import dev.minechase.core.api.permission.packet.PermissionUpdatePacket;
import dev.minechase.core.api.punishment.model.Punishment;
import dev.minechase.core.api.punishment.model.PunishmentType;
import dev.minechase.core.api.punishment.packet.PunishmentUpdatePacket;
import dev.minechase.core.api.util.UUIDUtils;
import dev.minechase.core.bukkit.CorePlugin;
import dev.minechase.core.bukkit.util.HeadUtil;
import lombok.AllArgsConstructor;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ViewPunishmentsMenu extends IPagedMenu {

    private final UUID targetUUID;
    private final List<Punishment> punishments;
    private final PunishmentType type;

    public ViewPunishmentsMenu(UUID targetUUID, List<Punishment> punishments, PunishmentType type) {
        this.targetUUID = targetUUID;
        this.punishments = punishments;
        this.type = type;
    }

    @Override
    public String getPageTitle(Player player) {
        return UUIDUtils.getName(this.targetUUID) + "'s Punishments";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Punishment punishment : this.punishments) {
            if (punishment.getType() != this.type) continue;

            buttons.add(new PunishmentButton(punishment));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdating(Player player) {
        return true;
    }

    @Override
    public long autoUpdateEvery() {
        return 1_000L;
    }

    @AllArgsConstructor
    public class PunishmentButton extends IButton {

        private final Punishment punishment;

        @Override
        public ItemStack getItem(Player player) {
            ItemFactory factory = new ItemFactory(HeadUtil.DARK_GREEN_BASE_64);

            if (this.punishment.isTemporary()) factory = new ItemFactory(HeadUtil.YELLOW_BASE_64);
            if (this.punishment.isExpired() || this.punishment.isRemoved()) factory = new ItemFactory(HeadUtil.DARK_RED_BASE_64);

            factory.displayName("&6" + this.punishment.getSentAtDate());
            factory.lore(
                    "&7&m--------------------------",
                    "&fSender&7: &e" + this.punishment.getSenderName(),
                    "&fTarget&7: &6" + this.punishment.getTargetName(),
                    "&fReason&7: &b" + this.punishment.getReason(),
                    "&fDuration&7: &a" + this.punishment.getDurationString(),
                    "&fServer&7: &b" + this.punishment.getServer(),
                    "&7&m--------------------------"
            );

            if (this.punishment.isRemoved()) {
                factory.addToLore("&fRemoved Reason&7: &c" + this.punishment.getRemovedReason());
                factory.addToLore("&fRemoved By&7: &c" + this.punishment.getRemovedByName());
                factory.addToLore("&fRemoved At&7: &c" + this.punishment.getRemovedAtDate());
                factory.addToLore("&fRemoved On&7: &c" + this.punishment.getRemovedOn());
                factory.addToLore("&7&m--------------------------");
            } else {
                factory.addToLore("&cClick to remove this punishment.");
                factory.addToLore("&7&m--------------------------");
            }

            return factory.build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (this.punishment.isExpired() || this.punishment.isRemoved() || !this.punishment.isRemovable()) {
                return;
            }

            player.closeInventory();
            player.beginConversation(new ConversationBuilder(player).stringPrompt("&aType 'cancel' to cancel this process, otherwise type the reason of removal.", (ctx, response) -> {
                if (!response.equalsIgnoreCase("cancel")) {
                    CorePlugin.getInstance().getPunishmentHandler().unpunish(player, this.punishment.getType(), punishment.getTargetUUID(), response, true);
                } else {
                    ctx.getForWhom().sendRawMessage(CC.translate("&cProcess cancelled."));
                }

                return Prompt.END_OF_CONVERSATION;
            }).echo(false).build());
        }
    }

}
