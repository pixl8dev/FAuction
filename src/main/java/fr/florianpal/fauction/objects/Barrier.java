package fr.florianpal.fauction.objects;

import fr.florianpal.fauction.utils.FormatUtil;
import fr.florianpal.fauction.utils.PlayerHeadUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

                List<String> descriptions = new ArrayList<>();
                for (String desc : barrier.getDescription()) {
                    desc = FormatUtil.format(desc);
                    descriptions.add(desc);
                }

                itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
                PlayerHeadUtil.addTexture(itemStack, barrier.getTexture());

                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(FormatUtil.format(barrier.getTitle())); // We set a displayName to the skull
                itemMeta.setLore(descriptions);
                itemStack.setItemMeta(itemMeta);
                itemStack.setAmount(1);
            } else {

                List<String> descriptions = new ArrayList<>();
                for (String desc : barrier.getDescription()) {
                    desc = FormatUtil.format(desc);
                    descriptions.add(desc);
                }

                itemStack = new ItemStack(barrier.getMaterial(), 1);
                ItemMeta meta = itemStack.getItemMeta();
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
