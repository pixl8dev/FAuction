package fr.florianpal.fauction.schedules;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.events.ExpireAddEvent;
import fr.florianpal.fauction.objects.Auction;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpireSchedule implements Runnable {

    private final FAuction plugin;

    private List<Auction> auctions = new ArrayList<>();

    public ExpireSchedule(FAuction plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        FAuction.newChain().asyncFirst(() -> plugin.getAuctionCommandManager().getAuctions()).syncLast(auctionList -> {
            this.auctions = auctionList;
            for (Auction auction : this.auctions) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(auction.getDate());
                cal.add(Calendar.SECOND, plugin.getConfigurationManager().getGlobalConfig().getTime());
                if (cal.getTime().getTime() <= Calendar.getInstance().getTime().getTime()) {
                    plugin.getExpireCommandManager().addExpire(auction);
                    Bukkit.getPluginManager().callEvent(new ExpireAddEvent(auction.getPlayerUUID(), auction));
                    plugin.getAuctionCommandManager().deleteAuction(auction.getId());
                }
            }
        }).execute();
    }
}
