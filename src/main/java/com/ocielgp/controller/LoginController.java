package com.ocielgp.controller;

import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutDown;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.database.AdministradorData;
import com.ocielgp.model.AdministradorModel;
import com.ocielgp.utilities.InputDetails;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.NotificationHandler;
import com.ocielgp.utilities.Validator;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    // Containers
    @FXML
    private HBox loginPane;

    // Controls
    @FXML
    private ImageView userImage;
    @FXML
    private JFXTextField userField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loginPane.setOpacity(0); // Hide loginPane

        this.userImage.setImage(new Image(Objects.requireNonNull(LoginController.class.getClassLoader().getResourceAsStream("default-user.png"))));
//        Ellipse ellipse = new Ellipse(75, 75);
//        ellipse.setCenterX(65);
//        ellipse.setCenterY(75);
//        userImage.setClip(ellipse);

        this.loginButton.setOnAction(actionEvent -> {
            if (
                    Validator.emptyValidator(
                            new InputDetails(userField, userField.getText()),
                            new InputDetails(passwordField, passwordField.getText()))
            ) {
                loginPane.setDisable(true);

                AdministradorModel administradorModel = AdministradorData.login(userField.getText(), passwordField.getText());
                if (administradorModel != null) { // If administrador exists, do this
                    FXMLLoader dashboard = new FXMLLoader(
                            Objects.requireNonNull(AppController.class.getClassLoader().getResource("dashboard.fxml"))
                    );
                    dashboard.setController(new DashboardController(administradorModel));
                    FadeOutDown fadeOutDown = new FadeOutDown(AppController.root.getCenter());
                    fadeOutDown.setOnFinished(evt -> {
                        try {
                            AppController.root.setCenter(dashboard.load());
                        } catch (IOException e) {
                            Loading.stopLoad();
                            e.printStackTrace();
                            NotificationHandler.danger("Error", "Hubo un problema al cargar el dashboard", 5);
                        }
                    });
                    fadeOutDown.play();
                    Loading.startLoad();
                } else { // If administrador doesn't exists, do this
                    loginPane.setDisable(false);
                }
            }
        });

        EventHandler<KeyEvent> enterKey = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        };
        this.userField.addEventFilter(KeyEvent.KEY_PRESSED, enterKey);
        this.passwordField.addEventFilter(KeyEvent.KEY_PRESSED, enterKey);
        Platform.runLater(() -> {
            // Animation initial
            new FadeInUp(loginPane).play();
            userField.requestFocus();
            userField.setText("ociel");
            passwordField.setText("dos");
        });
    }
}
