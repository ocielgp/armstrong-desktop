package com.ocielgp.utilities;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class Photos {
    private ImageView imageViewPhoto;
    private JFXButton buttonDeletePhoto;

    public Photos(ImageView imageViewPhoto, JFXButton buttonDeletePhoto) {
        this.imageViewPhoto = imageViewPhoto;
        this.buttonDeletePhoto = buttonDeletePhoto;
    }

    private final EventHandler<ActionEvent> uploadPhoto = actionEvent -> {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(Window.getWindows().get(0));
        System.out.println(file);
        if (file != null) {
            this.imageViewPhoto.setImage(new Image(file.toURI().toString()));
            this.buttonDeletePhoto.setDisable(false);
        }
    };

    private final EventHandler<ActionEvent> deletePhoto = actionEvent -> {
        this.imageViewPhoto.setImage(null);
        this.buttonDeletePhoto.setDisable(true);
    };

    public EventHandler<ActionEvent> getUploadPhotoEvent() {
        return uploadPhoto;
    }

    public EventHandler<ActionEvent> getDeletePhotoEvent() {
        return deletePhoto;
    }
}
