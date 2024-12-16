package fr.florianpal.fauction.gui.subGui;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.ExpireGuiConfig;
import fr.florianpal.fauction.events.ExpireRemoveEvent;
import fr.florianpal.fauction.gui.AbstractGuiWithAuctions;
import fr.florianpal.fauction.gui.GuiInterface;
import fr.florianpal.fauction.languages.MessageKeys;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Category;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExpireGui extends AbstractGuiWithAuctions implements GuiInterface {

    private final ExpireGuiConfig expireGuiConfig;

    public ExpireGui(FAuction plugin, Player player, List<Auction> auctions, int page, Category category) {
        super(plugin, player, page, auctions, category, plugin.getConfigurationManager().getExpireConfig());
        this.expireGuiConfig = plugin.getConfigurationManager().getExpireConfig();
        initGui(expireGuiConfig.getNameGui(), expireGuiConfig.getSize());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv || inv.getHolder() != this || player != e.getWhoClicked()) {
            return;
        }
        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (guiClick(e)) {
            return;
        }

        if (spamManager.spamTest(player)) {
            return;
        }

        for (int index : expireGuiConfig.getBaseBlocks()) {
            if (index == e.getRawSlot()) {

                if (auctions.isEmpty()) {
                    player.closeInventory();
                    return;
                }

                int nb0 = expireGuiConfig.getBaseBlocks().get(0);
                int nb = (e.getRawSlot() - nb0) / 9;
                Auction auction = auctions.get((e.getRawSlot() - nb0) + ((this.expireGuiConfig.getExpireBlocks().size() * this.page) - this.expireGuiConfig.getExpireBlocks().size()) - nb * 2);

                if (e.isLeftClick()) {
                    FAuction.newChain().asyncFirst(() -> expireCommandManager.expireExist(auction.getId())).syncLast(a -> {

                        if (a == null) {
                            return;
                        }

                        if (!a.getPlayerUUID().equals(player.getUniqueId())) {
                            return;
                        }

                        if (player.getInventory().firstEmpty() == -1) {
                            player.getWorld().dropItem(player.getLocation(), a.getItemStack());
                        } else {
                            player.getInventory().addItem(a.getItemStack());
                        }

                        expireCommandManager.deleteExpire(a.getId());
                        auctions.remove(a);
                        new ExpireRemoveEvent(player, a).callEvent();

                        CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
                        issuerTarget.sendInfo(MessageKeys.REMOVE_EXPIRE_SUCCESS);

                        FAuction.newChain().asyncFirst(() -> expireCommandManager.getExpires(player.getUniqueId())).syncLast(auctions -> {
                            ExpireGui gui = new ExpireGui(plugin, player, auctions, 1, category);
                            gui.initializeItems();
                        }).execute();
                    }).execute();
                }
            }
        }
    }
}