package fr.florianpal.fauction.objects;

import org.bukkit.Material;

import java.util.List;

public class Category {

    private final String id;

    private final String displayName;

    private final List<String> materials;

    public Category(String id, String displayName, List<String> materials) {
        this.id = id;
        this.displayName = displayName;
        this.materials = materials;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getMaterialsString() {
        return materials;
    }

    public List<Material> getMaterials() {
        return materials.stream().map(Material::getMaterial).toList();
    }

    public boolean containsAll() {
        return materials.stream().anyMatch(s -> s.equals("ALL"));
    }
}
