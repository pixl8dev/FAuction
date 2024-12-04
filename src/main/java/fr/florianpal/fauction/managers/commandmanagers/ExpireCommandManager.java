package fr.florianpal.fauction.managers.commandmanagers;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.queries.ExpireQueries;
import fr.florianpal.fauction.utils.SerializationUtil;

import java.util.List;
import java.util.UUID;


public class ExpireCommandManager {
    private final ExpireQueries expireQueries;

    public ExpireCommandManager(FAuction plugin) {
        this.expireQueries = plugin.getExpireQueries();
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

    public Auction expireExist(int id) {
        return expireQueries.getExpire(id);
    }
}