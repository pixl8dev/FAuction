package fr.florianpal.fauction.configurations.gui;

import fr.florianpal.fauction.objects.Barrier;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGuiWithAuctionsConfig extends AbstractGuiConfig {



    protected List<Integer> baseBlocks = new ArrayList<>();

    protected List<Barrier> barrierBlocks = new ArrayList<>();

    protected List<Barrier> previousBlocks = new ArrayList<>();

    protected List<Barrier> nextBlocks = new ArrayList<>();

    protected List<Barrier> expireBlocks = new ArrayList<>();

    private List<Barrier> auctionGuiBlocks = new ArrayList<>();

    protected List<Barrier> categoriesBlocks = new ArrayList<>();

    protected List<Barrier> closeBlocks = new ArrayList<>();

    protected List<Barrier> playerBlocks = new ArrayList<>();

    private List<Barrier> historicBlocks = new ArrayList<>();

    protected String title = "";

    protected boolean replaceTitle = true;

    protected List<String> description = new ArrayList<>();

    protected String nameGui = "";

    protected int size;


    public void load(Configuration config, String baseBlock) {
        barrierBlocks = new ArrayList<>();
        previousBlocks = new ArrayList<>();
        nextBlocks = new ArrayList<>();
        expireBlocks = new ArrayList<>();
        baseBlocks = new ArrayList<>();
        closeBlocks = new ArrayList<>();
        playerBlocks = new ArrayList<>();
        description = new ArrayList<>();
        categoriesBlocks = new ArrayList<>();
        historicBlocks = new ArrayList<>();
        auctionGuiBlocks = new ArrayList<>();

        for (String index : config.getConfigurationSection("block").getKeys(false)) {
            if (config.getString("block." + index + ".utility").equalsIgnoreCase("previous")) {

                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        new Barrier(
                                Integer.parseInt(index),
                                Material.getMaterial(config.getString("block." + index + ".replacement.material")),
                                config.getString("block." + index + ".replacement.title"),
                                config.getStringList("block." + index + ".replacement.description"),
                                config.getString("block." + index + ".replacement.texture", ""),
                                config.getInt("block." + index + ".replacement.customModelData", 0)
                        ),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                previousBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("next")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        new Barrier(
                                Integer.parseInt(index),
                                Material.getMaterial(config.getString("block." + index + ".replacement.material")),
                                config.getString("block." + index + ".replacement.title"),
                                config.getStringList("block." + index + ".replacement.description"),
                                config.getString("block." + index + ".replacement.texture", ""),
                                config.getInt("block." + index + ".replacement.customModelData", 0)
                        ),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                nextBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("player")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                playerBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("expireGui")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                expireBlocks.add(barrier);

            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("historicGui")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                historicBlocks.add(barrier);

            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("category")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                categoriesBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("barrier")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                barrierBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase(baseBlock)) {
                baseBlocks.add(Integer.valueOf(index));
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("close")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                closeBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("auctionGui")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                auctionGuiBlocks.add(barrier);
            }
        }
        size = config.getInt("gui.size");
        nameGui = config.getString("gui.name");
        title = config.getString("gui.title");
        replaceTitle = config.getBoolean("gui.replaceTitle", true);
        description.addAll(config.getStringList("gui.description"));
        inventoryType = InventoryType.valueOf(config.getString("gui.type", "CHEST"));
    }

    public List<Barrier> getAuctionGuiBlocks() {
        return auctionGuiBlocks;
    }

    public List<Integer> getBaseBlocks() {
        return baseBlocks;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getNameGui() {
        return nameGui;
    }

    public int getSize() {
        return size;
    }

    public List<Barrier> getBarrierBlocks() {
        return barrierBlocks;
    }

    public List<Barrier> getPreviousBlocks() {
        return previousBlocks;
    }

    public List<Barrier> getExpireBlocks() {
        return expireBlocks;
    }

    public List<Barrier> getNextBlocks() {
        return nextBlocks;
    }

    public List<Barrier> getCloseBlocks() {
        return closeBlocks;
    }

    public List<Barrier> getPlayerBlocks() {
        return playerBlocks;
    }

    public boolean isReplaceTitle() {
        return replaceTitle;
    }

    public List<Barrier> getCategoriesBlocks() {
        return categoriesBlocks;
    }

    public List<Barrier> getHistoricBlocks() {
        return historicBlocks;
    }
}
