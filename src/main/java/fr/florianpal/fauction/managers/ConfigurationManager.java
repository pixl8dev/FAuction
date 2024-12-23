package fr.florianpal.fauction.managers;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.CategoriesConfig;
import fr.florianpal.fauction.configurations.DatabaseConfig;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.configurations.gui.*;
import fr.florianpal.fauction.utils.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigurationManager {
    private final DatabaseConfig database = new DatabaseConfig();
    private final FileConfiguration databaseConfig;

    private final AuctionConfig auctionConfig = new AuctionConfig();
    private FileConfiguration auctionConfiguration;

    private final HistoricConfig historicConfig = new HistoricConfig();
    private FileConfiguration historicConfiguration;

    private final PlayerViewConfig playerViewConfig = new PlayerViewConfig();
    private FileConfiguration playerViewConfiguration;

    private final ExpireGuiConfig expireConfig = new ExpireGuiConfig();
    private FileConfiguration expireConfiguration;

    private final AuctionConfirmGuiConfig auctionConfirmConfig = new AuctionConfirmGuiConfig();
    private FileConfiguration auctionConfirmConfiguration;

    private final GlobalConfig globalConfig = new GlobalConfig();
    private FileConfiguration globalConfiguration;


    private final CategoriesConfig categoriesConfig = new CategoriesConfig();
    private FileConfiguration categoriesConfiguration;

    public ConfigurationManager(FAuction plugin, File pluginFile) {
        File databaseFile = new File(plugin.getDataFolder(), "database.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile, databaseFile, "database.yml");
        databaseConfig = YamlConfiguration.loadConfiguration(databaseFile);
        database.load(databaseConfig);
        loadAllConfiguration(plugin, pluginFile);
    }

    public void reload(FAuction plugin, File pluginFile) {
        loadAllConfiguration(plugin, pluginFile);
    }

    private void loadAllConfiguration(FAuction plugin, File pluginFile) {
        File auctionFile = new File(plugin.getDataFolder(), "gui/auction.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile, auctionFile, "gui/auction.yml");
        auctionConfiguration = YamlConfiguration.loadConfiguration(auctionFile);

        File historicFile = new File(plugin.getDataFolder(), "gui/historic.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile,historicFile, "gui/historic.yml");
        historicConfiguration = YamlConfiguration.loadConfiguration(historicFile);

        File playerViewFile = new File(plugin.getDataFolder(), "gui/playerView.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile, playerViewFile, "gui/playerView.yml");
        playerViewConfiguration = YamlConfiguration.loadConfiguration(playerViewFile);

        File expireFile = new File(plugin.getDataFolder(), "gui/expire.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile, expireFile, "gui/expire.yml");
        expireConfiguration = YamlConfiguration.loadConfiguration(expireFile);

        File auctionConfirmFile = new File(plugin.getDataFolder(), "gui/auctionConfirm.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile, auctionConfirmFile, "gui/auctionConfirm.yml");
        auctionConfirmConfiguration = YamlConfiguration.loadConfiguration(auctionConfirmFile);

        File globalFile = new File(plugin.getDataFolder(), "config.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile, globalFile, "config.yml");
        globalConfiguration = YamlConfiguration.loadConfiguration(globalFile);

        File categoriesFile = new File(plugin.getDataFolder(), "categories.yml");
        FileUtil.createDefaultConfiguration(plugin, pluginFile, categoriesFile, "categories.yml");
        categoriesConfiguration = YamlConfiguration.loadConfiguration(categoriesFile);

        globalConfig.load(globalConfiguration);
        categoriesConfig.load(categoriesConfiguration);
        auctionConfig.load(plugin, auctionConfiguration);
        historicConfig.load(plugin, historicConfiguration);
        auctionConfirmConfig.load(plugin, auctionConfirmConfiguration);
        expireConfig.load(plugin, expireConfiguration);
        playerViewConfig.load(plugin, playerViewConfiguration);
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public AuctionConfig getAuctionConfig() {
        return auctionConfig;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public AuctionConfirmGuiConfig getAuctionConfirmConfig() {
        return auctionConfirmConfig;
    }

    public ExpireGuiConfig getExpireConfig() {
        return expireConfig;
    }

    public PlayerViewConfig getPlayerViewConfig() {
        return playerViewConfig;
    }

    public CategoriesConfig getCategoriesConfig() {
        return categoriesConfig;
    }

    public HistoricConfig getHistoricConfig() {
        return historicConfig;
    }
}
