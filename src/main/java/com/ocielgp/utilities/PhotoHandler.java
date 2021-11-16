package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;


public class PhotoHandler {
    private final FormChangeListener formChangeListener;
    private final ImageView imageView;
    private byte[] bytes;
    private final JFXButton buttonDeletePhoto;

    public PhotoHandler(FormChangeListener formChangeListener, ImageView imageView, JFXButton buttonDeletePhoto) {
        this.formChangeListener = formChangeListener;
        this.imageView = imageView;
        this.buttonDeletePhoto = buttonDeletePhoto;

        imageView.setOnMouseClicked(mouseEvent -> browseImage());
        imageView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.ENTER) {
                browseImage();
            }
        });
        buttonDeletePhoto.addEventFilter(ActionEvent.ACTION, actionEvent -> removeImage());

        this.imageView.setImage(FileLoader.getDefaultImage());
    }


    private void browseImage() {
        // stop fingerprint to don't crash the app
        if (Fingerprint_Controller.IsConnected()) Fingerprint_Controller.StopCapture();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File lastSavedFolder = UserPreferences.GetFolderPath();
        if (lastSavedFolder != null) { // open last directory if exists
            fileChooser.setInitialDirectory(lastSavedFolder);
        }

        File file = fileChooser.showOpenDialog(Application.STAGE_PRIMARY);
        if (file != null) {
            UserPreferences.SetFolderPath(file); // save last directory
            this.imageView.setImage(new Image(file.toURI().toString()));
            this.buttonDeletePhoto.setDisable(false);
            try {
                this.bytes = Files.readAllBytes(file.toPath());
                if (this.formChangeListener.isListen()) this.formChangeListener.change("photo", false);
            } catch (Exception exception) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception);
            }
        }

        if (Fingerprint_Controller.IsConnected()) Fingerprint_Controller.BackgroundReader();
    }

    private void removeImage() {
        this.imageView.requestFocus();
        this.imageView.setImage(FileLoader.getDefaultImage());
        this.buttonDeletePhoto.setDisable(true);
        this.bytes = null;
        formChangeListener.change("photo", false);
    }

    public void setPhoto(byte[] bytes) {
        if (bytes != null) {
            this.imageView.setImage(FileLoader.loadImage(bytes));
            this.bytes = bytes;
            this.buttonDeletePhoto.setDisable(false);
        } else {
            this.imageView.setImage(FileLoader.getDefaultImage());
            this.buttonDeletePhoto.setDisable(true);
        }
    }

    public byte[] getPhoto() {
        return this.bytes;
    }

    public void restartPane() {
        this.imageView.setImage(FileLoader.getDefaultImage());
        this.bytes = null;
        this.buttonDeletePhoto.setDisable(true);
    }
}
