package fr.florianpal.fauction.gui.subGui;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.PlayerViewConfig;
import fr.florianpal.fauction.enums.CancelReason;
import fr.florianpal.fauction.events.AuctionCancelEvent;
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
import java.util.stream.Collectors;

public class PlayerViewGui extends AbstractGuiWithAuctions implements GuiInterface {

    private final PlayerViewConfig playerViewConfig;

    public PlayerViewGui(FAuction plugin, Player player, List<Auction> auctions, int page, Category category) {
        super(plugin, player, page, auctions, category, plugin.getConfigurationManager().getPlayerViewConfig());
        this.playerViewConfig = plugin.getConfigurationManager().getPlayerViewConfig();
        this.auctions = auctions.stream().filter(a -> a.getPlayerUUID().equals(player.getUniqueId())).collect(Collectors.toList());
        initGui(playerViewConfig.getNameGui(), playerViewConfig.getSize());
    }

    @Override
    public void initializeItems() {

        initBarrier();

        if (!auctions.isEmpty()) {
            int id = (this.playerViewConfig.getBaseBlocks().size() * this.page) - this.playerViewConfig.getBaseBlocks().size();
            for (int index : playerViewConfig.getBaseBlocks()) {
                inv.setItem(index, createGuiItem(auctions.get(id)));
                id++;
                if (id >= (auctions.size())) break;
            }
        }
        openInventory(player);
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

        for (int index : playerViewConfig.getBaseBlocks()) {
            if (index == e.getRawSlot()) {
                int nb0 = playerViewConfig.getBaseBlocks().get(0);
                int nb = (e.getRawSlot() - nb0) / 9;
                Auction auction = auctions.get((e.getRawSlot() - nb0) + ((this.playerViewConfig.getBaseBlocks().size() * this.page) - this.playerViewConfig.getBaseBlocks().size()) - nb * 2);

                if (e.isRightClick()) {
                    FAuction.newChain().asyncFirst(() -> auctionCommandManager.auctionExist(auction.getId())).syncLast(a -> {
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

                        auctionCommandManager.deleteAuction(a.getId());
                        plugin.getLogger().info("Player delete from ah auction : " + a.getId() + ", Item : " + a.getItemStack().getItemMeta().getDisplayName() + " of " + a.getPlayerName() + ", by" + player.getName());

                        auctions.remove(a);
                        new AuctionCancelEvent(player, a, CancelReason.PLAYER).callEvent();

                        CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
                        issuerTarget.sendInfo(MessageKeys.REMOVE_AUCTION_SUCCESS);

                        player.closeInventory();

                        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
                            PlayerViewGui gui = new PlayerViewGui(plugin, player, auctions, this.page, category);
                            gui.initializeItems();
                        }).execute();
                    }).execute();
                }
                break;
            }
        }
    }
}