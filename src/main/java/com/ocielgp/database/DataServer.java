package com.ocielgp.database;

import com.ocielgp.utilities.NotificationHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataServer {
    private static final String host;
    private static final String port;
    private static final String user;
    private static final String password;
    private static final String database;
    private static Connection con;

    static {
        Properties properties = new Properties();
        InputStream inputStream = DataServer.class.getClassLoader().getResourceAsStream("dataSource.properties");
        if (inputStream == null) {
            try {
                throw new FileNotFoundException("DataSource no encontrado");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Get data from config file
        host = properties.getProperty("host");
        port = properties.getProperty("port");
        user = properties.getProperty("user");
        password = properties.getProperty("password");
        database = properties.getProperty("database");
    }

    private static void firstConnection() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC", user, password);
            NotificationHandler.createNotification(
                    "gmi-cloud-done",
                    "Conexión establecida",
                    "Conexión con el servidor establecida",
                    2,
                    NotificationHandler.EPIC_STYLE
            );
            System.out.println("[DataServer]: Conectado a " + host);
        } catch (SQLException exception) {
            exception.printStackTrace();
            NotificationHandler.createNotification(
                    "gmi-cloud-off",
                    "Error",
                    "Hubo un problema al conectarse con el servidor",
                    3,
                    NotificationHandler.DANGER_STYLE
            );
        }
    }

    public static Connection getConnection() {
        try {
            if (con == null) {
                firstConnection();
            } else if (!con.isValid(3)) { // Reconnect if connection is lost
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
                System.out.println("[DataServer]: Reconectado a " + host);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            NotificationHandler.createNotification(
                    "gmi-cloud-off",
                    "Error",
                    "Hubo un problema al conectarse con el servidor",
                    3,
                    NotificationHandler.DANGER_STYLE
            );
        }
        return con;
    }
}