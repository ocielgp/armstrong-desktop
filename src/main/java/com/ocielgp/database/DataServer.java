package com.ocielgp.database;

import com.ocielgp.files.ConfigFiles;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class DataServer {
    private static final String host;
    private static final String port;
    private static final String user;
    private static final String password;
    private static final String database;
    private static final AtomicReference<Connection> con = new AtomicReference<>();

    private static boolean configValid = false;

    static {
        byte source = Byte.parseByte(ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "source"));

        // TODO: ENCRYPT PROPERTIES
        host = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "host" + source);
        port = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "port" + source);
        user = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "user" + source);
        password = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "password" + source);
        database = ConfigFiles.readProperty(ConfigFiles.File.DATASOURCE, "database");
        if (host != null && port != null && user != null && password != null && database != null) {
            configValid = true;
        }
        System.out.println(configValid);
    }

    private static void firstConnection() {
        try {
            con.set(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC", user, password));
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
    }

    synchronized public static Connection getConnection() {
        System.out.println("getConnection");
        if (configValid) {
            try {
                if (con.get() == null) {
                    DataServer.firstConnection();
                } else if (!con.get().isValid(3)) { // Reconnect if connection is lost
                    con.set(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC", user, password));
                    System.out.println("[DataServer]: Reconectado a " + host);
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException, "gmi-cloud-off");
            }
        }
        return con.get();
    }
}