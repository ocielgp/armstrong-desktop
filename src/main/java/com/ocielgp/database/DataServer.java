package com.ocielgp.database;

import com.ocielgp.configuration.AppPreferences;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.SQLException;

public class DataServer {
    private static final HikariDataSource hikariDataSource;
    private static final String host;
    private static final String port;
    private static final String user;
    private static final String password;
    private static final String database;

    static {
        int source = AppPreferences.getPreferenceInt("DB_SOURCE");
        database = AppPreferences.getPreferenceString("DB_NAME");
        host = AppPreferences.getPreferenceString("DB_HOST_" + source);
        port = AppPreferences.getPreferenceString("DB_PORT_" + source);
        user = AppPreferences.getPreferenceString("DB_USER_" + source);
        password = AppPreferences.getPreferenceString("DB_PASSWORD_" + source);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.setLeakDetectionThreshold(8000);
        System.out.println("[DataServer]: Tratando de conectar a " + host + "...");
        hikariDataSource = new HikariDataSource(hikariConfig);
        System.out.println("[DataServer]: Conectado a " + host);
        Notifications.createNotification(
                "gmi-cloud-done",
                "Conexión establecida",
                "Conexión con el servidor establecida.",
                3,
                Styles.EPIC
        );
    }

    synchronized public static Connection getConnection() {
        System.out.println("getConnection()");
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return null;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
    }
}