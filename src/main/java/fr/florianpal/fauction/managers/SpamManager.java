package fr.florianpal.fauction.managers;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.languages.MessageKeys;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.*;

public class SpamManager {

    private final FAuction plugin;

    private final GlobalConfig globalConfig;

    private final Map<UUID, List<LocalDateTime>> spamTest = new HashMap<>();

    public SpamManager(FAuction plugin) {
        this.plugin = plugin;
        this.globalConfig = plugin.getConfigurationManager().getGlobalConfig();
    }

    public boolean spamTest(Player player) {
        if (globalConfig.isSecurityForSpammingPacket()) {

            LocalDateTime clickTest = LocalDateTime.now();
            if (spamTest.containsKey(player.getUniqueId())) {

                List<LocalDateTime> localDateTimes = spamTest.get(player.getUniqueId());
                boolean isSpamming = localDateTimes.stream().anyMatch(d -> d.getHour() == clickTest.getHour() && d.getMinute() == clickTest.getMinute() && (d.getSecond() == clickTest.getSecond() || d.getSecond() == clickTest.getSecond() + 1 || d.getSecond() == clickTest.getSecond() - 1));
                if (isSpamming) {
                    plugin.getLogger().warning("Warning : Spam gui auction Pseudo : " + player.getName());
                    CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
                    issuerTarget.sendInfo(MessageKeys.SPAM);
                    return true;
                } else {
                    spamTest.get(player.getUniqueId()).add(clickTest);
                }
            } else {
                spamTest.put(player.getUniqueId(), new ArrayList<>());
                spamTest.get(player.getUniqueId()).add(clickTest);
            }
        }
        return false;
    }
}
