package fr.florianpal.fauction.schedules;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.managers.commandmanagers.AuctionCommandManager;
import fr.florianpal.fauction.managers.commandmanagers.ExpireCommandManager;
import fr.florianpal.fauction.managers.commandmanagers.HistoricCommandManager;

public class CacheSchedule implements Runnable {

    private final ExpireCommandManager expireCommandManager;

    private final HistoricCommandManager historicCommandManager;

    private final AuctionCommandManager auctionCommandManager;

    public CacheSchedule(FAuction plugin) {
        this.auctionCommandManager = plugin.getAuctionCommandManager();
        this.historicCommandManager = plugin.getHistoricCommandManager();
        this.expireCommandManager = plugin.getExpireCommandManager();
    }

    @Override
    public void run() {
        FAuction.newChain().async(() -> {
            expireCommandManager.updateCache();
            historicCommandManager.updateCache();
            auctionCommandManager.updateCache();
        }).execute();
    }
}
