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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DataServer {
    private static final HikariConfig hikariConfig;
    private static HikariDataSource hikariDataSource;
    private static final String host;
    private static final String port;
    private static final String user;
    private static final String password;
    private static final String database;

    static {
        int source = UserPreferences.getPreferenceInt("DB_SOURCE");
        database = UserPreferences.getPreferenceString("DB_NAME");
        host = UserPreferences.getPreferenceString("DB_HOST_" + source);
        port = UserPreferences.getPreferenceString("DB_PORT_" + source);
        user = UserPreferences.getPreferenceString("DB_USER_" + source);
        password = UserPreferences.getPreferenceString("DB_PASSWORD_" + source);

        hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        /*hikariConfig.setMinimumIdle(0);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(35000);
        hikariConfig.setMaxLifetime(45000);*/
        hikariConfig.setKeepaliveTime(30000);
//        hikariConfig.setMaxLifetime(25000);
        hikariConfig.setConnectionTimeout(25000);
        hikariConfig.setLeakDetectionThreshold(3000);
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
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return hikariDataSource.getConnection();
                } catch (SQLException sqlException) {
                    // reconnecting
                    System.out.println("[DataServer]: Reconectando a " + host + "...");
                    hikariDataSource = new HikariDataSource(hikariConfig);
                    try {
                        Connection connection = hikariDataSource.getConnection();
                        System.out.println("[DataServer]: Conectado a " + host);
                        return connection;
                    } catch (SQLException sqlException1) {
                        Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
                        System.out.println("[DataServer]: Desconectado");
                    }
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException exception) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception.getMessage(), exception);
        }
        return null;
    }

    synchronized public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
    }

    public static int countRows(PreparedStatement ps) {
        try {
            ResultSet rs = ps.executeQuery();
            if (rs.last()) {
                return rs.getRow();
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return 0;
    }
}