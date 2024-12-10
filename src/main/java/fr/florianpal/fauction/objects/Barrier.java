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
