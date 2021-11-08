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
    private final ImageView imageViewPhoto;
    private byte[] bytes;
    private final JFXButton buttonDeletePhoto;

    public PhotoHandler(FormChangeListener formChangeListener, ImageView imageViewPhoto, JFXButton buttonDeletePhoto) {
        this.formChangeListener = formChangeListener;
        this.imageViewPhoto = imageViewPhoto;
        this.buttonDeletePhoto = buttonDeletePhoto;

        imageViewPhoto.setOnMouseClicked(mouseEvent -> browseImage());
        imageViewPhoto.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.ENTER) {
                browseImage();
            }
        });
        buttonDeletePhoto.addEventFilter(ActionEvent.ACTION, actionEvent -> deleteImage());

        this.imageViewPhoto.setStyle("-fx-cursor: hand");
        this.imageViewPhoto.setImage(FileLoader.getDefaultImage());
    }


    private void browseImage() {
        if (Fingerprint_Controller.getStatusCode() != 0) {
            Fingerprint_Controller.FB_StopReader();
        }
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // TODO: OPEN LAST LOCATION
        File folder = UserPreferences.getFolderPath();
        if (folder != null) {
            fileChooser.setInitialDirectory(folder);
        }

        File file = fileChooser.showOpenDialog(Application.STAGE_PRIMARY);
        if (file != null) {
            UserPreferences.setFolderPath(file);
            this.imageViewPhoto.setImage(new Image(file.toURI().toString()));
            this.buttonDeletePhoto.setDisable(false);
            try {
                this.bytes = Files.readAllBytes(file.toPath());
                if (this.formChangeListener.isListen()) {
                    this.formChangeListener.change("photo", false);
                }
            } catch (Exception exception) {
                Notifications.CatchException(
                        MethodHandles.lookup().lookupClass().getSimpleName(),
                        Thread.currentThread().getStackTrace()[1],
                        exception.getMessage(),
                        exception
                );
            }
        }

        if (Fingerprint_Controller.getStatusCode() != 0) {
            Fingerprint_Controller.StartCapture();
        }
    }

    private void deleteImage() {
        this.imageViewPhoto.setImage(FileLoader.getDefaultImage());
        this.buttonDeletePhoto.setDisable(true);
        this.bytes = null;
        this.imageViewPhoto.requestFocus();
        formChangeListener.change("photo", false);
    }

    public void setPhoto(byte[] bytes) {
        if (bytes != null) {
            this.bytes = bytes;
            this.imageViewPhoto.setImage(FileLoader.loadImage(bytes));
            this.buttonDeletePhoto.setDisable(false);
        } else {
            this.imageViewPhoto.setImage(FileLoader.getDefaultImage());
            this.buttonDeletePhoto.setDisable(true);
        }
    }

    public byte[] getPhoto() {
        return this.bytes;
    }

    public void resetHandler() {
        this.imageViewPhoto.setImage(FileLoader.getDefaultImage());
        this.bytes = null;
        this.buttonDeletePhoto.setDisable(true);
    }
}
