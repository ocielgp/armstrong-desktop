package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class PhotoHandler {
    private final ImageView imageViewPhoto;
    private byte[] bytes;
    private final JFXButton buttonDeletePhoto;

    public PhotoHandler(ImageView imageViewPhoto, JFXButton buttonUploadPhoto, JFXButton buttonDeletePhoto) {
        this.imageViewPhoto = imageViewPhoto;
        this.buttonDeletePhoto = buttonDeletePhoto;

        imageViewPhoto.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> browseImage());
        buttonUploadPhoto.addEventFilter(ActionEvent.ACTION, actionEvent -> browseImage());
        buttonDeletePhoto.addEventFilter(ActionEvent.ACTION, actionEvent -> deleteImage());
    }

    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(Window.getWindows().get(0));
        if (file != null) {
            this.imageViewPhoto.setImage(new Image(file.toURI().toString()));
            this.buttonDeletePhoto.setDisable(false);
            try {
                this.bytes = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                NotificationHandler.danger("PhotoHandler", "Error al guardar bytes de la foto.", 5);
                e.printStackTrace();
            }
        }

    }

    private void deleteImage() {
        this.imageViewPhoto.setImage(null);
        this.buttonDeletePhoto.setDisable(true);
        this.bytes = null;
        this.imageViewPhoto.requestFocus();
    }

    public byte[] getPhoto() {
        return this.bytes;
    }

}
