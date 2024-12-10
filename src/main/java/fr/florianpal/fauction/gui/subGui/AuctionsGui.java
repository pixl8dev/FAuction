package fr.florianpal.fauction.gui.subGui;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.AuctionConfig;
import fr.florianpal.fauction.gui.AbstractGuiWithAuctions;
import fr.florianpal.fauction.gui.GuiInterface;
import fr.florianpal.fauction.gui.visualization.InventoryVisualization;
import fr.florianpal.fauction.languages.MessageKeys;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Category;
import fr.florianpal.fauction.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuctionsGui extends AbstractGuiWithAuctions implements GuiInterface {

    private final AuctionConfig auctionConfig;

    public AuctionsGui(FAuction plugin, Player player, List<Auction> auctions, int page, Category category) {
        super(plugin, player, page, auctions, plugin.getConfigurationManager().getAuctionConfig());
        this.auctionConfig = plugin.getConfigurationManager().getAuctionConfig();

        if (category == null) category = plugin.getConfigurationManager().getCategoriesConfig().getDefault();
        this.category = category;

        if (!category.containsAll()) {
            this.auctions = auctions.stream().filter(a -> this.category.getMaterials().contains(a.getItemStack().getType())).collect(Collectors.toList());
        }

        initGui(auctionConfig.getNameGui(), auctionConfig.getSize());
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

        for (int index : auctionConfig.getBaseBlocks()) {
            if (index == e.getRawSlot()) {
                int nb0 = auctionConfig.getBaseBlocks().get(0);
                int nb = ((e.getRawSlot() - nb0)) / 9;
                Auction auction = auctions.get((e.getRawSlot() - nb0) + ((this.auctionConfig.getBaseBlocks().size() * this.page) - this.auctionConfig.getBaseBlocks().size()) - nb * 2);

                if (e.isRightClick()) {
                    FAuction.newChain().asyncFirst(() -> auctionCommandManager.auctionExist(auction.getId())).syncLast(a -> {

                        if (a == null) {
                            return;
                        }

                        boolean isModCanCancel = (e.isShiftClick() && player.hasPermission("fauction.mod.cancel"));
                        if (!a.getPlayerUUID().equals(player.getUniqueId()) && !isModCanCancel) {

                            InventoryVisualization inventoryVisualization = null;
                            if (ItemUtil.isShulker(a.getItemStack())) {

                                BlockStateMeta blockStateMeta = (BlockStateMeta) a.getItemStack().getItemMeta();
                                ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                                inventoryVisualization = new InventoryVisualization(plugin, player, shulkerBox);
                            } else if (ItemUtil.isBarrel(a.getItemStack())) {

                                BlockStateMeta blockStateMeta = (BlockStateMeta) a.getItemStack().getItemMeta();
                                Barrel barrel = (Barrel) blockStateMeta.getBlockState();
                                inventoryVisualization = new InventoryVisualization(plugin, player, barrel);
                            }

                            if (inventoryVisualization != null) {
                                Bukkit.getPluginManager().registerEvents(inventoryVisualization, plugin);
                                inventoryVisualization.open();
                            }
                            return;
                        }

                        if (!isModCanCancel) {
                            if (player.getInventory().firstEmpty() == -1) {
                                player.getWorld().dropItem(player.getLocation(), a.getItemStack());
                            } else {
                                player.getInventory().addItem(a.getItemStack());
                            }
                        }

                        auctionCommandManager.deleteAuction(a.getId());
                        if (isModCanCancel) {
                            plugin.getExpireCommandManager().addExpire(a);
                            plugin.getLogger().info("Modo delete from ah auction : " + a.getId() + ", Item : " + a.getItemStack().getItemMeta().getDisplayName() + " of " + a.getPlayerName() + ", by" + player.getName());
                        } else {
                            plugin.getLogger().info("Player delete from ah auction : " + a.getId() + ", Item : " + a.getItemStack().getItemMeta().getDisplayName() + " of " + a.getPlayerName() + ", by" + player.getName());
                        }
                        auctions.remove(a);
                        CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
                        issuerTarget.sendInfo(MessageKeys.REMOVE_AUCTION_SUCCESS);

                        player.closeInventory();

                        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
                            AuctionsGui gui = new AuctionsGui(plugin, player, auctions, 1, category);
                            gui.initializeItems();
                        }).execute();
                    }).execute();
                } else if (e.isLeftClick()) {
                    CommandIssuer issuerTarget = plugin.getCommandManager().getCommandIssuer(player);
                    if (auction.getPlayerUUID().equals(player.getUniqueId())) {
                        issuerTarget.sendInfo(MessageKeys.BUY_YOUR_ITEM);
                        return;
                    }
                    if (!plugin.getVaultIntegrationManager().getEconomy().has(player, auction.getPrice())) {
                        issuerTarget.sendInfo(MessageKeys.NO_HAVE_MONEY);
                        return;
                    }

                    AuctionConfirmGui auctionConfirmGui = new AuctionConfirmGui(plugin, player, page, auction);
                    auctionConfirmGui.initializeItems();
                }
                break;
            }
        }
    }
}