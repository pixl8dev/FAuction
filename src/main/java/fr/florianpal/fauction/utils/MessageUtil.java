package fr.florianpal.fauction.utils;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.languages.MessageKeys;
import org.bukkit.entity.Player;

import java.io.File;

public class MessageUtil {

    public static void sendMessage(FAuction plugin, Player player, MessageKeys messageKeys, String... replacements) {

        GlobalConfig globalConfig = plugin.getConfigurationManager().getGlobalConfig();
        if (!globalConfig.getMessageSend().containsKey(messageKeys.name()) || globalConfig.getMessageSend().get(messageKeys.name())) {
            CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
            issuerTarget.sendInfo(messageKeys, replacements);
        }
    }
}
