package fr.florianpal.fauction.utils;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.languages.MessageKeys;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static void sendMessage(FAuction plugin, Player player, MessageKeys messageKeys, String... replacements) {

        GlobalConfig globalConfig = plugin.getConfigurationManager().getGlobalConfig();
        if (!globalConfig.getMessageSend().containsKey(messageKeys.name()) || Boolean.TRUE.equals(globalConfig.getMessageSend().get(messageKeys.name()))) {
            CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
            issuerTarget.sendInfo(messageKeys, replacements);
        }
    }
}
