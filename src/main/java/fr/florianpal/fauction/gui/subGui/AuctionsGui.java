package fr.florianpal.fauction.gui.subGui;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.AuctionConfig;
import fr.florianpal.fauction.enums.CancelReason;
import fr.florianpal.fauction.enums.Gui;
import fr.florianpal.fauction.events.AuctionCancelEvent;
import fr.florianpal.fauction.gui.AbstractGuiWithAuctions;
import fr.florianpal.fauction.gui.visualization.InventoryVisualization;
import fr.florianpal.fauction.languages.MessageKeys;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Category;
import fr.florianpal.fauction.utils.CurrencyUtil;
import fr.florianpal.fauction.utils.ItemUtil;
import fr.florianpal.fauction.utils.MessageUtil;
import fr.florianpal.fauction.utils.VisualizationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.List;

public class AuctionsGui extends AbstractGuiWithAuctions {

    private final AuctionConfig auctionConfig;

    public AuctionsGui(FAuction plugin, Player player, List<Auction> auctions, int page, Category category) {
        super(plugin, player, page, auctions, category, plugin.getConfigurationManager().getAuctionConfig());
        this.auctionConfig = plugin.getConfigurationManager().getAuctionConfig();
    }

    @Override
    protected void previousAction() {
        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
            AuctionsGui gui = new AuctionsGui(plugin, player, auctions, this.page - 1, category);
            gui.initialize();
        }).execute();
    }

    @Override
    protected void nextAction() {
        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
            AuctionsGui gui = new AuctionsGui(plugin, player, auctions, this.page + 1, category);
            gui.initialize();
        }).execute();
    }

    @Override
    protected void categoryAction(Category nextCategory) {
        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
            AuctionsGui gui = new AuctionsGui(plugin, player, auctions, 1, nextCategory);
            gui.initialize();
        }).execute();
    }

    private final java.util.Set<Integer> processingAuctions = new java.util.HashSet<>();

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

        for (int index : auctionConfig.getBaseBlocks()) {
            if (index == e.getRawSlot()) {
                int nb0 = auctionConfig.getBaseBlocks().get(0);
                int nb = (e.getRawSlot() - nb0) / 9;
                Auction auction = auctions.get((e.getRawSlot() - nb0) + ((this.auctionConfig.getBaseBlocks().size() * this.page) - this.auctionConfig.getBaseBlocks().size()) - nb * 2);

                if (e.isRightClick()) {
                    // Prevent processing the same auction multiple times
                    if (processingAuctions.contains(auction.getId())) {
                        return;
                    }
                    processingAuctions.add(auction.getId());
                    
                    FAuction.newChain().asyncFirst(() -> auctionCommandManager.auctionExist(auction.getId())).syncLast(a -> {

                        if (a == null) {
                            return;
                        }

                        boolean isModCanCancel = (e.isShiftClick() && player.hasPermission("fauction.mod.cancel"));
                        if (!a.getPlayerUUID().equals(player.getUniqueId()) && !isModCanCancel) {

                            VisualizationUtils.createVizualisation(plugin, a, player, Gui.AUCTION);
                            return;
                        }

                        try {
                            auctionCommandManager.deleteAuction(a.getId());

                            if (!isModCanCancel) {
                                if (player.getInventory().firstEmpty() == -1) {
                                    player.getWorld().dropItem(player.getLocation(), a.getItemStack());
                                } else {
                                    player.getInventory().addItem(a.getItemStack());
                                }
                            }

                            if (isModCanCancel) {
                                plugin.getExpireCommandManager().addExpire(a);
                                plugin.getLogger().info("Modo delete from ah auction : " + a.getId() + ", Item : " + a.getItemStack().getItemMeta().getDisplayName() + " of " + a.getPlayerName() + ", by" + player.getName());
                                Bukkit.getPluginManager().callEvent(new AuctionCancelEvent(player, a, CancelReason.MODERATOR));
                            } else {
                                plugin.getLogger().info("Player delete from ah auction : " + a.getId() + ", Item : " + a.getItemStack().getItemMeta().getDisplayName() + " of " + a.getPlayerName() + ", by" + player.getName());
                                Bukkit.getPluginManager().callEvent(new AuctionCancelEvent(player, a, CancelReason.PLAYER));
                            }
                            auctions.remove(a);

                            MessageUtil.sendMessage(plugin, player, MessageKeys.REMOVE_AUCTION_SUCCESS);
                        } catch (Exception exception) {
                            plugin.getLogger().severe(exception.toString());
                            player.closeInventory();
                            processingAuctions.remove(auction.getId());

                            FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctionsNew -> {
                                AuctionsGui gui = new AuctionsGui(plugin, player, auctionsNew, 1, category);
                                gui.initialize();
                            }).execute();
                        }

                        player.closeInventory();

                        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctionsNew -> {
                            AuctionsGui gui = new AuctionsGui(plugin, player, auctionsNew, 1, category);
                            gui.initialize();
                        }).execute();
                    }).execute();
                } else if (e.isLeftClick()) {
                    if (auction.getPlayerUUID().equals(player.getUniqueId())) {
                        MessageUtil.sendMessage(plugin, player, MessageKeys.BUY_YOUR_ITEM);
                        return;
                    }

                    if (!CurrencyUtil.haveCurrency(plugin, player, globalConfig.getCurrencyType(), auction.getPrice())) {
                        MessageUtil.sendMessage(plugin, player, MessageKeys.NO_HAVE_MONEY);
                        return;
                    }

                    AuctionConfirmGui auctionConfirmGui = new AuctionConfirmGui(plugin, player, page, auction);
                    auctionConfirmGui.initialize();
                }
                break;
            }
        }
    }
}