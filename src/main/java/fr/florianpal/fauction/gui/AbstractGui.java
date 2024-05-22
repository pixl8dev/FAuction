package fr.florianpal.fauction.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.managers.commandManagers.AuctionCommandManager;
import fr.florianpal.fauction.managers.commandManagers.CommandManager;
import fr.florianpal.fauction.managers.commandManagers.ExpireCommandManager;
import fr.florianpal.fauction.objects.Barrier;
import fr.florianpal.fauction.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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

    protected AbstractGui(FAuction plugin, Player player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
        this.commandManager = plugin.getCommandManager();
        inv = null;
        this.globalConfig = plugin.getConfigurationManager().getGlobalConfig();
        this.auctionCommandManager = plugin.getAuctionCommandManager();
        this.expireCommandManager = plugin.getExpireCommandManager();

        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]);
    }

    protected void initGui(String title, int size) {
        inv = Bukkit.createInventory(this, size, FormatUtil.format(title));
    }

    public ItemStack getItemStack(Barrier barrier, boolean isRemplacement) {
        ItemStack itemStack;
        if (isRemplacement) {
            itemStack = getItemStack(barrier.getRemplacement(), false);
        } else {

            if (barrier.getMaterial() == Material.PLAYER_HEAD) {
                itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                profile.setProperty(new ProfileProperty("textures", barrier.getTexture()));
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                skullMeta.setPlayerProfile(profile);

                List<String> descriptions = new ArrayList<>();
                for (String desc : barrier.getDescription()) {
                    desc = FormatUtil.format(desc);
                    descriptions.add(desc);
                }

                skullMeta.setDisplayName(FormatUtil.format(barrier.getTitle())); // We set a displayName to the skull
                skullMeta.setLore(descriptions);
                itemStack.setItemMeta(skullMeta);
                itemStack.setAmount(1);

            } else {
                itemStack = new ItemStack(barrier.getMaterial(), 1);
                ItemMeta meta = itemStack.getItemMeta();
                List<String> descriptions = new ArrayList<>();
                for (String desc : barrier.getDescription()) {
                    desc = FormatUtil.format(desc);
                    descriptions.add(desc);
                }
                if (meta != null) {
                    meta.setDisplayName(FormatUtil.format(barrier.getTitle()));
                    meta.setLore(descriptions);
                    meta.setCustomModelData(barrier.getCustomModelData());
                    itemStack.setItemMeta(meta);
                }
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
