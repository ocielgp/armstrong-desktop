package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Gym;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.models.Model_Gym;
import com.ocielgp.utilities.Notifications;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_App implements Initializable {
    @FXML
    public BorderPane borderPaneApp;

    @FXML
    private JFXComboBox<Model_Gym> comboBoxGyms;
    @FXML
    private HBox boxTheme;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        this.comboBoxGyms.setVisible(false);
        this.boxTheme.setOnMouseClicked(mouseEvent -> changeTheme());

        // TODO: RESEARCH ABOUT THIS
        Notifications.initializeNotificationSystem();
        Platform.runLater(() -> {
            this.borderPaneApp.getStyleClass().set(1, UserPreferences.getPreferenceString("THEME"));
            new FadeIn(this.borderPaneApp).play();

            // select last gym
            JDBC_Gym.ReadGyms().thenAccept(model_gym -> {
                comboBoxGyms.setItems(model_gym);
                int previousIdGym = UserPreferences.getPreferenceInt("LAST_GYM");
                for (Model_Gym gym : model_gym) {
                    if (previousIdGym == gym.getIdGym()) {
                        Platform.runLater(() -> {
                            this.comboBoxGyms.getSelectionModel().select(gym);
                        });
                        break;
                    }
                }
                this.comboBoxGyms.valueProperty().addListener((observable, oldValue, newValue) -> UserPreferences.setPreference("LAST_GYM", Application.getCurrentGym().getIdGym()));
                this.comboBoxGyms.focusedProperty().addListener((observableValue, oldValue, newValue) -> comboBoxGyms.getStyleClass().remove("red-border-input-line"));
            });

            // check if scanner is connected
            CompletableFuture.runAsync(Fingerprint_Controller::Scanner);
        });
    }

    private void changeTheme() {
        String newTheme = (UserPreferences.getPreferenceString("THEME").equals("day-theme")) ? "night-theme" : "day-theme";
        this.borderPaneApp.getStyleClass().set(1, newTheme);
        UserPreferences.setPreference("THEME", newTheme);
    }

    public Model_Gym getGym() {
        return this.comboBoxGyms.getValue();
    }

    public JFXComboBox<Model_Gym> getGymNode() {
        return this.comboBoxGyms;
    }

}