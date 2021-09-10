package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import com.ocielgp.app.GlobalController;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.fingerprint.Fingerprint;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;


public class PhotoHandler {
    private final BooleanUpdater booleanUpdater;
    private final ImageView imageViewPhoto;
    private byte[] bytes;
    private final JFXButton buttonDeletePhoto;

    public PhotoHandler(BooleanUpdater booleanUpdater, ImageView imageViewPhoto, JFXButton buttonUploadPhoto, JFXButton buttonDeletePhoto) {
        this.booleanUpdater = booleanUpdater;
        this.imageViewPhoto = imageViewPhoto;
        this.buttonDeletePhoto = buttonDeletePhoto;

        imageViewPhoto.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> browseImage());
        buttonUploadPhoto.addEventFilter(ActionEvent.ACTION, actionEvent -> browseImage());
        buttonDeletePhoto.addEventFilter(ActionEvent.ACTION, actionEvent -> deleteImage());

        this.imageViewPhoto.setStyle("-fx-cursor: hand");
        ConfigFiles.getDefaultImage().thenAccept(image -> Platform.runLater(() -> this.imageViewPhoto.setImage(image)));
    }


    private void browseImage() {
        if (Fingerprint.getStatusCode() != 0) {
            Fingerprint.StopCapture();
            Fingerprint.ResetFingerprintUI();
        }
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // TODO: OPEN LAST LOCATION
        File file = fileChooser.showOpenDialog(GlobalController.getPrimaryStage());
        if (file != null) {
            this.imageViewPhoto.setImage(new Image(file.toURI().toString()));
            this.buttonDeletePhoto.setDisable(false);
            try {
                this.bytes = Files.readAllBytes(file.toPath());
                if (this.booleanUpdater.isListener()) {
                    this.booleanUpdater.change("photo", false);
                }

                fileChooser = null;
                file = null;
            } catch (Exception exception) {
                Notifications.catchError(
                        MethodHandles.lookup().lookupClass().getSimpleName(),
                        Thread.currentThread().getStackTrace()[1],
                        exception.getMessage(),
                        exception
                );
            }
        }

        if (Fingerprint.getStatusCode() != 0) {
            Fingerprint.StartCapture();
        }
    }

    private void deleteImage() {
        ConfigFiles.getDefaultImage().thenAccept(image -> Platform.runLater(() -> this.imageViewPhoto.setImage(image)));
        this.buttonDeletePhoto.setDisable(true);
        this.bytes = null;
        this.imageViewPhoto.requestFocus();
    }

    public void setPhoto(byte[] bytes) {
        if (bytes != null) {
            this.bytes = bytes;
            ConfigFiles.loadImage(bytes).thenAccept(image -> Platform.runLater(() -> this.imageViewPhoto.setImage(image)));
            this.buttonDeletePhoto.setDisable(false);
        } else {
            ConfigFiles.getDefaultImage().thenAccept(image -> Platform.runLater(() -> this.imageViewPhoto.setImage(image)));
            this.buttonDeletePhoto.setDisable(true);
        }
    }

    public byte[] getPhoto() {
        return this.bytes;
    }

    public void resetHandler() {
        ConfigFiles.getDefaultImage().thenAccept(image -> Platform.runLater(() -> this.imageViewPhoto.setImage(image)));
        this.bytes = null;
        this.buttonDeletePhoto.setDisable(true);
    }
}
