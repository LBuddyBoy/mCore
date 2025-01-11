package dev.minechase.core.bukkit.util.totp;

import dev.lbuddyboy.commons.api.util.StringUtils;
import dev.lbuddyboy.commons.util.CC;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.Arrays;

public class DisclaimerPrompt extends StringPrompt {

	@Override
	public String getPromptText(ConversationContext context) {
		return CC.translate(StringUtils.join(Arrays.asList(
				"&c&lTake a minute to read over this, it's important. 2FA can be enabled to protect against hackers getting into your Minecraft account. If you enable 2FA, you'll be required to enter a code every time you log in. If you lose your 2FA device, you won't be able to log in to the network.",
				"&7If you've read the above and would like to proceed, type \"yes\" in chat. Otherwise, type anything else."
		), " "));
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		if (input.equalsIgnoreCase("yes")) {
			return new ScanMapPrompt();
		}

		context.getForWhom().sendRawMessage(CC.translate("&aAborted 2FA setup."));

		return Prompt.END_OF_CONVERSATION;
	}

}