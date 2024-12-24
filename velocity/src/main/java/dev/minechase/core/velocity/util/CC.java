package dev.minechase.core.velocity.util;

import dev.lbuddyboy.commons.api.util.GradientUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dev.lbuddyboy.commons.api.util.GradientUtils.colorMap;

public class CC {
    
    public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .flattener(ComponentFlattener.basic())
            .useUnusualXRepeatedCharacterHexFormat()
            .character('&')
            .hexCharacter('#')
            .build();

    private static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m", "§l", "§n", "§o", "§k", "§m");

    public static final Pattern HEX_PATTERN1 = Pattern.compile("&#(\\w{5}[0-9a-f])");
    public static final Pattern HEX_PATTERN2 = Pattern.compile("#(\\w{5}[0-9a-f])");

    public static TextComponent translate(String s) {
        s = s.replaceAll(";true>", ">&l");
        s = s.replaceAll(";false>", ">");

        for (String[] symbol : GradientUtils.extractTextBetweenSymbols(s)) {
            String raw = symbol[0];
            String font = "";

            for (String code : SPECIAL_COLORS) {
                if (!raw.contains(code)) continue;

                font += code;
            }

            s = s.replace(raw, blend(symbol[3], symbol[1], symbol[2], font));
        }

        s = s.replaceAll("&#", "#");

        Matcher matcher = HEX_PATTERN1.matcher(s);
        StringBuilder buffer = new StringBuilder();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, GradientUtils.toLegacy(GradientUtils.hexToRGB("#" + matcher.group(1))).toString());
        }

        String matched = matcher.appendTail(buffer).toString();

        matcher = HEX_PATTERN2.matcher(matched);
        buffer = new StringBuilder();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, GradientUtils.toLegacy(GradientUtils.hexToRGB("#" + matcher.group(1))).toString());
        }

        return SERIALIZER.deserialize(matcher.appendTail(buffer).toString());
    }

    public static String translate(TextComponent component) {
        return SERIALIZER.serialize(component);
    }

    public static String translate(Component component) {
        return SERIALIZER.serialize(component);
    }

    public static String blend(String text, String gradient1, String gradient2) {
        return GradientUtils.blend(text, gradient1, gradient2);
    }

    public static String blend(String text, String gradient1, String gradient2, boolean bold) {
        return GradientUtils.blend(text, gradient1, gradient2, bold);
    }

    public static String blend(String text, String gradient1, String gradient2, String font) {
        return GradientUtils.blend(text, gradient1, gradient2, font);
    }

    public static String format(String string, Object... format) {
        for (int i = 0; i < format.length; i += 2) {
            string = string.replace((String) format[i], String.valueOf(format[i + 1]));
        }
        return string;
    }

    public static List<TextComponent> translate(List<String> lore) {
        return lore.stream().map(CC::translate).collect(Collectors.toList());
    }

    public static List<String> translate(List<String> lore, Object... objects) {
        return lore.stream().map(s -> format(s, objects)).collect(Collectors.toList());
    }

}
