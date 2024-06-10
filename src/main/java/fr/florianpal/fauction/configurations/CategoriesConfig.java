package fr.florianpal.fauction.configurations;

import fr.florianpal.fauction.objects.Category;
import org.bukkit.configuration.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoriesConfig {

    private LinkedHashMap<String, Category> categories;

    public void load(Configuration config) {

        categories = new LinkedHashMap<>();
        for (String id : config.getConfigurationSection("categories").getKeys(false)) {
            String displayName = config.getString("categories." + id + ".displayName");
            List<String> materials = config.getStringList("categories." + id + ".materials");

            categories.put(id, new Category(id, displayName, materials));
        }
    }

    public Map<String, Category> getCategories() {
        return categories;
    }

    public Category getDefault() {
        return categories.getOrDefault("default", null);
    }

    public Category getNext(Category category) {
        boolean next = false;
        for (var entry : categories.entrySet()) {

            if (next) {
                return entry.getValue();
            }

            if (entry.getKey().equals(category.getId())) {
                next = true;
            }
        }

        return getDefault();
    }
}
