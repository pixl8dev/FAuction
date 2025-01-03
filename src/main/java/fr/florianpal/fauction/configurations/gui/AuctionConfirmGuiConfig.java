package fr.florianpal.fauction.configurations.gui;

import dev.dejvokep.boostedyaml.YamlDocument;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.objects.Barrier;
import fr.florianpal.fauction.objects.Confirm;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.florianpal.fauction.enums.BlockType.BARRIER;
import static fr.florianpal.fauction.enums.BlockType.CONFIRM;

public class AuctionConfirmGuiConfig extends AbstractGuiConfig {

    private String titleTrue = "";

    private String titleFalse = "";

    private List<String> description = new ArrayList<>();

    private String nameGui = "";

    private Integer size = 27;

    private List<Barrier> barrierBlocks = new ArrayList<>();

    private Map<Integer, Confirm> confirmBlocks = new HashMap<>();

    public void load(FAuction plugin, YamlDocument config) {
        titleTrue = config.getString("gui.title-true");
        titleFalse = config.getString("gui.title-false");
        nameGui = config.getString("gui.name");
        description = config.getStringList("gui.description");
        size = config.getInt("gui.size");
        inventoryType = InventoryType.valueOf(config.getString("gui.type", "CHEST"));

        barrierBlocks = new ArrayList<>();
        confirmBlocks = new HashMap<>();

        for (Object indexObject : config.getSection("block").getKeys()) {

            String index = indexObject.toString();
            String currentUtility = config.getString("block." + index + ".utility");

            if (BARRIER.equalsIgnoreCase(currentUtility)) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material", Material.BARRIER.toString())),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                barrierBlocks.add(barrier);
            } else if (CONFIRM.equalsIgnoreCase(currentUtility)) {
                confirmBlocks.put(Integer.valueOf(index), new Confirm(null,
                        Material.getMaterial(config.getString("block." + index + ".material", Material.BARRIER.toString())),
                        config.getBoolean("block." + index + ".value"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)));
            } else {
                plugin.getLogger().severe("Error : unknown block type " + currentUtility);
            }
        }
    }

    public List<String> getDescription() {
        return description;
    }

    public String getNameGui() {
        return nameGui;
    }

    public Integer getSize() {
        return size;
    }

    public List<Barrier> getBarrierBlocks() {
        return barrierBlocks;
    }

    public Map<Integer, Confirm> getConfirmBlocks() {
        return confirmBlocks;
    }

    public String getTitleTrue() {
        return titleTrue;
    }

    public String getTitleFalse() {
        return titleFalse;
    }
}
