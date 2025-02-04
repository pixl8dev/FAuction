package fr.florianpal.fauction.gui.subGui;

import co.aikar.commands.CommandIssuer;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.AuctionConfirmGuiConfig;
import fr.florianpal.fauction.events.AuctionBuyEvent;
import fr.florianpal.fauction.gui.AbstractGui;
import fr.florianpal.fauction.gui.GuiInterface;
import fr.florianpal.fauction.languages.MessageKeys;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Barrier;
import fr.florianpal.fauction.objects.Confirm;
import fr.florianpal.fauction.objects.Historic;
import fr.florianpal.fauction.utils.FormatUtil;
import fr.florianpal.fauction.utils.PlaceholderUtil;
import fr.florianpal.fauction.utils.PlayerHeadUtil;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class AuctionConfirmGui extends AbstractGui implements GuiInterface {

    private final Auction auction;

    protected final AuctionConfirmGuiConfig auctionConfirmConfig;

    private final Map<Integer, Confirm> confirmList = new HashMap<>();

    AuctionConfirmGui(FAuction plugin, Player player, int page, Auction auction) {
        super(plugin, player, page, plugin.getConfigurationManager().getAuctionConfirmConfig());
        this.auction = auction;
        this.auctionConfirmConfig = plugin.getConfigurationManager().getAuctionConfirmConfig();
        initGui(auctionConfirmConfig.getNameGui(), auctionConfirmConfig.getSize());
    }

    public void initializeItems() {

        for (Barrier barrier : auctionConfirmConfig.getBarrierBlocks()) {
            inv.setItem(barrier.getIndex(), getItemStack(barrier, false));
        }

        for (Integer index : auctionConfirmConfig.getAuctionBlocks()) {
            inv.setItem(index, createGuiItem(auction));
        }

        int id = 0;
        for (Map.Entry<Integer, Confirm> entry : auctionConfirmConfig.getConfirmBlocks().entrySet()) {
            Confirm confirm = new Confirm(this.auction,
                    entry.getValue().getMaterial(),
                    entry.getValue().isValue(),
                    entry.getValue().getTexture(),
                    entry.getValue().getCustomModelData()
            );
            confirmList.put(entry.getKey(), confirm);
            inv.setItem(entry.getKey(), createGuiItem(confirm));
            id++;
            if (id >= (auctionConfirmConfig.getConfirmBlocks().size())) break;
        }
        openInventory(player);
    }

    public ItemStack createGuiItem(Auction auction) {
        ItemStack item = auction.getItemStack().clone();
        ItemMeta meta = item.getItemMeta();
        String title = auctionConfirmConfig.getAuctionTitle();
        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("")) {
            title = title.replace("{ItemName}", item.getType().name().replace('_', ' ').toLowerCase());
        } else {
            title = title.replace("{ItemName}", item.getItemMeta().getDisplayName());
        }
        title = title.replace("{OwnerName}", auction.getPlayerName());
        title = title.replace("{Price}", df.format(auction.getPrice()));

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(auction.getPlayerUUID());
        if (offlinePlayer != null) {
            title = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), offlinePlayer, title);
        }

        title = FormatUtil.format(title);
        List<String> listDescription = new ArrayList<>();

        for (String desc : auctionConfirmConfig.getAuctionDescription()) {
            if (item.getItemMeta().getDisplayName().equalsIgnoreCase("")) {
                desc = desc.replace("{ItemName}", item.getType().name().replace('_', ' ').toLowerCase());
            } else {
                desc = desc.replace("{ItemName}", item.getItemMeta().getDisplayName());
            }

            desc = desc.replace("{OwnerName}", auction.getPlayerName());

            if (auction instanceof Historic) {
                Historic historic = (Historic) auction;
                desc = desc.replace("{BuyerName}", historic.getPlayerBuyerName());
            }

            if (offlinePlayer != null) {
                desc = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), offlinePlayer, desc);
            }

            desc = desc.replace("{Price}", df.format(auction.getPrice()));
            Date expireDate = new Date((auction.getDate().getTime() + globalConfig.getTime() * 1000L));
            SimpleDateFormat formater = new SimpleDateFormat(globalConfig.getDateFormat());
            desc = desc.replace("{ExpireTime}", formater.format(expireDate));

            Duration duration = Duration.between(new Date().toInstant(), new Date(auction.getDate().getTime() + globalConfig.getTime() * 1000L).toInstant());
            desc = desc.replace("{RemainingTime}", FormatUtil.durationFormat(globalConfig.getRemainingDateFormat(), duration));
            if (desc.contains("lore")) {
                if (item.getItemMeta().getLore() != null) {
                    listDescription.addAll(item.getItemMeta().getLore());
                } else {
                    listDescription.add(desc.replace("{lore}", ""));
                }
            } else {
                desc = FormatUtil.format(desc);
                listDescription.add(desc);
            }
        }
        if (meta != null) {
            if (auctionConfirmConfig.isReplaceTitle()) {
                meta.setDisplayName(title);
            }
            meta.setLore(listDescription);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createGuiItem(Confirm confirm) {
        ItemStack item = new ItemStack(confirm.getMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        String title;
        if (confirm.isValue()) {
            title = auctionConfirmConfig.getTitleTrue();
        } else {
            title = auctionConfirmConfig.getTitleFalse();
        }

        ItemStack itemStack = confirm.getAuction().getItemStack().clone();
        if (confirm.getAuction().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("")) {
            title = title.replace("{Item}", itemStack.getType().toString());
        } else {
            title = title.replace("{Item}", itemStack.getItemMeta().getDisplayName());
        }
        title = title.replace("{Price}", df.format(confirm.getAuction().getPrice()));
        title = title.replace("{OwnerName}", confirm.getAuction().getPlayerName());

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(auction.getPlayerUUID());
        if (offlinePlayer != null) {
            title = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), offlinePlayer, title);
        }

        title = FormatUtil.format(title);
        List<String> listDescription = new ArrayList<>();
        for (String desc : auctionConfirmConfig.getDescription()) {
            desc = desc.replace("{Price}", df.format(confirm.getAuction().getPrice()));
            if (confirm.getAuction().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("")) {
                desc = desc.replace("{Item}", confirm.getAuction().getItemStack().getType().toString());
            } else {
                desc = desc.replace("{Item}", confirm.getAuction().getItemStack().getItemMeta().getDisplayName());
            }
            desc = desc.replace("{OwnerName}", confirm.getAuction().getPlayerName());

            if (offlinePlayer != null) {
                desc = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), offlinePlayer, desc);
            }

            desc = FormatUtil.format(desc);
            listDescription.add(desc);
        }

        if (meta != null) {
            meta.setDisplayName(title);
            meta.setLore(listDescription);
            meta.setCustomModelData(confirm.getCustomModelData());
            item.setItemMeta(meta);
        }
        if (confirm.getMaterial() == Material.PLAYER_HEAD) {
            PlayerHeadUtil.addTexture(itemStack, confirm.getTexture());
            itemStack.setAmount(1);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() != inv || inv.getHolder() != this || player != e.getWhoClicked()) {
            return;
        }
        e.setCancelled(true);

        if (spamManager.spamTest(player)) {
            return;
        }

        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        for (Map.Entry<Integer, Confirm> entry : auctionConfirmConfig.getConfirmBlocks().entrySet()) {
            if (entry.getKey() == e.getRawSlot()) {
                CommandIssuer issuerTarget = commandManager.getCommandIssuer(player);
                Confirm confirm = confirmList.get(e.getRawSlot());
                if (!confirm.isValue()) {
                    issuerTarget.sendInfo(MessageKeys.BUY_AUCTION_CANCELLED);

                    FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
                        AuctionsGui gui = new AuctionsGui(plugin, player, auctions, 1, null);
                        gui.initializeItems();
                    }).execute();
                    return;
                }

                FAuction.newChain().asyncFirst(() -> auctionCommandManager.auctionExist(this.auction.getId())).syncLast(a -> {
                    if (a == null) {
                        issuerTarget.sendInfo(MessageKeys.NO_AUCTION);
                        return;
                    }

                    FAuction.newChain().asyncFirst(() -> auctionCommandManager.auctionExist(this.auction.getId())).syncLast(auctionGood -> {
                        if (auctionGood == null) {
                            issuerTarget.sendInfo(MessageKeys.AUCTION_ALREADY_SELL);
                            return;
                        }

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(auctionGood.getPlayerUUID());
                        if (offlinePlayer == null) {
                            return;
                        }

                        EconomyResponse economyResponse4 = plugin.getVaultIntegrationManager().getEconomy().depositPlayer(offlinePlayer, auctionGood.getPrice());
                        if (!economyResponse4.transactionSuccess()) {
                            return;
                        }

                        EconomyResponse economyResponse5 = plugin.getVaultIntegrationManager().getEconomy().withdrawPlayer(player, auctionGood.getPrice());
                        if (!economyResponse5.transactionSuccess()) {
                            return;
                        }

                        issuerTarget.sendInfo(MessageKeys.BUY_AUCTION_SUCCESS);
                        auctionCommandManager.deleteAuction(auctionGood.getId());
                        historicCommandManager.addHistoric(a, player.getUniqueId(), player.getName());

                        if (player.getInventory().firstEmpty() == -1) {
                            player.getWorld().dropItem(player.getLocation(), auctionGood.getItemStack());
                        } else {
                            player.getInventory().addItem(auctionGood.getItemStack());
                        }

                        Bukkit.getPluginManager().callEvent(new AuctionBuyEvent(player, a));

                        if (plugin.getConfigurationManager().getGlobalConfig().isOnBuyCommandUse()) {
                            String command = getCommand(auctionGood);
                            getServer().dispatchCommand(getServer().getConsoleSender(), command);
                        }

                        plugin.getLogger().info("Player : " + player.getName() + " buy " + auctionGood.getItemStack().getItemMeta().getDisplayName() + " at " + auctionGood.getPlayerName());

                        FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
                            AuctionsGui gui = new AuctionsGui(plugin, player, auctions, 1, null);
                            gui.initializeItems();
                        }).execute();
                    }).execute();
                }).execute();
                break;
            }
        }
    }

    @NotNull
    private String getCommand(Auction auctionGood) {
        String command = plugin.getConfigurationManager().getGlobalConfig().getOnBuyCommand();
        command = command.replace("{OwnerName}", auctionGood.getPlayerName());
        command = command.replace("{Amount}", String.valueOf(auctionGood.getItemStack().getAmount()));
        if (!auctionGood.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase("")) {
            command = command.replace("{ItemName}", auctionGood.getItemStack().getItemMeta().getDisplayName());
        } else {
            command = command.replace("{ItemName}", auctionGood.getItemStack().getType().name().replace('_', ' ').toLowerCase());
        }
        command = command.replace("{BuyerName}", player.getName());
        command = command.replace("{ItemPrice}", String.valueOf(auctionGood.getPrice()));
        return command;
    }
}
