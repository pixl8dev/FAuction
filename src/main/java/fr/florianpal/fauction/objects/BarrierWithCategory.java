package fr.florianpal.fauction.objects;

import org.bukkit.Material;

import java.util.List;

public class BarrierWithCategory extends Barrier {

    private Category category;

    public BarrierWithCategory(int index, Material material, String title, List<String> description, String texture, int customModelData, Category category) {
        super(index, material, title, description, texture, customModelData);

        this.category = category;
    }

    public BarrierWithCategory(int index, Material material, String title, List<String> description, BarrierWithCategory remplacement, String texture, int customModelData, Category category) {
        super(index, material, title, description, remplacement, texture, customModelData);

        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
