package fr.florianpal.fauction.managers.commandmanagers;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Historic;
import fr.florianpal.fauction.queries.HistoricQueries;
import fr.florianpal.fauction.utils.SerializationUtil;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class HistoricCommandManager {
    private final HistoricQueries historicQueries;

    private final Map<UUID, List<Historic>> cache = new HashMap<>();

    public HistoricCommandManager(FAuction plugin) {
        this.historicQueries = plugin.getHistoricQueries();
        updateCache();
    }

    public List<Historic> getHistorics() {
        return historicQueries.getHistorics();
    }

    public List<Historic> getHistorics(UUID uuid) {
        return historicQueries.getHistorics(uuid);
    }

    public void addHistoric(UUID playerUUID, String playerName, UUID playerBuyerUUID, String playerBuyerName, ItemStack item, double price)  {
        historicQueries.addHistoric(playerUUID, playerName, playerBuyerUUID, playerBuyerName, SerializationUtil.serialize(item), price, Calendar.getInstance().getTime());
    }

    public void addHistoric(Auction auction, UUID playerBuyerUUID, String playerBuyerName)  {
        historicQueries.addHistoric(auction, playerBuyerUUID, playerBuyerName);
    }

    public void updateCache() {
        List<Historic> historics = historicQueries.getHistorics();

        for (Historic historic : historics) {
            if (!cache.containsKey(historic.getPlayerUUID())) {
                cache.put(historic.getPlayerUUID(), new ArrayList<>());
            }
            cache.get(historic.getPlayerUUID()).add(historic);
        }
    }

    public Map<UUID, List<Historic>> getCache() {
        return cache;
    }
}