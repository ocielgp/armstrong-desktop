package com.ocielgp.utilities;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FileLoader {

    public static Image getDefaultImage() {
        return loadImage("no-user-image.png");
    }

    public static Image getIconApp() {
        return loadImage("app-icon.png");
    }

    public static Image loadImage(String fileName) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                InputStream file = FileLoader.class.getClassLoader().getResourceAsStream(fileName);
                Image image = null;
                try {
                    assert file != null;
                    image = new Image(file);
                    file.close();
                } catch (Exception exception) {
                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception);
                }
                return image;
            }).get();
        } catch (InterruptedException | ExecutionException exception) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception);
        }
        return null;
    }

    public static Image loadImage(byte[] bytes) {
        if (bytes == null) return getDefaultImage();
        try {
            return CompletableFuture.supplyAsync(() -> {
                ByteArrayInputStream imgBytes = new ByteArrayInputStream(bytes);
                Image image = new Image(imgBytes);
                try {
                    imgBytes.close();
                } catch (IOException ioException) {
                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], ioException);
                }
                return image;
            }).get();
        } catch (InterruptedException | ExecutionException exception) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception);
        }
        return null;
    }
}
