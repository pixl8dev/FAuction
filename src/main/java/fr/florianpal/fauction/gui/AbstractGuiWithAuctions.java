package fr.florianpal.fauction.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.gui.AbstractGuiWithAuctionsConfig;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Barrier;
import fr.florianpal.fauction.objects.Category;
import fr.florianpal.fauction.utils.FormatUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractGuiWithAuctions extends AbstractGui  {

    protected List<Auction> auctions;

    protected Category category;

    protected AbstractGuiWithAuctionsConfig abstractGuiWithAuctionsConfig;

    protected AbstractGuiWithAuctions(FAuction plugin, Player player, int page, List<Auction> auctions, AbstractGuiWithAuctionsConfig abstractGuiWithAuctionsConfig) {
        super(plugin, player, page);
        this.auctions = auctions;
        this.abstractGuiWithAuctionsConfig = abstractGuiWithAuctionsConfig;
    }

    @Override
    protected void initGui(String title, int size) {
        title = title.replace("{Page}", String.valueOf(this.page));
        title = title.replace("{TotalPage}", String.valueOf(((this.auctions.size() - 1) / abstractGuiWithAuctionsConfig.getAuctionBlocks().size()) + 1));

        this.inv = Bukkit.createInventory(this, abstractGuiWithAuctionsConfig.getSize(), FormatUtil.format(title));
    }

    @Override
    public ItemStack getItemStack(Barrier barrier, boolean isRemplacement) {
        ItemStack itemStack;
        if (isRemplacement) {
            itemStack = getItemStack(barrier.getRemplacement(), false);
        } else {

            if (barrier.getMaterial() == Material.PLAYER_HEAD) {
                itemStack = new ItemStack(Material.PLAYER_HEAD, 1);

                if (PaperLib.isPaper()) {
                    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                    profile.setProperty(new ProfileProperty("textures", barrier.getTexture()));
                    ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
                    skullMeta.setPlayerProfile(profile);
                    itemStack.setItemMeta(skullMeta);
                }

                itemStack.setAmount(1);
            } else {
                itemStack = new ItemStack(barrier.getMaterial(), 1);
            }

            ItemMeta meta = itemStack.getItemMeta();
            List<String> descriptions = new ArrayList<>();
            for (String desc : barrier.getDescription()) {
                desc = FormatUtil.format(desc);
                desc = plugin.parsePlaceholder(player, desc);
                desc = desc.replace("{categoryDisplayName}", category != null ? category.getDisplayName() : "");
                descriptions.add(desc);
            }

            if (meta != null) {
                String name = barrier.getTitle().replace("{categoryDisplayName}", category != null ? category.getDisplayName() : "");
                name = plugin.parsePlaceholder(player, name);
                meta.setDisplayName(FormatUtil.format(name));
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
