package fr.florianpal.fauction.gui;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.AbstractGuiWithAuctionsConfig;
import fr.florianpal.fauction.gui.subGui.AuctionsGui;
import fr.florianpal.fauction.gui.subGui.ExpireGui;
import fr.florianpal.fauction.gui.subGui.HistoricGui;
import fr.florianpal.fauction.gui.subGui.PlayerViewGui;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Barrier;
import fr.florianpal.fauction.objects.Category;
import fr.florianpal.fauction.objects.Historic;
import fr.florianpal.fauction.utils.FormatUtil;
import fr.florianpal.fauction.utils.ListUtil;
import fr.florianpal.fauction.utils.PlaceholderUtil;
import fr.florianpal.fauction.utils.PlayerHeadUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractGuiWithAuctions extends AbstractGui  {

    protected List<Auction> auctions;

    protected Category category;

    protected AbstractGuiWithAuctionsConfig abstractGuiWithAuctionsConfig;

    protected AbstractGuiWithAuctions(FAuction plugin, Player player, int page, List<Auction> auctions, Category category, AbstractGuiWithAuctionsConfig abstractGuiWithAuctionsConfig) {
        super(plugin, player, page, abstractGuiWithAuctionsConfig);
        this.auctions = auctions;
        this.abstractGuiWithAuctionsConfig = abstractGuiWithAuctionsConfig;

          if (category == null) category = plugin.getConfigurationManager().getCategoriesConfig().getDefault();
        this.category = category;

        this.auctions = ListUtil.getAuctionByCategory(auctions, category);
    }

    @Override
    protected void initGui(String title, int size) {
        title = title.replace("{Page}", String.valueOf(this.page));
        title = title.replace("{TotalPage}", String.valueOf(((this.auctions.size() - 1) / abstractGuiWithAuctionsConfig.getBaseBlocks().size()) + 1));

        this.inv = Bukkit.createInventory(this, abstractGuiWithAuctionsConfig.getSize(), FormatUtil.format(title));
    }

    protected void initBarrier() {

        for (Barrier barrier : abstractGuiWithAuctionsConfig.getBarrierBlocks()) {
            inv.setItem(barrier.getIndex(), createGuiItem(getItemStack(barrier, false)));
        }

        for (Barrier barrier : abstractGuiWithAuctionsConfig.getExpireBlocks()) {
            inv.setItem(barrier.getIndex(), createGuiItem(getItemStack(barrier, false)));
        }

        for (Barrier previous : abstractGuiWithAuctionsConfig.getPreviousBlocks()) {
            if (page > 1) {
                inv.setItem(previous.getIndex(), createGuiItem(getItemStack(previous, false)));
            } else {
                inv.setItem(previous.getRemplacement().getIndex(), createGuiItem(getItemStack(previous, true)));
            }
        }

        for (Barrier next : abstractGuiWithAuctionsConfig.getNextBlocks()) {
            if ((this.abstractGuiWithAuctionsConfig.getBaseBlocks().size() * this.page) - this.abstractGuiWithAuctionsConfig.getBaseBlocks().size() < auctions.size() - this.abstractGuiWithAuctionsConfig.getBaseBlocks().size()) {
                inv.setItem(next.getIndex(), createGuiItem(getItemStack(next, false)));
            } else {
                inv.setItem(next.getRemplacement().getIndex(), createGuiItem(getItemStack(next, true)));
            }
        }

        for (Barrier player : abstractGuiWithAuctionsConfig.getPlayerBlocks()) {
            inv.setItem(player.getIndex(), createGuiItem(getItemStack(player, false)));
        }

        for (Barrier auctionGui : abstractGuiWithAuctionsConfig.getAuctionGuiBlocks()) {
            inv.setItem(auctionGui.getIndex(), createGuiItem(getItemStack(auctionGui, false)));
        }

        for (Barrier close : abstractGuiWithAuctionsConfig.getCloseBlocks()) {
            inv.setItem(close.getIndex(), createGuiItem(getItemStack(close, false)));
        }

        for (Barrier categoryBlock : abstractGuiWithAuctionsConfig.getCategoriesBlocks()) {
            inv.setItem(categoryBlock.getIndex(), createGuiItem(getItemStack(categoryBlock, false)));
        }

        for (Barrier historic : abstractGuiWithAuctionsConfig.getHistoricBlocks()) {
            inv.setItem(historic.getIndex(), createGuiItem(getItemStack(historic, false)));
        }
    }

    public void initializeItems() {

        initBarrier();

        if (!auctions.isEmpty()) {
            int id = (this.abstractGuiWithAuctionsConfig.getBaseBlocks().size() * this.page) - this.abstractGuiWithAuctionsConfig.getBaseBlocks().size();
            for (int index : abstractGuiWithAuctionsConfig.getBaseBlocks()) {
                inv.setItem(index, createGuiItem(auctions.get(id)));
                id++;
                if (id >= (auctions.size())) break;
            }
        }
        openInventory(player);
    }

    public ItemStack createGuiItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || meta.getDisplayName() == null || meta.getLore() == null) {
            return itemStack;
        }
        String name = FormatUtil.format(meta.getDisplayName());
        List<String> descriptions = new ArrayList<>();
        for (String desc : meta.getLore()) {

            desc = desc.replace("{TotalSale}", String.valueOf(this.auctions.size()));
            desc = FormatUtil.format(desc);
            descriptions.add(desc);
        }
        meta.setDisplayName(name);
        meta.setLore(descriptions);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack createGuiItem(Auction auction) {
        ItemStack item = auction.getItemStack().clone();
        ItemMeta meta = item.getItemMeta();
        String title = abstractGuiWithAuctionsConfig.getTitle();
        if (item.getItemMeta().getDisplayName().equalsIgnoreCase("")) {
            title = title.replace("{ItemName}", item.getType().name().replace('_', ' ').toLowerCase());
        } else {
            title = title.replace("{ItemName}", item.getItemMeta().getDisplayName());
        }
        title = title.replace("{OwnerName}", auction.getPlayerName());
        title = title.replace("{Price}", df.format(auction.getPrice()));

        if (auction instanceof Historic) {
            Historic historic = (Historic) auction;
            title = title.replace("{BuyerName}", historic.getPlayerBuyerName());
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(auction.getPlayerUUID());
        if (offlinePlayer != null) {
            title = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), offlinePlayer, title);
        }

        title = FormatUtil.format(title);
        List<String> listDescription = new ArrayList<>();

        for (String desc : abstractGuiWithAuctionsConfig.getDescription()) {
            if (item.getItemMeta().getDisplayName().equalsIgnoreCase("")) {
                desc = desc.replace("{ItemName}", item.getType().name().replace('_', ' ').toLowerCase());
            } else {
                desc = desc.replace("{ItemName}", item.getItemMeta().getDisplayName());
            }

            desc = desc.replace("{TotalSale}", String.valueOf(this.auctions.size()));
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
            if (abstractGuiWithAuctionsConfig.isReplaceTitle()) {
                meta.setDisplayName(title);
            }
            meta.setLore(listDescription);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public ItemStack getItemStack(Barrier barrier, boolean isRemplacement) {
        ItemStack itemStack;
        if (isRemplacement) {
            itemStack = getItemStack(barrier.getRemplacement(), false);
        } else {

            itemStack = new ItemStack(barrier.getMaterial(), 1);
            if (barrier.getMaterial() == Material.PLAYER_HEAD) {

                PlayerHeadUtil.addTexture(itemStack, barrier.getTexture());
                itemStack.setAmount(1);
            }

            List<String> descriptions = new ArrayList<>();
            for (String desc : barrier.getDescription()) {
                desc = FormatUtil.format(desc);
                desc = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), player, desc);
                desc = desc.replace("{categoryDisplayName}", category != null ? category.getDisplayName() : "");
                descriptions.add(desc);
            }

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String name = barrier.getTitle().replace("{categoryDisplayName}", category != null ? category.getDisplayName() : "");
                name = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), player, name);
                meta.setDisplayName(FormatUtil.format(name));
                meta.setLore(descriptions);
                meta.setCustomModelData(barrier.getCustomModelData());
                itemStack.setItemMeta(meta);
            }
        }
        return itemStack;
    }

    public boolean guiClick(InventoryClickEvent e) {

        boolean isBarrier = abstractGuiWithAuctionsConfig.getBarrierBlocks().stream().anyMatch(b -> b.getIndex() == e.getRawSlot());
        if (isBarrier) {
            return true;
        }

        boolean isPrevious = abstractGuiWithAuctionsConfig.getPreviousBlocks().stream().anyMatch(b -> b.getIndex() == e.getRawSlot() && this.page > 1);
        if (isPrevious) {

            previousAction();
            return true;
        }

        boolean isNext = abstractGuiWithAuctionsConfig.getNextBlocks().stream().anyMatch(next -> e.getRawSlot() == next.getIndex() && ((this.abstractGuiWithAuctionsConfig.getBaseBlocks().size() * this.page) - this.abstractGuiWithAuctionsConfig.getBaseBlocks().size() < auctions.size() - this.abstractGuiWithAuctionsConfig.getBaseBlocks().size()) && next.getMaterial() != next.getRemplacement().getMaterial());
        if (isNext) {

            nextAction();
            return true;
        }

        boolean isAuctionGui = abstractGuiWithAuctionsConfig.getAuctionGuiBlocks().stream().anyMatch(auctionGui -> e.getRawSlot() == auctionGui.getIndex());
        if (isAuctionGui) {

            FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
                AuctionsGui gui = new AuctionsGui(plugin, player, auctions, 1, null);
                gui.initializeItems();
            }).execute();
            return true;
        }

        boolean isExpire = abstractGuiWithAuctionsConfig.getExpireBlocks().stream().anyMatch(expire -> e.getRawSlot() == expire.getIndex());
        if (isExpire) {

            FAuction.newChain().asyncFirst(() -> expireCommandManager.getExpires(player.getUniqueId())).syncLast(auctions -> {
                ExpireGui gui = new ExpireGui(plugin, player, auctions, 1, category);
                gui.initializeItems();
            }).execute();
            return true;
        }

        boolean isCategory = abstractGuiWithAuctionsConfig.getCategoriesBlocks().stream().anyMatch(c -> e.getRawSlot() == c.getIndex());
        if (isCategory) {

            Category nextCategory = plugin.getConfigurationManager().getCategoriesConfig().getNext(category);
            categoryAction(nextCategory);
            return true;
        }

        boolean isClose = abstractGuiWithAuctionsConfig.getCloseBlocks().stream().anyMatch(close -> e.getRawSlot() == close.getIndex());
        if (isClose) {

            player.closeInventory();
            return true;
        }

        boolean isPlayer = abstractGuiWithAuctionsConfig.getPlayerBlocks().stream().anyMatch(player -> e.getRawSlot() == player.getIndex());
        if (isPlayer) {

            FAuction.newChain().asyncFirst(auctionCommandManager::getAuctions).syncLast(auctions -> {
                PlayerViewGui gui = new PlayerViewGui(plugin, player, auctions, 1, category);
                gui.initializeItems();
            }).execute();
            return true;
        }

        boolean isHistoric = abstractGuiWithAuctionsConfig.getHistoricBlocks().stream().anyMatch(player -> e.getRawSlot() == player.getIndex());
        if (isHistoric) {

            FAuction.newChain().asyncFirst(historicCommandManager::getHistorics).syncLast(historics -> {
                HistoricGui gui = new HistoricGui(plugin, player, ListUtil.historicToAuction(historics), 1, category);
                gui.initializeItems();
            }).execute();
            return true;
        }

        return false;
    }

    protected abstract void previousAction();

    protected abstract void nextAction();

    protected abstract void categoryAction(Category nextCategory);

    @Override
    public Inventory getInventory() {
        return inv;
    }

    protected void openInventory(Player p) {
        p.openInventory(this.inv);
    }
}
