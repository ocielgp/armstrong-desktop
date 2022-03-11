package com.ocielgp.dao;

import com.ocielgp.app.UserPreferences;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataServer {
    private static final HikariConfig hikariConfig;
    private static HikariDataSource hikariDataSource;
    private static final String host;
    private static final String port;
    private static final String user;
    private static final String password;
    private static final String database;

    static {
        int source = UserPreferences.GetPreferenceInt("DB_SOURCE");
        database = UserPreferences.GetPreferenceString("DB_NAME");
        host = UserPreferences.GetPreferenceString("DB_HOST_" + source);
        port = UserPreferences.GetPreferenceString("DB_PORT_" + source);
        user = UserPreferences.GetPreferenceString("DB_USER_" + source);
        password = UserPreferences.GetPreferenceString("DB_PASSWORD_" + source);

        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setConnectionTimeout(5000);
        System.out.println("[DataServer]: Connecting to " + host + "...");
        try {
            hikariDataSource = new HikariDataSource(hikariConfig);
            System.out.println("[DataServer]: Connected at " + host);
            Notifications.BuildNotification(
                    "gmi-cloud-done",
                    "Conexión establecida",
                    "Conexión con el servidor establecida",
                    3,
                    Styles.EPIC
            );
        } catch (Exception exception) {
            Notifications.BuildNotification(
                    "gmi-cloud-off",
                    "Sin conexión",
                    "Problema al conectar con el servidor\n" + exception.getMessage(),
                    60,
                    Styles.DANGER
            );
        }
    }

    synchronized public static Connection GetConnection() {
        try {
            Connection connection = hikariDataSource.getConnection();
            return connection;
        } catch (Exception ignored) {
            // reconnecting
            System.out.println("[DataServer]: Reconectando a " + host + "...");
            try {
                hikariDataSource = new HikariDataSource(hikariConfig);
                Connection connection = hikariDataSource.getConnection();
                System.out.println("[DataServer]: Conectado a " + host);
                return connection;
            } catch (Exception exception) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception);
                System.out.println("[DataServer]: Desconectado");
            }
        }
        return null;
    }

    synchronized public static void CloseConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        }
    }

    public static int CountRows(PreparedStatement ps) {
        try {
            ResultSet rs = ps.executeQuery();
            if (rs.last()) {
                return rs.getRow();
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        }
        return 0;
    }
}