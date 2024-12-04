package fr.florianpal.fauction.gui.subGui;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.PlayerViewConfig;
import fr.florianpal.fauction.gui.AbstractGuiWithAuctions;
import fr.florianpal.fauction.gui.GuiInterface;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Historic;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricGui extends AbstractGuiWithAuctions implements GuiInterface {

    private final PlayerViewConfig playerViewConfig;

    private final List<LocalDateTime> spamTest = new ArrayList<>();

    public HistoricGui(FAuction plugin, Player player, List<Auction> auctions, int page) {
        super(plugin, player, page, auctions, plugin.getConfigurationManager().getPlayerViewConfig());
        this.playerViewConfig = plugin.getConfigurationManager().getPlayerViewConfig();
        initGui(playerViewConfig.getNameGui(), playerViewConfig.getSize());
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