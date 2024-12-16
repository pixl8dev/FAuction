package fr.florianpal.fauction.managers.commandmanagers;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.enums.CacheType;
import fr.florianpal.fauction.events.CacheReloadEvent;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.queries.AuctionQueries;
import fr.florianpal.fauction.utils.SerializationUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class AuctionCommandManager {

    private final AuctionQueries auctionQueries;

    private final Map<UUID, List<Auction>> cache = new HashMap<>();

    public AuctionCommandManager(FAuction plugin) {
        this.auctionQueries = plugin.getAuctionQueries();
        updateCache();
    }

    public List<Auction> getAuctions() {
        return auctionQueries.getAuctions();
    }

    public List<Auction> getAuctions(UUID uuid) {
        return auctionQueries.getAuctions(uuid);
    }

    public void addAuction(Player player, ItemStack item, double price)  {
        auctionQueries.addAuction(player.getUniqueId(), player.getName(), SerializationUtil.serialize(item), price, Calendar.getInstance().getTime());
    }


    public void deleteAuction(int id) {
        auctionQueries.deleteAuctions(id);
    }

    public Auction auctionExist(int id) {
        return auctionQueries.getAuction(id);
    }

    public void updateCache() {
        List<Auction> auctions = auctionQueries.getAuctions();

        for (Auction auction : auctions) {
            if (!cache.containsKey(auction.getPlayerUUID())) {
                cache.put(auction.getPlayerUUID(), new ArrayList<>());
            }
            cache.get(auction.getPlayerUUID()).add(auction);
        }

        new CacheReloadEvent(cache, CacheType.AUCTION).callEvent();
    }

    public Map<UUID, List<Auction>> getCache() {
        return cache;
    }
}