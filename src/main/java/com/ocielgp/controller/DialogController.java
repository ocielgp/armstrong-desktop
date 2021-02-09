package com.ocielgp.controller;

import com.ocielgp.model.CustomDialogModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogController implements Initializable {
    // Containers
    @FXML
    private GridPane container;
    @FXML
    private HBox header;

    // Controls
    @FXML
    private FontIcon icon;
    @FXML
    private Label title;
    @FXML
    private Label content;
    @FXML
    private HBox buttonsContainer;

    // Attributes
    private CustomDialogModel dialog;

    public DialogController(CustomDialogModel dialog) {
        this.dialog = dialog;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.icon.setIconLiteral(this.dialog.getIcon());
        this.title.setText(this.dialog.getTitle());
        this.content.setText(this.dialog.getContent());
        this.buttonsContainer.getChildren().addAll(this.dialog.getButtons());

        System.out.println(this.dialog.getButtons()[0].getStyleClass().get(3));
        // Add styles
        this.header.getStyleClass().add(this.dialog.getButtons()[0].getStyleClass().get(3));
        this.container.getStyleClass().add(AppController.themeType);
    }
}
