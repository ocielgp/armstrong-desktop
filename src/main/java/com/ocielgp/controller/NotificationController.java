package com.ocielgp.controller;

import com.ocielgp.model.NotificationModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {
    // Containers
    @FXML
    private GridPane container;

    // Controls
    @FXML
    private FontIcon icon;
    @FXML
    private Label title;
    @FXML
    private Label content;

    // Attributes
    private NotificationModel notification;

    public NotificationController(NotificationModel notification) {
        this.notification = notification;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.icon.setIconLiteral(this.notification.getIcon());
        this.title.setText(this.notification.getTitle());
        this.content.setText(this.notification.getContent());

        // Add styles
        this.container.getStyleClass().add(AppController.themeType);
        this.container.getStyleClass().add(this.notification.getStyle());
    }
}
