package com.ocielgp.controller;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.dao.JDBC_Admins;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

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
            FadeInUp fadeInUp = new FadeInUp(this.boxRoot);
            fadeInUp.setOnFinished(actionEvent -> {
                this.fieldUsername.setText("Ociel");
                this.fieldPassword.setText("dos");
                this.fieldUsername.requestFocus();
            });
            fadeInUp.play();
        });
    }

    private void auth() {
        if (Validator.emptyValidator(this.fieldUsername, this.fieldPassword)) {
            this.boxRoot.setDisable(true);
            CompletableFuture.runAsync(() -> {
                Loading.show();
                Boolean isValidUser = JDBC_Admins.ReadLogin(this.fieldUsername.getText(), this.fieldPassword.getText());
                if (isValidUser == null) {
                    Platform.runLater(() -> new Flash(this.boxRoot).play());
                    Notifications.Warn("Bloqueado", "Esta cuenta se encuentra bloqueada");
                    Loading.closeNow();
                } else if (isValidUser) {
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
