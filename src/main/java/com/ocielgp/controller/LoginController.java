package com.ocielgp.controller;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.GlobalController;
import com.ocielgp.database.staff.DATA_STAFF_MEMBERS;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.utilities.InputDetails;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Validator;
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

public class LoginController implements Initializable {
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
    private byte attemps = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ConfigFiles.getDefaultImage().thenAccept(image -> Platform.runLater(() -> this.imageUser.setImage(image)));

        this.buttonLogin.setOnAction(actionEvent -> {
            ArrayList<InputDetails> inputs = new ArrayList<>();
            inputs.add(new InputDetails(this.fieldUsername, this.fieldUsername.getText()));
            inputs.add(new InputDetails(this.fieldPassword, this.fieldPassword.getText()));
            if (Validator.emptyValidator(inputs.listIterator())) {
                this.boxLoginPane.setDisable(true);
                CompletableFuture.runAsync(() -> {
                    Boolean answer = DATA_STAFF_MEMBERS.Login(this.fieldUsername.getText(), this.fieldPassword.getText());
                    if (answer == null) {
                        Platform.runLater(() -> new Flash(this.boxLoginPane).play());
                        Notifications.warn("Bloqueado", "Esta cuenta se encuentra bloqueada.");
                    } else if (answer) {
                        Node dashboardFXML = Loader.Load(
                                "dashboard.fxml",
                                "Login",
                                false
                        );
                        Platform.runLater(() -> {
                            FadeOutDown fadeOutDown = new FadeOutDown(this.boxLoginPane);
                            fadeOutDown.setOnFinished((action) -> {
                                GlobalController.appController.borderPane.setCenter(dashboardFXML);
                                new FadeIn(GlobalController.appController.borderPane.getCenter()).play();
                            });
                            fadeOutDown.play();
                        });
                    } else {
                        this.attemps++;
                        if (attemps == 3) {
                            Notifications.danger("Intentos excedidos", "Si no recuerdas la contraseña, contacte con el encargado.");
                            Platform.runLater(() -> new Flash(this.boxLoginPane).play());
                        } else {
                            Notifications.danger("Error", "Usuario / Contraseña incorrectos.", 2);
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
            this.fieldUsername.requestFocus();
            this.fieldUsername.setText("Ociel");
            this.fieldPassword.setText("dos");
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
