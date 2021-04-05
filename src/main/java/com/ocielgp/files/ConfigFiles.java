package com.ocielgp.files;

import com.ocielgp.app.AppController;
import com.ocielgp.utilities.NotificationHandler;
import com.ocielgp.utilities.Pagination;
import javafx.beans.value.ChangeListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

public class ConfigFiles {
    private static String getFileName(File file) {
        switch (file) {
            case APP:
                return "app.properties";
            case DATASOURCE:
                return "dataSource.properties";
            default:
                throw new IllegalStateException("Unexpected value: " + file);
        }
    }

    private static Properties loadPropertiesFile(File file) {
        String fileName = getFileName(file);
        InputStream inputStream = AppController.class.getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        if (inputStream == null) {
            try {
                throw new FileNotFoundException("[file]: " + fileName + " no encontrado.");
            } catch (FileNotFoundException fileNotFoundException) {
                NotificationHandler.catchError(
                        MethodHandles.lookup().lookupClass().getSimpleName(),
                        Thread.currentThread().getStackTrace()[1],
                        fileNotFoundException.getMessage(),
                        fileNotFoundException
                );
            }
        } else {
            try {
                properties.load(inputStream);
                inputStream.close();
                return properties;
            } catch (IOException ioException) {
                NotificationHandler.catchError(
                        MethodHandles.lookup().lookupClass().getSimpleName(),
                        Thread.currentThread().getStackTrace()[1],
                        ioException.getMessage(),
                        ioException
                );
            }
        }
        return null;
    }

    public static String readProperty(File file, String property) {
        try {
            Properties properties = loadPropertiesFile(file);
            String propertyString = properties.getProperty(property);
            if (propertyString == null) {
                throw new FileNotFoundException("[" + file + "]: property " + property + " no encontrado.");
            } else {
                return propertyString;
            }
        } catch (IOException ioException) {
            NotificationHandler.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    ioException.getMessage(),
                    ioException
            );
        }
        System.out.println("nulo");
        return null;
    }

    public static void saveProperty(File file, String property, String value) {
        String fileName = getFileName(file);
        try {
            Properties properties = loadPropertiesFile(file);
            properties.setProperty(property, value);
            properties.store(new FileOutputStream(
                            AppController.class.getClassLoader().getResource(fileName).getPath()
                    ),
                    null
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ChangeListener<Object> listenerSaver(File file, String property, Pagination pagination) {
        return (observableValue, oldValue, newValue) -> {
            saveProperty(file, property, newValue.toString());
            pagination.loadData(1); // Refresh table
        };
    }

    public enum File {
        APP,
        DATASOURCE
    }
}
