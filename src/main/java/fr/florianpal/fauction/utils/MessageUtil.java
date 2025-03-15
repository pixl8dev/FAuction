package fr.florianpal.fauction.utils;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.languages.MessageKeys;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static void sendMessage(FAuction plugin, Player player, MessageKeys messageKeys, String... replacements) {

        CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
        System.out.println(plugin.getCommandManager().getLocales().getOptionalMessage(issuerTarget, messageKeys.getMessageKey()));
        if (plugin.getCommandManager().getLocales().getOptionalMessage(issuerTarget, messageKeys.getMessageKey()) != null) {
            issuerTarget.sendInfo(messageKeys, replacements);
        }
    }
}
