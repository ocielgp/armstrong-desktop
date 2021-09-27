package com.ocielgp.files;

import com.jfoenix.controls.JFXRadioButton;
import com.ocielgp.configuration.AppPreferences;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Pagination;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ConfigFiles {

    public static void createSelectedToggleProperty(ToggleGroup toggle, String radioButtonPrefix, String preference, Pagination pagination) {
        int selectPreviousState = AppPreferences.getPreferenceInt(preference);
        for (byte i = 0; i < toggle.getToggles().size(); i++) {
            if (((JFXRadioButton) toggle.getToggles().get(i)).getId().equals(radioButtonPrefix + selectPreviousState)) {
                toggle.getToggles().get(i).setSelected(true);
            }
        }
        toggle.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue != null && oldValue != newValue) {
                JFXRadioButton selected = (JFXRadioButton) newValue;
                AppPreferences.setPreference(preference, String.valueOf(selected.getId().charAt(selected.getId().length() - 1)));
                pagination.restartTable();
            }
        });
    }

    public static ChangeListener<Object> listenerSaver(String preference, Pagination pagination) {
        return (observableValue, oldValue, newValue) -> {
            AppPreferences.setPreference(preference, Boolean.parseBoolean(newValue.toString()));
            pagination.restartTable();
        };
    }

    public static Image getDefaultImage() {
        return loadImage("no-user-image.png");
    }

    public static Image getIconApp() {
        return loadImage("app-icon.png");
    }


    public static Image loadImage(String fileName) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                InputStream file = ConfigFiles.class.getClassLoader().getResourceAsStream(fileName);
                Image image = null;
                try {
                    image = new Image(file);
                    file.close();
                } catch (Exception exception) {
                    Notifications.catchError(
                            MethodHandles.lookup().lookupClass().getSimpleName(),
                            Thread.currentThread().getStackTrace()[1],
                            exception.getMessage(),
                            exception
                    );
                }
                return image;
            }).get();
        } catch (InterruptedException | ExecutionException exception) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    exception.getMessage(),
                    exception
            );
        }
        return null;
    }

    public static Image loadImage(byte[] bytes) {
        try {
            return CompletableFuture.supplyAsync(() -> {
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
            }).get();
        } catch (InterruptedException | ExecutionException exception) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    exception.getMessage(),
                    exception
            );
        }
        return null;
    }

    public enum File {
        APP,
        DATASOURCE
    }
}
