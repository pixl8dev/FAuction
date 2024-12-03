package fr.florianpal.fauction.configurations;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalConfig {

    private String lang = "en";
    private String orderBy;
    private boolean onBuyCommandUse;

    private boolean securityForSpammingPacket;

    private String dateFormat;

    private String onBuyCommand;

    private boolean limitationsUseMetaLuckperms = false;
    private Map<String, Integer> limitations = new HashMap<>();
    private Map<Material, Double> minPrice = new HashMap<>();

    private Map<Material, Double> maxPrice = new HashMap<>();

    private List<Material> blacklistItem = new ArrayList<>();

    private boolean defaultMaxValueEnable = false;

    private boolean defaultMinValueEnable = false;

    private double defaultMinValue = 0;

    private double defaultMaxValue = 100000000;

    private int time;
    private int checkEvery;



    public void load(Configuration config) {
        lang = config.getString("lang");
        orderBy = config.getString("orderBy");
        dateFormat = config.getString("dateFormat");
        onBuyCommandUse = config.getBoolean("onBuy.sendCommand.use");
        onBuyCommand = config.getString("onBuy.sendCommand.command");
        securityForSpammingPacket = config.getBoolean("securityForSpammingPacket", true);
        time = config.getInt("expiration.time");
        checkEvery = config.getInt("expiration.checkEvery");
        minPrice = new HashMap<>();
        maxPrice = new HashMap<>();
        blacklistItem = new ArrayList<>();

        limitationsUseMetaLuckperms = config.getBoolean("limitations-use-meta-luckperms", false);
        limitations = new HashMap<>();
        for (String limitationGroup : config.getConfigurationSection("limitations").getKeys(false)) {
            limitations.put(limitationGroup, config.getInt("limitations." + limitationGroup));
        }

        if (config.contains("min-price-default")) {
            defaultMinValueEnable = config.getBoolean("min-price-default.enable");
            defaultMinValue = config.getDouble("min-price-default.value");
        }

        if (config.contains("max-price-default")) {
            defaultMaxValueEnable = config.getBoolean("max-price-default.enable");
            defaultMaxValue = config.getDouble("max-price-default.value");
        }

        if (config.contains("min-price")) {
            minPrice = new HashMap<>();
            for (String material : config.getConfigurationSection("min-price").getKeys(false)) {
                minPrice.put(Material.valueOf(material), config.getDouble("min-price." + material));
            }
        }

        if (config.contains("max-price")) {
            maxPrice = new HashMap<>();
            for (String material : config.getConfigurationSection("max-price").getKeys(false)) {
                maxPrice.put(Material.valueOf(material), config.getDouble("max-price." + material));
            }
        }

        if (config.contains("item-blacklist")) {
            blacklistItem = config.getStringList("item-blacklist").stream().map(Material::valueOf).toList();
        }
    }

    public int getTime() {
        return time;
    }

    public int getCheckEvery() {
        return checkEvery;
    }

    public Map<String, Integer> getLimitations() {
        return limitations;
    }

    public boolean isOnBuyCommandUse() {
        return onBuyCommandUse;
    }

    public String getOnBuyCommand() {
        return onBuyCommand;
    }

    public Map<Material, Double> getMinPrice() {
        return minPrice;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public String getLang() {
        return lang;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public boolean isSecurityForSpammingPacket() {
        return securityForSpammingPacket;
    }

    public Map<Material, Double> getMaxPrice() {
        return maxPrice;
    }

    public boolean isDefaultMaxValueEnable() {
        return defaultMaxValueEnable;
    }

    public boolean isDefaultMinValueEnable() {
        return defaultMinValueEnable;
    }

    public double getDefaultMinValue() {
        return defaultMinValue;
    }

    public double getDefaultMaxValue() {
        return defaultMaxValue;
    }

    public List<Material> getBlacklistItem() {
        return blacklistItem;
    }

    public boolean isLimitationsUseMetaLuckperms() {
        return limitationsUseMetaLuckperms;
    }
}
