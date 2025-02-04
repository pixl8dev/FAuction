package fr.florianpal.fauction.gui;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.configurations.gui.AbstractGuiConfig;
import fr.florianpal.fauction.managers.SpamManager;
import fr.florianpal.fauction.managers.commandmanagers.AuctionCommandManager;
import fr.florianpal.fauction.managers.commandmanagers.CommandManager;
import fr.florianpal.fauction.managers.commandmanagers.ExpireCommandManager;
import fr.florianpal.fauction.managers.commandmanagers.HistoricCommandManager;
import fr.florianpal.fauction.objects.Barrier;
import fr.florianpal.fauction.utils.FormatUtil;
import fr.florianpal.fauction.utils.PlaceholderUtil;
import fr.florianpal.fauction.utils.PlayerHeadUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractGui implements InventoryHolder, Listener {

    protected Inventory inv;

    protected final FAuction plugin;

    protected Player player;

    protected int page;

    protected final GlobalConfig globalConfig;

    protected final CommandManager commandManager;

    protected final AuctionCommandManager auctionCommandManager;

    protected final ExpireCommandManager expireCommandManager;

    protected final HistoricCommandManager historicCommandManager;

    protected final SpamManager spamManager;

    protected AbstractGuiConfig abstractGuiConfig;

    protected DecimalFormat df;

    protected AbstractGui(FAuction plugin, Player player, int page, AbstractGuiConfig abstractGuiConfig) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        this.commandManager = plugin.getCommandManager();
        this.inv = null;
        this.globalConfig = plugin.getConfigurationManager().getGlobalConfig();
        this.auctionCommandManager = plugin.getAuctionCommandManager();
        this.expireCommandManager = plugin.getExpireCommandManager();
        this.historicCommandManager = plugin.getHistoricCommandManager();
        this.spamManager = plugin.getSpamManager();
        this.abstractGuiConfig = abstractGuiConfig;

        df = new DecimalFormat(plugin.getConfigurationManager().getGlobalConfig().getDecimalFormat());
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
    }

    protected void initGui(String title, int size) {
        if (abstractGuiConfig.getType() == InventoryType.CHEST) {
            inv = Bukkit.createInventory(this, size, FormatUtil.format(title));
        } else {
            inv = Bukkit.createInventory(this, abstractGuiConfig.getType(), FormatUtil.format(title));
        }
    }

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
                descriptions.add(desc);
            }

            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                String title = barrier.getTitle();
                title = PlaceholderUtil.parsePlaceholder(plugin.isPlaceholderAPIEnabled(), player, title);
                meta.setDisplayName(FormatUtil.format(title));
                meta.setLore(descriptions);
                meta.setCustomModelData(barrier.getCustomModelData());
                itemStack.setItemMeta(meta);
            }
        }
        return itemStack;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    protected void openInventory(Player p) {
        p.openInventory(this.inv);
    }
}
