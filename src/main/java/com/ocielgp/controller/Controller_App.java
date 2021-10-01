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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_App implements Initializable {
    // Containers
    @FXML
    public BorderPane borderPane;

    // Controls
    @FXML
    private Button buttonTheme;
    @FXML
    private FontIcon iconTheme;
    @FXML
    private JFXComboBox<Model_Gym> comboBoxGyms;

    // Attributes
    private String theme; // Initial theme

    public Controller_App() {
        this.theme = UserPreferences.getPreferenceString("THEME");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.borderPane.getStyleClass().add(theme);
//        this.comboBoxGyms.setVisible(false);
        this.buttonTheme.setOnAction(actionEvent -> {
            if (this.borderPane.getStyleClass().get(1).equals("day-theme")) {
                this.borderPane.getStyleClass().set(1, "night-theme");
                this.theme = "night-theme";
            } else {
                this.borderPane.getStyleClass().set(1, "day-theme");
                this.theme = "day-theme";
            }
            UserPreferences.setPreference("THEME", this.theme);
        });

        // TODO: RESEARCH ABOUT THIS
        Notifications.initializeNotificationSystem();
        Platform.runLater(() -> {
            new FadeIn(this.borderPane).play();

            // Connect to data source
            CompletableFuture.runAsync(() -> {
                ObservableList<Model_Gym> modelGyms = JDBC_Gym.ReadGyms();
                Platform.runLater(() -> {
                    comboBoxGyms.setItems(modelGyms);
                    int previousIdGym = UserPreferences.getPreferenceInt("LAST_GYM");
                    for (Model_Gym gym : modelGyms) {
                        if (previousIdGym == gym.getIdGym()) {
                            comboBoxGyms.getSelectionModel().select(gym);
                            break;
                        }
                    }
                });
                comboBoxGyms.valueProperty().addListener((observable, oldValue, newValue) -> UserPreferences.setPreference("LAST_GYM", Application.getCurrentGym().getIdGym()));
                comboBoxGyms.focusedProperty().addListener((observableValue, oldValue, newValue) -> comboBoxGyms.getStyleClass().remove("red-border-input-line"));
            });

            // Check if fingerprint scanner is connected
            CompletableFuture.runAsync(Fingerprint_Controller::Scanner);
        });
    }

    public Model_Gym getGym() {
        return this.comboBoxGyms.getValue();
    }

    public JFXComboBox<Model_Gym> getGymNode() {
        return this.comboBoxGyms;
    }

    public String getTheme() {
        return this.theme;
    }
}