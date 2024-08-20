package com.rainchat.lastjump.plugin.data.database;

import com.rainchat.lastjump.LastJump;
import com.rainchat.lastjump.common.utils.general.Utils;
import com.rainchat.lastjump.common.utils.storage.YAML;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Database {

    private final LastJump plugin;
    private final YAML config;

    private Connection connection;

    public Database(LastJump plugin, YAML config) {
        this.plugin = plugin;
        this.config = config;
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.createStatement().execute("SELECT 1");
                    }
                } catch (SQLException e) {
                    connection = get();
                }
            }
        }).runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);
    }

    private Connection get() {
        try {
            if (config.getFileConfiguration().getBoolean("mysql.use")) {
                Utils.info("[Database] ( Connected ) ( MySQL )");
                Class.forName("com.mysql.jdbc.Driver");
                return DriverManager.getConnection("jdbc:mysql://"
                        + config.getFileConfiguration().getString("mysql.host")
                        + ":" + config.getFileConfiguration().getString("mysql.port")
                        + "/" + config.getFileConfiguration().getString("mysql.database"),
                        config.getFileConfiguration().getString("mysql.username"),
                        config.getFileConfiguration().getString("mysql.password"));
            } else {
                Utils.info("[Database] ( Connected ) ( SQLite )");
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "data.db"));
            }
        } catch (ClassNotFoundException | SQLException e) {
            Utils.exception(e.getStackTrace(), e.getMessage());
            return null;
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private boolean check() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = get();
            if (connection == null || connection.isClosed()) {
                return false;
            }
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS `arena` (`uuid` char(36), `arena` text(255), `score` bigint(255));");
        }
        return true;
    }

    public boolean set() {
        try {
            return check();
        } catch (SQLException e) {
            Utils.exception(e.getStackTrace(), e.getMessage());
            return false;
        }
    }

    public void insert(String uuid) {
        if (set()) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM arena WHERE uuid= '" + uuid + "';");
                        if (!resultSet.next()) {
                            preparedStatement = connection.prepareStatement("INSERT INTO arena (uuid, `arena`, score) VALUES(?, ?, ?);");
                            preparedStatement.setString(1, uuid);
                            preparedStatement.setString(2, "default");
                            preparedStatement.setLong(3, 0L);
                            preparedStatement.executeUpdate();
                        }
                    } catch (SQLException exception) {
                        Utils.exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        closeStatements(resultSet, preparedStatement);
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public void delete(String uuid) {
        if (set()) {
            BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        resultSet = connection.createStatement().executeQuery("SELECT * FROM arena WHERE uuid= '" + uuid + "';");
                        if (resultSet.next()) {
                            preparedStatement = connection.prepareStatement("DELETE FROM arena WHERE uuid = ?");
                            preparedStatement.setString(1, uuid);
                            preparedStatement.executeUpdate();
                        }
                    } catch (SQLException exception) {
                        Utils.exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        closeStatements(resultSet, preparedStatement);
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public void setValues(String uuid, String arena, long score) {
        if (set()) {
            BukkitRunnable r = new BukkitRunnable() {
                public void run() {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null;
                    try {
                        preparedStatement = connection.prepareStatement("REPLACE INTO arena (uuid, `arena`, score) VALUES(?, ?, ?)");
                        preparedStatement.setString(1, uuid);
                        preparedStatement.setString(2, arena);
                        preparedStatement.setLong(3, score);
                        preparedStatement.executeUpdate();
                    } catch (SQLException exception) {
                        Utils.exception(exception.getStackTrace(), exception.getMessage());
                    } finally {
                        closeStatements(resultSet, preparedStatement);
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        }
    }

    public HashMap<String,Integer> getValues(String uuid) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM arena WHERE uuid= '" + uuid + "';");
            HashMap<String, Integer> arenaScore = new HashMap<>();
            while (resultSet.next()) {
                arenaScore.put(resultSet.getString("arena"), resultSet.getInt("score"));
            }
            return arenaScore;
        } catch (SQLException exception) {
            Utils.exception(exception.getStackTrace(), exception.getMessage());
        } finally {
            closeStatements(resultSet, statement);
        }
        return new HashMap<>();
    }

    public LinkedHashMap<String,Integer> getArenaValues(String arena) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM arena WHERE arena= '" + arena + "';");
            LinkedHashMap<String, Integer> arenaScore = new LinkedHashMap<>();
            while (resultSet.next()) {
                arenaScore.put(resultSet.getString("uuid"), resultSet.getInt("score"));
            }
            return getSortedMap(arenaScore);
        } catch (SQLException exception) {
            Utils.exception(exception.getStackTrace(), exception.getMessage());
        } finally {
            closeStatements(resultSet, statement);
        }
        return new LinkedHashMap<>();
    }

    private LinkedHashMap<String, Integer> getSortedMap(Map<String, Integer> map) {
        return map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void closeStatements(ResultSet resultSet, PreparedStatement preparedStatement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException exception) {
                Utils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException exception) {
                Utils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
    }

    private void closeStatements(ResultSet resultSet, Statement statement) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException exception) {
                Utils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException exception) {
                Utils.exception(exception.getStackTrace(), exception.getMessage());
            }
        }
    }
}