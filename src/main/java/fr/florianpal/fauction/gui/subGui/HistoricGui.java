package fr.florianpal.fauction.gui.subGui;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.HistoricConfig;
import fr.florianpal.fauction.gui.AbstractGuiWithAuctions;
import fr.florianpal.fauction.gui.GuiInterface;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Category;
import fr.florianpal.fauction.utils.ListUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HistoricGui extends AbstractGuiWithAuctions implements GuiInterface {

    public HistoricGui(FAuction plugin, Player player, List<Auction> auctions, int page, Category category) {
        super(plugin, player, page, auctions, category, plugin.getConfigurationManager().getHistoricConfig());
        HistoricConfig historicConfig = plugin.getConfigurationManager().getHistoricConfig();
        initGui(historicConfig.getNameGui(), historicConfig.getSize());
    }

    @Override
    protected void previousAction() {
        FAuction.newChain().asyncFirst(() -> historicCommandManager.getHistorics(player.getUniqueId())).syncLast(historics -> {
            HistoricGui gui = new HistoricGui(plugin, player, ListUtil.historicToAuction(historics), this.page - 1, category);
            gui.initializeItems();
        }).execute();
    }

    @Override
    protected void nextAction() {
        FAuction.newChain().asyncFirst(() -> historicCommandManager.getHistorics(player.getUniqueId())).syncLast(historics -> {
            HistoricGui gui = new HistoricGui(plugin, player, ListUtil.historicToAuction(historics), this.page + 1, category);
            gui.initializeItems();
        }).execute();
    }

    @Override
    protected void categoryAction(Category nextCategory) {
        FAuction.newChain().asyncFirst(() -> historicCommandManager.getHistorics(player.getUniqueId())).syncLast(historics -> {
            HistoricGui gui = new HistoricGui(plugin, player, ListUtil.historicToAuction(historics), 1 , nextCategory);
            gui.initializeItems();
        }).execute();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv || inv.getHolder() != this || player != e.getWhoClicked()) {
            return;
        }

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        guiClick(e);
    }
}