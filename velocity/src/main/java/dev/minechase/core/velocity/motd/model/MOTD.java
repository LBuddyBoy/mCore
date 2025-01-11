package dev.minechase.core.velocity.motd.model;

import com.velocitypowered.api.util.Favicon;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LBuddyBoy Development
 * @file dev.minechase.core.velocity.util
 * @since 2/16/2024
 */

@AllArgsConstructor
@Getter
public class MOTD {

    private final String line1, line2, legacyLine1, legacyLine2;
    private final Favicon favicon;

}
