package com.ocielgp.files;

import com.jfoenix.controls.JFXRadioButton;
import com.ocielgp.app.GlobalController;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Pagination;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Properties;

public class ConfigFiles {

    public static String readProperty(File file, String property) {
        try {
            Properties properties = loadPropertiesFile(file);
            assert properties != null;
            String propertyString = properties.getProperty(property);
            if (propertyString == null) {
                throw new FileNotFoundException("[" + file + "]: property " + property + " no encontrado.");
            } else {
                return propertyString;
            }
        } catch (IOException ioException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    ioException.getMessage(),
                    ioException
            );
        }
        return null;
    }

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
        InputStream inputStream = GlobalController.class.getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        if (inputStream == null) {
            try {
                throw new FileNotFoundException("[file]: " + fileName + " no encontrado.");
            } catch (FileNotFoundException fileNotFoundException) {
                Notifications.catchError(
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
                Notifications.catchError(
                        MethodHandles.lookup().lookupClass().getSimpleName(),
                        Thread.currentThread().getStackTrace()[1],
                        ioException.getMessage(),
                        ioException
                );
            }
        }
        return null;
    }

    public static void saveProperty(File file, String property, String value) {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() {
                        String fileName = getFileName(file);
                        try {
                            Properties properties = loadPropertiesFile(file);
                            properties.setProperty(property, value);
                            FileOutputStream fileOutputStream = new FileOutputStream(
                                    Objects.requireNonNull(GlobalController.class.getClassLoader().getResource(fileName)).getPath()
                            );
                            properties.store(fileOutputStream, null);
                            fileOutputStream.close();
                        } catch (Exception exception) {
                            Notifications.catchError(
                                    MethodHandles.lookup().lookupClass().getSimpleName(),
                                    Thread.currentThread().getStackTrace()[1],
                                    exception.getMessage(),
                                    exception
                            );
                        }
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    public static void createSelectedToggleProperty(ToggleGroup toggle, String radioButtonPrefix, String property, Pagination pagination) {
        byte selectPreviousState = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, property)));
        for (byte i = 0; i < toggle.getToggles().size(); i++) {
            if (((JFXRadioButton) toggle.getToggles().get(i)).getId().equals(radioButtonPrefix + selectPreviousState)) {
                toggle.getToggles().get(i).setSelected(true);
            }
        }
        toggle.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != null && oldValue != newValue) {
                JFXRadioButton selected = (JFXRadioButton) newValue;
                ConfigFiles.saveProperty(File.APP, property, String.valueOf(selected.getId().charAt(selected.getId().length() - 1)));
                pagination.loadData(1);
            }
        });
    }

    public static ChangeListener<Object> listenerSaver(File file, String property, Pagination pagination) {
        return (observableValue, oldValue, newValue) -> {
            saveProperty(file, property, newValue.toString());
            pagination.loadData(1); // Refresh table
        };
    }

    public static Image getDefaultImage() {
        return loadImage("no-user-image.png");
    }

    public static Image getIconApp() {
        return loadImage("app-icon.png");
    }


    public static Image loadImage(String fileName) {
        InputStream file = ConfigFiles.class.getClassLoader().getResourceAsStream(fileName);
        Image image = null;
        try {
            image = new Image(file);
            file.close();
        } catch (Exception exception) {
            System.out.println("error");
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    exception.getMessage(),
                    exception
            );
        }
        return image;
    }

    public static Image loadImage(byte[] bytes) {
        ByteArrayInputStream imgBytes = new ByteArrayInputStream(bytes);
        Image image = new Image(imgBytes);
        try {
            imgBytes.close();
        } catch (IOException ioException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    ioException.getMessage(),
                    ioException
            );
        }
        return image;
    }

    public enum File {
        APP,
        DATASOURCE
    }
}
