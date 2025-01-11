package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import dev.minechase.core.api.CoreAPI;
import dev.minechase.core.api.api.ScopedPermission;
import dev.minechase.core.api.rank.model.Rank;
import dev.minechase.core.api.tag.model.Tag;

import java.util.Collection;

public class TagContext extends CommonCommandContext<Tag> {

    public TagContext() {
        super("tags", Tag.class);
    }

    @Override
    public Tag getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String source = context.popFirstArg();
        Tag tag = CoreAPI.getInstance().getTagHandler().getTag(source);

        if (tag != null) return tag;

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No tag with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return CoreAPI.getInstance().getTagHandler().getLocalTags().values().stream().map(Tag::getName).toList();
    }

}
