package dev.minechase.core.bukkit.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class WordUtil {

    public static List<String> wrapText(String text, int maxLineLength) {
        List<String> wrappedText = new ArrayList<>();
        String[] words = text.split(" ");
        int currentLength = 0;
        String currentString = "";
        String lastColor = "&f";

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            currentLength += ChatColor.stripColor(word).length();
            currentString += word + " ";

            if (currentLength >= maxLineLength || i >= words.length - 1) {
                wrappedText.add(lastColor + currentString);
                lastColor = ChatColor.getLastColors(currentString);
                currentString = "";
                currentLength = 0;
            }
        }

        return wrappedText;
    }

}
