package fr.florianpal.fauction.managers.commandmanagers;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.queries.ExpireQueries;
import fr.florianpal.fauction.utils.SerializationUtil;

import java.util.*;

public class ExpireCommandManager {

    private final ExpireQueries expireQueries;

    private final Map<UUID, List<Auction>> cache = new HashMap<>();

    public ExpireCommandManager(FAuction plugin) {
        this.expireQueries = plugin.getExpireQueries();
        updateCache();
    }

    public List<Auction> getExpires() {
        return expireQueries.getExpires();
    }

    public List<Auction> getExpires(UUID uuid) {
        return expireQueries.getExpires(uuid);
    }

    public void addExpire(Auction auction)  {
        expireQueries.addExpire(auction.getPlayerUUID(), auction.getPlayerName(), SerializationUtil.serialize(auction.getItemStack()), auction.getPrice(), auction.getDate());
    }

    public void deleteExpire(int id) {
        expireQueries.deleteExpire(id);
    }

    public void deleteAll() {
        expireQueries.deleteAll();
    }

    public Auction expireExist(int id) {
        return expireQueries.getExpire(id);
    }

    public void updateCache() {
        List<Auction> expires = expireQueries.getExpires();

        for (Auction expire : expires) {
            if (!cache.containsKey(expire.getPlayerUUID())) {
                cache.put(expire.getPlayerUUID(), new ArrayList<>());
            }
            cache.get(expire.getPlayerUUID()).add(expire);
        }
    }

    public Map<UUID, List<Auction>> getCache() {
        return cache;
    }
}