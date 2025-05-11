package fr.florianpal.fauction.schedules;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.objects.CurrencyPending;
import fr.florianpal.fauction.queries.CurrencyPendingQueries;
import fr.florianpal.fauction.utils.CurrencyUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class CurrencyScheduler implements Runnable {

    private final CurrencyPendingQueries currencyPendingQueries;

    private final FAuction plugin;

    public CurrencyScheduler(FAuction plugin) {
        this.plugin = plugin;
        this.currencyPendingQueries = plugin.getCurrencyPendingQueries();
    }

    @Override
    public void run() {
        FAuction.newChain().asyncFirst(currencyPendingQueries::getCurrencyPending).syncLast(currencyPendings -> {

            for (CurrencyPending currencyPending : currencyPendings) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(currencyPending.getPlayerUUID());

                if (offlinePlayer.isOnline()) {
                    CurrencyUtil.giveCurrency(plugin, offlinePlayer, currencyPending.getCurrencyType(), currencyPending.getAmount());
                    currencyPendingQueries.deleteCurrencyPending(currencyPending.getId());
                }
            }

        }).execute();
    }
}
