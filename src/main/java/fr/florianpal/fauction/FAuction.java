package fr.florianpal.fauction;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import fr.florianpal.fauction.commands.AuctionCommand;
import fr.florianpal.fauction.managers.ConfigurationManager;
import fr.florianpal.fauction.managers.DatabaseManager;
import fr.florianpal.fauction.managers.VaultIntegrationManager;
import fr.florianpal.fauction.managers.commandmanagers.*;
import fr.florianpal.fauction.managers.implementations.LuckPermsImplementation;
import fr.florianpal.fauction.placeholders.FPlaceholderExpansion;
import fr.florianpal.fauction.queries.AuctionQueries;
import fr.florianpal.fauction.queries.ExpireQueries;
import fr.florianpal.fauction.queries.HistoricQueries;
import fr.florianpal.fauction.schedules.CacheSchedule;
import fr.florianpal.fauction.schedules.ExpireSchedule;
import fr.florianpal.fauction.utils.SerializationUtil;
import io.papermc.lib.PaperLib;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class FAuction extends JavaPlugin {

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

    private boolean placeholderAPIEnabled = false;

    private LuckPermsImplementation luckPermsImplementation;

    private Metrics metrics;

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static TaskChainFactory getTaskChainFactory() {
        return taskChainFactory;
    }

    private static FAuction api;

    @Override
    public void onEnable() {

        metrics = new Metrics(this, 24018);
        PaperLib.suggestPaper(this);

        taskChainFactory = BukkitTaskChainFactory.create(this);

        configurationManager = new ConfigurationManager(this);

        if (configurationManager.getGlobalConfig().isLimitationsUseMetaLuckperms()) {
            luckPermsImplementation = new LuckPermsImplementation();
        }

        File languageFile = new File(getDataFolder(), "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");
        createDefaultConfiguration(languageFile, "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");

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

    public void createDefaultConfiguration(File actual, String defaultName) {
        // Make parent directories
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (actual.exists()) {
            return;
        }

        InputStream input = null;
        try {
            JarFile file = new JarFile(this.getFile());
            ZipEntry copy = file.getEntry(defaultName);
            if (copy == null) throw new FileNotFoundException();
            input = file.getInputStream(copy);
        } catch (IOException e) {
            getLogger().severe("Unable to read default configuration: " + defaultName);
        }

        if (input != null) {
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                getLogger().info("Default configuration file written: " + actual.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException ignored) {
                }

                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    public String parsePlaceholder(OfflinePlayer player, String text) {
        if (placeholderAPIEnabled) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    public void reloadConfiguration() {
        configurationManager.reload(this);
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



    public void transfertBDD(boolean toPaper) {

        FAuction.newChain().asyncFirst(() -> getAuctionQueries().getAuctionsBrut()).asyncLast(auctions -> {
            try {
                for (Map.Entry<Integer, byte[]> entry : auctions.entrySet()) {
                    if (toPaper) {
                        ItemStack item = SerializationUtil.deserializeBukkit(entry.getValue());
                        getAuctionQueries().updateItem(entry.getKey(), SerializationUtil.serializePaper(item));
                    } else {
                        ItemStack item = SerializationUtil.deserializePaper(entry.getValue());
                        getAuctionQueries().updateItem(entry.getKey(), SerializationUtil.serializeBukkit(item));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).execute();

        FAuction.newChain().asyncFirst(() -> getExpireQueries().getExpiresBrut()).asyncLast(expires -> {
            try {
                for (Map.Entry<Integer, byte[]> entry : expires.entrySet()) {
                    if (toPaper) {
                        ItemStack item = SerializationUtil.deserializeBukkit(entry.getValue());
                        getExpireQueries().updateItem(entry.getKey(), SerializationUtil.serializePaper(item));
                    } else {
                        ItemStack item = SerializationUtil.deserializePaper(entry.getValue());
                        getExpireQueries().updateItem(entry.getKey(), SerializationUtil.serializeBukkit(item));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).execute();
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
}
