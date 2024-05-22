package fr.florianpal.fauction.objects;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.florianpal.fauction.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Barrier {

    private final int index;

    private final Material material;

    private final String title;

    private final List<String> description;

    private Barrier remplacement;

    private final String texture;

    private final int customModelData;

    public Barrier(int index, Material material, String title, List<String> description, String texture, int customModelData) {
        this.index = index;
        this.material = material;
        this.title = title;
        this.description = description;
        this.texture = texture;
        this.customModelData = customModelData;
    }

    public Barrier(int index, Material material, String title, List<String> description, Barrier remplacement, String texture, int customModelData) {
        this.index = index;
        this.material = material;
        this.title = title;
        this.description = description;
        this.remplacement = remplacement;
        this.texture = texture;
        this.customModelData = customModelData;
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
                    itemStack.setItemMeta(meta);
                }
            }


        }
        return itemStack;
    }

    public int getIndex() {
        return index;
    }

    public Material getMaterial() {
        return material;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDescription() {
        return description;
    }

    public Barrier getRemplacement() {
        return remplacement;
    }

    public String getTexture() {
        return texture;
    }

    public int getCustomModelData() {
        return customModelData;
    }
}
