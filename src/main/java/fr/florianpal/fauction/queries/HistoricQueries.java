package fr.florianpal.fauction.queries;

import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.enums.SQLType;
import fr.florianpal.fauction.managers.DatabaseManager;
import fr.florianpal.fauction.objects.Auction;
import fr.florianpal.fauction.objects.Historic;
import fr.florianpal.fauction.utils.SerializationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HistoricQueries implements IDatabaseTable {

    private final FAuction plugin;

    private final DatabaseManager databaseManager;

    private final GlobalConfig globalConfig;

    private static final String GET_HISTORICS = "SELECT * FROM fa_auctions_historic ORDER BY id ";
    private static final String GET_HISTORIC_WITH_ID = "SELECT * FROM fa_auctions_historic WHERE id=?";
    private static final String GET_HISTORICS_BY_UUID = "SELECT * FROM fa_auctions_historic WHERE playerUuid=?";
    private static final String ADD_HISTORIC = "INSERT INTO fa_auctions_historic (playerUuid, playerName, playerBuyerUuid, playerBuyerName, item, price, date) VALUES(?,?,?,?,?,?,?)";
    private static final String DELETE_ALL = "DELETE FROM fa_auctions_historic";


    private String autoIncrement = "AUTO_INCREMENT";

    private String parameters = "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci";

    public HistoricQueries(FAuction plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();

        this.globalConfig = plugin.getConfigurationManager().getGlobalConfig();
        if (plugin.getConfigurationManager().getDatabase().getSqlType() == SQLType.SQLite) {
            autoIncrement = "AUTOINCREMENT";
            parameters = "";
        }
    }

    public void addHistoric(UUID playerUUID, String playerName, UUID playerBuyerUUID, String playerBuyerName, byte[] item, double price, Date date) {
        PreparedStatement statement = null;
        try (Connection connection = databaseManager.getConnection()) {
            statement = connection.prepareStatement(ADD_HISTORIC);
            statement.setString(1, playerUUID.toString());
            statement.setString(2, playerName);
            statement.setString(3, playerBuyerUUID.toString());
            statement.setString(4, playerBuyerName);
            statement.setBytes(5, item);
            statement.setDouble(6, price);
            statement.setLong(7, date.getTime());
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe(String.join("Error when add auction. Error {} ", e.getMessage()));
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe(String.join("Error when close statement. Error {} ", e.getMessage()));
            }
        }
    }

    public void addHistoric(Auction auction, UUID playerBuyerUUID, String playerBuyerName) {
        PreparedStatement statement = null;
        try (Connection connection = databaseManager.getConnection()) {
            statement = connection.prepareStatement(ADD_HISTORIC);
            statement.setString(1, auction.getPlayerUUID().toString());
            statement.setString(2, auction.getPlayerName());
            statement.setString(3, playerBuyerUUID.toString());
            statement.setString(4, playerBuyerName);
            statement.setBytes(5, SerializationUtil.serialize(auction.getItemStack()));
            statement.setDouble(6, auction.getPrice());
            statement.setLong(7, auction.getDate().getTime());
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe(String.join("Error when add auction. Error {} ", e.getMessage()));
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe(String.join("Error when close statement. Error {} ", e.getMessage()));
            }
        }
    }

    public Map<Integer, byte[]> getHistoricBrut() {


        PreparedStatement statement = null;
        ResultSet result = null;
        Map<Integer, byte[]> auctions = new HashMap<>();
        try (Connection connection = databaseManager.getConnection()) {
            statement = connection.prepareStatement(GET_HISTORICS + this.globalConfig.getOrderBy());

            result = statement.executeQuery();

            while (result.next()) {
                int id = result.getInt(1);

                byte[] item = result.getBytes(6);
                auctions.put(id, item);
            }
            return auctions;
        } catch (SQLException e) {
            plugin.getLogger().severe(String.join("Error when get all auctions. Error {} ", e.getMessage()));
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe(String.join("Error when close statement. Error {} ", e.getMessage()));
            }
        }
        return auctions;
    }

    public List<Historic> getHistorics() {


        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<Historic> auctions = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection()) {
            statement = connection.prepareStatement(GET_HISTORICS + this.globalConfig.getOrderBy());

            result = statement.executeQuery();

            while (result.next()) {
                int id = result.getInt(1);
                UUID playerUUID = UUID.fromString(result.getString(2));
                String playerName = result.getString(3);
                UUID playerBuyerUUID = UUID.fromString(result.getString(4));
                String playerBuyerName = result.getString(5);

                byte[] item = result.getBytes(6);
                double price = result.getDouble(7);
                long date = result.getLong(8);

                auctions.add(new Historic(id, playerUUID, playerName, playerBuyerUUID, playerBuyerName, price, item, date));
            }
            return auctions;
        } catch (SQLException e) {
            plugin.getLogger().severe(String.join("Error when get all auction. Error {} ", e.getMessage()));
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe(String.join("Error when close statement. Error {} ", e.getMessage()));
            }
        }
        return auctions;
    }

    public List<Historic> getHistorics(UUID playerUuid) {

        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<Historic> auctions = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection()) {
            statement = connection.prepareStatement(GET_HISTORICS_BY_UUID);
            statement.setString(1, playerUuid.toString());
            result = statement.executeQuery();

            while (result.next()) {

                int id = result.getInt(1);
                UUID playerUUID = UUID.fromString(result.getString(2));
                String playerName = result.getString(3);
                UUID playerBuyerUUID = UUID.fromString(result.getString(4));
                String playerBuyerName = result.getString(5);

                byte[] item = result.getBytes(6);
                double price = result.getDouble(7);
                long date = result.getLong(8);

                auctions.add(new Historic(id, playerUUID, playerName, playerBuyerUUID, playerBuyerName, price, item, date));
            }
            return auctions;
        } catch (SQLException e) {
            plugin.getLogger().severe(String.join("Error when get auction by player uuid. Error {} ", e.getMessage()));
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe(String.join("Error when close statement. Error {} ", e.getMessage()));
            }
        }
        return auctions;
    }

    public Auction getHistoric(int id) {
        PreparedStatement statement = null;
        ResultSet result = null;
        try (Connection connection = databaseManager.getConnection()) {
            statement = connection.prepareStatement(GET_HISTORIC_WITH_ID);
            statement.setInt(1, id);
            result = statement.executeQuery();

            if (result.next()) {
                UUID playerUUID = UUID.fromString(result.getString(2));
                String playerName = result.getString(3);
                UUID playerBuyerUUID = UUID.fromString(result.getString(4));
                String playerBuyerName = result.getString(5);

                byte[] item = result.getBytes(6);
                double price = result.getDouble(7);
                long date = result.getLong(8);


                return new Historic(id, playerUUID, playerName, playerBuyerUUID, playerBuyerName, price, item, date);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(String.join("Error when get auction by id. Error {} ", e.getMessage()));
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe(String.join("Error when close statement. Error {} ", e.getMessage()));
            }
        }
        return null;
    }

    public void deleteAll() {
        PreparedStatement statement = null;
        try (Connection connection = databaseManager.getConnection()) {
            statement = connection.prepareStatement(DELETE_ALL);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe(String.join("Error when delete all historic to database. Error {} ", e.getMessage()));
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    plugin.getLogger().severe(String.join("Error when close statement. Error {} ", e.getMessage()));
                }
            }
        }
    }

    @Override
    public String[] getTable() {
        return new String[]{"fa_auctions_historic",
                "`id` INTEGER PRIMARY KEY " + autoIncrement + ", " +
                        "`playerUuid` VARCHAR(36) NOT NULL, " +
                        "`playerName` VARCHAR(36) NOT NULL, " +
                        "`playerBuyerUuid` VARCHAR(36) NOT NULL, " +
                        "`playerBuyerName` VARCHAR(36) NOT NULL, " +
                        "`item` BLOB NOT NULL, " +
                        "`price` DOUBLE NOT NULL, " +
                        "`date` LONG NOT NULL",
                parameters
        };
    }
}
