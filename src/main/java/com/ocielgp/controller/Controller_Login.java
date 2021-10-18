package com.ocielgp.controller;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.dao.JDBC_Staff_Member;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_Login implements Initializable {
    @FXML
    private HBox boxLoginPane;
    @FXML
    private ImageView imageUser;
    @FXML
    private JFXTextField fieldUsername;
    @FXML
    private JFXPasswordField fieldPassword;
    @FXML
    private JFXButton buttonLogin;

    // Attributes
    private byte attempts = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.imageUser.setImage(FileLoader.getDefaultImage());

        this.buttonLogin.setOnAction(actionEvent -> {
            ArrayList<InputDetails> inputs = new ArrayList<>();
            inputs.add(new InputDetails(this.fieldUsername, this.fieldUsername.getText()));
            inputs.add(new InputDetails(this.fieldPassword, this.fieldPassword.getText()));
            if (Validator.emptyValidator(inputs.listIterator())) {
                this.boxLoginPane.setDisable(true);
                CompletableFuture.runAsync(() -> {
                    Boolean answer = JDBC_Staff_Member.ReadLogin(this.fieldUsername.getText(), this.fieldPassword.getText());
                    if (answer == null) {
                        Platform.runLater(() -> new Flash(this.boxLoginPane).play());
                        Notifications.Warn("Bloqueado", "Esta cuenta se encuentra bloqueada.");
                    } else if (answer) {
                        Node dashboardFXML = Loader.Load(
                                "dashboard.fxml",
                                "Login",
                                false
                        );
                        Platform.runLater(() -> {
                            FadeOutDown fadeOutDown = new FadeOutDown(this.boxLoginPane);
                            fadeOutDown.setOnFinished((action) -> {
                                Application.controllerApp.borderPaneApp.setCenter(dashboardFXML);
                                new FadeIn(Application.controllerApp.borderPaneApp.getCenter()).play();
                            });
                            fadeOutDown.play();
                        });
                    } else {
                        this.attempts++;
                        if (attempts == 3) {
                            Notifications.Danger("Intentos excedidos", "Si no recuerdas la contraseña, contacte con el encargado");
                            Platform.runLater(() -> new Flash(this.boxLoginPane).play());
                        } else {
                            Notifications.Danger("Error", "Usuario / Contraseña incorrectos", 2);
                            Platform.runLater(() -> {
                                new Shake(this.boxLoginPane).play();
                                this.boxLoginPane.setDisable(false);
                            });
                        }
                    }
                });
            }

            // clear memory
            inputs = null;
        });

        this.fieldUsername.setOnKeyPressed(this.eventHandlerLogin());
        this.fieldPassword.setOnKeyPressed(this.eventHandlerLogin());
        Platform.runLater(() -> {
            new FadeInUp(this.boxLoginPane).play();
            this.fieldUsername.setText("Ociel");
            this.fieldPassword.setText("dos");
            this.fieldUsername.requestFocus();
        });
    }

    // Event handlers
    private EventHandler<KeyEvent> eventHandlerLogin() {
        return keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                this.buttonLogin.fire();
            }
        };
    }
}
