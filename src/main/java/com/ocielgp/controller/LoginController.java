package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import animatefx.animation.FadeOutDown;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.AppController;
import com.ocielgp.database.StaffUsersData;
import com.ocielgp.model.StaffUsersModel;
import com.ocielgp.utilities.InputDetails;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Validator;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
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

        this.loginButton.setOnAction(actionEvent -> {
            ArrayList<InputDetails> inputs = new ArrayList<>();
            inputs.add(new InputDetails(this.userField, this.userField.getText()));
            inputs.add(new InputDetails(this.passwordField, this.passwordField.getText()));
            if (Validator.emptyValidator(inputs.listIterator())) {
                this.loginPane.setDisable(true);

                StaffUsersModel staffUserModel = StaffUsersData.login(this.userField.getText(), this.passwordField.getText());
                if (staffUserModel != null) { // If administrador isn't null, do this
                    AppController.setStaffUserModel(staffUserModel);
                    Node dashboardFXML = Loader.Load(
                            "dashboard.fxml",
                            "Login",
                            false
                    );
                    FadeOutDown hideLoginPane = new FadeOutDown(this.loginPane);
                    hideLoginPane.setOnFinished(evt -> {
                        AppController.setCenter(dashboardFXML);
                        new FadeIn(dashboardFXML).play();
                    });
                    hideLoginPane.play();
                } else { // If administrador is null, do this
                    this.loginPane.setDisable(false);
                }
            }
        });

        EventHandler<KeyEvent> enterKey = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                this.loginButton.fire();
            }
        };
        this.userField.addEventFilter(KeyEvent.KEY_PRESSED, enterKey);
        this.passwordField.addEventFilter(KeyEvent.KEY_PRESSED, enterKey);
        Platform.runLater(() -> {
            // Animation initial
            new FadeInUp(this.loginPane).play();
            this.userField.requestFocus();
            this.userField.setText("ociel");
            this.passwordField.setText("dos");
        });
    }
}
