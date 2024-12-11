package fr.florianpal.fauction;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import fr.florianpal.fauction.commands.AuctionCommand;
import fr.florianpal.fauction.managers.*;
import fr.florianpal.fauction.managers.commandmanagers.*;
import fr.florianpal.fauction.managers.implementations.LuckPermsImplementation;
import fr.florianpal.fauction.placeholders.FPlaceholderExpansion;
import fr.florianpal.fauction.queries.AuctionQueries;
import fr.florianpal.fauction.queries.ExpireQueries;
import fr.florianpal.fauction.queries.HistoricQueries;
import fr.florianpal.fauction.schedules.CacheSchedule;
import fr.florianpal.fauction.schedules.ExpireSchedule;
import fr.florianpal.fauction.utils.FileUtil;
import io.papermc.lib.PaperLib;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class FAuction extends JavaPlugin {

    private static FAuction api;

    private static TaskChainFactory taskChainFactory;

    private ConfigurationManager configurationManager;

    private AuctionQueries auctionQueries;

    private ExpireQueries expireQueries;

    private HistoricQueries historicQueries;

    private CommandManager commandManager;

    private VaultIntegrationManager vaultIntegrationManager;

    private DatabaseManager databaseManager;

    private LimitationManager limitationManager;

    private AuctionCommandManager auctionCommandManager;

    private ExpireCommandManager expireCommandManager;

    private HistoricCommandManager historicCommandManager;

    private SpamManager spamManager;

    private TransfertManager transfertManager;

    private Metrics metrics;

    private LuckPermsImplementation luckPermsImplementation;

    private boolean placeholderAPIEnabled = false;

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static TaskChainFactory getTaskChainFactory() {
        return taskChainFactory;
    }

    @Override
    public void onEnable() {

        metrics = new Metrics(this, 24018);
        PaperLib.suggestPaper(this);

        taskChainFactory = BukkitTaskChainFactory.create(this);

        configurationManager = new ConfigurationManager(this, this.getFile());

        if (configurationManager.getGlobalConfig().isLimitationsUseMetaLuckperms()) {
            luckPermsImplementation = new LuckPermsImplementation();
        }

        File languageFile = new File(getDataFolder(), "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");
        FileUtil.createDefaultConfiguration(this, this.getFile(), languageFile, "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");

        commandManager = new CommandManager(this);
        commandManager.registerDependency(ConfigurationManager.class, configurationManager);

        limitationManager = new LimitationManager(this);

        vaultIntegrationManager = new VaultIntegrationManager(this);

        try {
            databaseManager = new DatabaseManager(this);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        auctionQueries = new AuctionQueries(this);
        expireQueries = new ExpireQueries(this);
        historicQueries = new HistoricQueries(this);

        databaseManager.addRepository(expireQueries);
        databaseManager.addRepository(auctionQueries);
        databaseManager.addRepository(historicQueries);
        databaseManager.initializeTables();

        auctionCommandManager = new AuctionCommandManager(this);
        expireCommandManager = new ExpireCommandManager(this);
        historicCommandManager = new HistoricCommandManager(this);

        spamManager = new SpamManager(this);
        transfertManager = new TransfertManager(this);

        commandManager.registerCommand(new AuctionCommand(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FPlaceholderExpansion(this).register();
            placeholderAPIEnabled = true;
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ExpireSchedule(this), configurationManager.getGlobalConfig().getCheckEvery(), configurationManager.getGlobalConfig().getCheckEvery());
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new CacheSchedule(this), configurationManager.getGlobalConfig().getUpdateCacheEvery(), configurationManager.getGlobalConfig().getUpdateCacheEvery());

        api = this;
    }

    public static FAuction getApi() {
        return api;
    }

    public void reloadConfiguration() {
        configurationManager.reload(this, this.getFile());
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public AuctionQueries getAuctionQueries() {
        return auctionQueries;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public VaultIntegrationManager getVaultIntegrationManager() {
        return vaultIntegrationManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public AuctionCommandManager getAuctionCommandManager() {
        return auctionCommandManager;
    }

    public LimitationManager getLimitationManager() {
        return limitationManager;
    }

    public ExpireQueries getExpireQueries() {
        return expireQueries;
    }

    public ExpireCommandManager getExpireCommandManager() {
        return expireCommandManager;
    }

    public LuckPermsImplementation getLuckPermsImplementation() {
        return luckPermsImplementation;
    }

    public HistoricQueries getHistoricQueries() {
        return historicQueries;
    }

    public HistoricCommandManager getHistoricCommandManager() {
        return historicCommandManager;
    }

    public SpamManager getSpamManager() {
        return spamManager;
    }

    public TransfertManager getTransfertManager() {
        return transfertManager;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }
}
