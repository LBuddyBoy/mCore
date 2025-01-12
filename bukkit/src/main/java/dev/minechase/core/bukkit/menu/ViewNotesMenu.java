package dev.minechase.core.bukkit.menu;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.paged.IPagedMenu;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ConversationBuilder;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.Tasks;
import dev.minechase.core.api.note.model.Note;
import dev.minechase.core.api.note.packet.NoteUpdatePacket;
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

public class ViewNotesMenu extends IPagedMenu {

    private final UUID targetUUID;
    private final List<Note> notes;

    public ViewNotesMenu(UUID targetUUID, List<Note> notes) {
        this.targetUUID = targetUUID;
        this.notes = notes;
    }

    @Override
    public String getPageTitle(Player player) {
        return UUIDUtils.getName(this.targetUUID) + "'s Notes";
    }

    @Override
    public List<IButton> getPageButtons(Player player) {
        List<IButton> buttons = new ArrayList<>();

        for (Note note : this.notes) {
            buttons.add(new NoteButton(note));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class NoteButton extends IButton {

        private final Note note;

        @Override
        public ItemStack getItem(Player player) {
            ItemFactory factory = new ItemFactory(HeadUtil.DARK_GREEN_BASE_64);

            if (this.note.isTemporary()) factory = new ItemFactory(HeadUtil.YELLOW_BASE_64);
            if (this.note.isExpired() || this.note.isRemoved()) factory = new ItemFactory(HeadUtil.DARK_RED_BASE_64);

            factory.displayName("&6" + this.note.getSentAtDate());
            factory.lore(
                    "&7&m--------------------------",
                    "&fMessage&7: " + this.note.getReason(),
                    "&fSender&7: &e" + this.note.getSenderName(),
                    "&fTarget&7: &6" + this.note.getTargetName(),
                    "&7&m--------------------------"
            );

            if (this.note.isRemoved()) {
                factory.addToLore("&fRemoved Reason&7: &c" + this.note.getRemovedReason());
                factory.addToLore("&fRemoved By&7: &c" + this.note.getRemovedByName());
                factory.addToLore("&fRemoved At&7: &c" + this.note.getRemovedAtDate());
                factory.addToLore("&fRemoved On&7: &c" + this.note.getRemovedOn());
                factory.addToLore("&7&m--------------------------");
            } else {
                factory.addToLore("&cClick to remove this note.");
                factory.addToLore("&7&m--------------------------");
            }

            return factory.build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            if (this.note.isExpired() || this.note.isRemoved() || !this.note.isRemovable()) {
                return;
            }

            player.closeInventory();
            player.beginConversation(new ConversationBuilder(player).stringPrompt("&aType 'cancel' to cancel this process, otherwise type the reason of removal.", (ctx, response) -> {

                if (!response.equalsIgnoreCase("cancel")) {
                    this.note.setRemovedBy(player.getUniqueId());
                    this.note.setRemovedAt(System.currentTimeMillis());
                    this.note.setRemovedReason(response);
                    this.note.setRemovedOn(CorePlugin.getInstance().getServerName());

                    new NoteUpdatePacket(this.note).send();
//                    new NoteRemoveLog(this.note).createLog();
                } else {
                    ctx.getForWhom().sendRawMessage(CC.translate("&cProcess cancelled."));
                }

                Tasks.run(() -> openMenu(player));

                return Prompt.END_OF_CONVERSATION;
            }).echo(false).build());
        }
    }

}
