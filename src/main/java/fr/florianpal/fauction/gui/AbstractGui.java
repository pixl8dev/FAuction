package fr.florianpal.fauction.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.configurations.gui.AbstractGuiConfig;
import fr.florianpal.fauction.managers.commandManagers.AuctionCommandManager;
import fr.florianpal.fauction.managers.commandManagers.CommandManager;
import fr.florianpal.fauction.managers.commandManagers.ExpireCommandManager;
import fr.florianpal.fauction.objects.Barrier;
import fr.florianpal.fauction.utils.FormatUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractGui implements InventoryHolder, Listener {

    protected Inventory inv;

    protected final FAuction plugin;

    protected Player player;

    protected int page;

    protected final GlobalConfig globalConfig;

    protected final CommandManager commandManager;

    protected final AuctionCommandManager auctionCommandManager;

    protected  final ExpireCommandManager expireCommandManager;

    protected AbstractGuiConfig abstractGuiConfig;

    protected AbstractGui(FAuction plugin, Player player, int page, AbstractGuiConfig abstractGuiConfig) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        this.commandManager = plugin.getCommandManager();
        inv = null;
        this.globalConfig = plugin.getConfigurationManager().getGlobalConfig();
        this.auctionCommandManager = plugin.getAuctionCommandManager();
        this.expireCommandManager = plugin.getExpireCommandManager();
        this.abstractGuiConfig = abstractGuiConfig;

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

                if (PaperLib.isPaper()) {
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                    profile.setProperty(new ProfileProperty("textures", barrier.getTexture()));
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                    skullMeta.setPlayerProfile(profile);
                    itemStack.setItemMeta(skullMeta);
                }

                itemStack.setAmount(1);
            }

            ItemMeta meta = itemStack.getItemMeta();

            List<String> descriptions = new ArrayList<>();
            for (String desc : barrier.getDescription()) {
                desc = FormatUtil.format(desc);
                desc = plugin.parsePlaceholder(player, desc);
                descriptions.add(desc);
            }

            if (meta != null) {
                String title = barrier.getTitle();
                title = plugin.parsePlaceholder(player, title);
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
