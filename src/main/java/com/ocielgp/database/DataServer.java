package com.ocielgp.database;

import com.ocielgp.files.ConfigFiles;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DataServer {
    private static String host;
    private static String port;
    private static String user;
    private static String password;
    private static String database;
    private static Connection con;

    private static boolean configValid = false;

    static {
        try {
            byte source = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "source")));
            // TODO: ENCRYPT PROPERTIES
            host = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "host" + source);
            port = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "port" + source);
            user = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "user" + source);
            password = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "password" + source);
            database = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "database");

            if (host != null && port != null && user != null && password != null && database != null) {
                configValid = true;
            }
        } catch (Exception ignored) {
        }
    }

    synchronized private static Connection firstConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC", user, password);
            Notifications.createNotification(
                    "gmi-cloud-done",
                    "Conexión establecida",
                    "Conexión con el servidor establecida.",
                    3,
                    Styles.EPIC
            );
            System.out.println("[DataServer]: Conectado a " + host);
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException,
                    "gmi-cloud-off"
            );
        }
        return connection;
    }

    synchronized public static Connection getConnection() {
        if (configValid) {
            try {
                if (con == null) {
                    CompletableFuture.supplyAsync(
                            DataServer::firstConnection
                    ).thenApply(connection -> con = connection);
                } else if (!con.isValid(3)) { // Reconnect if connection is lost
                    con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC", user, password);
                    System.out.println("[DataServer]: Reconectado a " + host);
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException, "gmi-cloud-off");
            }
        }
        return con;
    }
}