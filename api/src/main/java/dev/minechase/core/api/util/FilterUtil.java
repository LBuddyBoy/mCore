package dev.minechase.core.api.util;

import dev.minechase.core.api.CoreAPI;

import java.util.List;
import java.util.regex.Pattern;

public class FilterUtil {

    private static final Pattern IP_OR_DOMAIN_PATTERN = Pattern.compile(
            "\\b(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,})\\b"
    );

    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "\\b\\d{1,5}\\s(?:[A-Za-z0-9.-]+\\s)*?(?:Street|St|Avenue|Ave|Road|Rd|Boulevard|Blvd|Lane|Ln|Drive|Dr|Court|Ct|Way|Terrace|Terr|Place|Pl)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public static List<String> getDisallowedNames() {
        return CoreAPI.getInstance().getChatHandler().getFilterList();
    }

    public static boolean isDisallowed(String name) {
        for (String word : name.split(" ")) {
            if (word.toLowerCase().contains("minechase.net")) {
                continue;
            }

            if (getDisallowedNames().stream().anyMatch(s -> word.toLowerCase()
                    .replaceAll("0", "o")
                    .replaceAll("1", "i")
                    .replaceAll("3", "e").equalsIgnoreCase(s))) {
                return true;
            }

            if (IP_OR_DOMAIN_PATTERN.matcher(word).find()) {
                return true;
            }

            if (ADDRESS_PATTERN.matcher(name).find()) {
                return true;
            }
        }

        if (getDisallowedNames().stream().anyMatch(s -> name.toLowerCase()
                .replaceAll("0", "o")
                .replaceAll("1", "i")
                .replaceAll("3", "e").contains(s.toLowerCase()))) {
            return true;
        }

        return false;
    }
}
