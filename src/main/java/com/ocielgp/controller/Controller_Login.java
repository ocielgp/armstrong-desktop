package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOutDown;
import animatefx.animation.Flash;
import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.dao.JDBC_Admin;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Login implements Initializable {
    @FXML
    private HBox boxRoot;
    @FXML
    private ImageView imageUser;
    @FXML
    private JFXTextField fieldUsername;
    @FXML
    private JFXPasswordField fieldPassword;
    @FXML
    private JFXButton buttonLogin;

    // attributes
    private byte attempts = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.imageUser.setImage(FileLoader.getDefaultImage());
        InputProperties.createEventEnter(this.buttonLogin, this.fieldUsername, this.fieldPassword);
        this.buttonLogin.setOnAction(actionEvent -> auth());

        Platform.runLater(() -> {
            this.fieldUsername.setText("sistema");
            this.fieldPassword.setText("fsociety");
            this.fieldUsername.requestFocus();
        });
    }

    private void auth() {
        if (Validator.emptyValidator(this.fieldUsername, this.fieldPassword)) {
            this.boxRoot.setDisable(true);
            Loading.show();
            JDBC_Admin.ReadLogin(this.fieldUsername.getText(), this.fieldPassword.getText()).thenAccept(isValidUser -> {
                if (isValidUser == null) {
                    Platform.runLater(() -> new Flash(this.boxRoot).play());
                    Notifications.Warn("Bloqueado", "Esta cuenta se encuentra bloqueada");
                    Loading.closeNow();
                } else if ((Boolean) isValidUser) {
                    Node dashboardFXML = Loader.Load(
                            "dashboard.fxml",
                            "Login",
                            true
                    );
                    Platform.runLater(() -> {
                        FadeOutDown fadeOutDown = new FadeOutDown(this.boxRoot);
                        fadeOutDown.setOnFinished((action) -> {
                            Application.controllerApp.borderPaneRoot.setCenter(dashboardFXML);
                            new FadeIn(Application.controllerApp.borderPaneRoot.getCenter()).play();
                            Loading.isAnimationFinished.set(true);
                        });
                        fadeOutDown.play();
                    });
                } else {
                    this.attempts++;
                    if (attempts == 3) {
                        Notifications.Danger("Intentos excedidos", "Si no recuerdas la contraseña, contacte con el encargado");
                        Platform.runLater(() -> new Flash(this.boxRoot).play());
                    } else {
                        Notifications.Danger("Error", "Usuario / Contraseña incorrectos", 2);
                        Platform.runLater(() -> {
                            new Shake(this.boxRoot).play();
                            this.boxRoot.setDisable(false);
                        });
                    }
                    Loading.closeNow();
                }
            });
        }
    }
}
