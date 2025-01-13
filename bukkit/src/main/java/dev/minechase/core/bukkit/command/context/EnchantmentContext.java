package dev.minechase.core.bukkit.command.context;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.command.CommonCommandContext;
import org.bukkit.GameMode;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentContext extends CommonCommandContext<Enchantment> {

    public EnchantmentContext() {
        super("enchantments", Enchantment.class);
    }

    @Override
    public Enchantment getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        String source = arg.popFirstArg();
        Enchantment enchantment = Registry.ENCHANTMENT.match(source);

        if (enchantment != null) return enchantment;

        throw new InvalidCommandArgument(CC.translate("<blend:&4;&c>No enchantment with the name '" + source + "' exists.</>"));
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return Registry.ENCHANTMENT.stream().map(enchantment -> enchantment.getKey().getKey()).toList();
    }
}