package com.ocielgp.controller;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Gym;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.models.Model_Gym;
import com.ocielgp.utilities.Loader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_App implements Initializable {
    @FXML
    public BorderPane borderPaneRoot;

    @FXML
    private JFXComboBox<Model_Gym> comboBoxGyms;
    @FXML
    private HBox boxTheme;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.borderPaneRoot.getStyleClass().add(UserPreferences.getPreferenceString("THEME"));
        Application.setAppController(this, comboBoxGyms);

        this.comboBoxGyms.setDisable(true);

        this.boxTheme.setOnMouseClicked(mouseEvent -> eventChangeTheme());

        // recover last gym if exists
        readLastGym();

        // check if scanner is connected
        CompletableFuture.runAsync(Fingerprint_Controller::Scanner);

        Model_Admin modelAdmin = new Model_Admin();
        modelAdmin.setPassword("a94cbdca65dd4582c45c2b8dd97aec782baa8fbad32b73b547bf5b0e52ef58f3");
        modelAdmin.setIdRole(Short.valueOf("1"));
        modelAdmin.setIdMember(2);
        modelAdmin.setName("Ociel");
        modelAdmin.setLastName("Garcia");
        Application.setModelAdmin(modelAdmin);

        Platform.runLater(() -> {
            Node loginView = Loader.Load(
                    "login.fxml",
                    "Controller_App",
                    true
            );
            borderPaneRoot.setCenter(loginView);
        });
    }

    private void eventChangeTheme() {
        String newTheme = (UserPreferences.getPreferenceString("THEME").equals("day-theme")) ? "night-theme" : "day-theme";
        Platform.runLater(() -> this.borderPaneRoot.getStyleClass().set(1, newTheme));
        UserPreferences.setPreference("THEME", newTheme);
    }

    private void readLastGym() {
        JDBC_Gym.ReadGyms().thenAccept(model_gym -> {
            comboBoxGyms.setItems(model_gym);
            int previousIdGym = UserPreferences.getPreferenceInt("LAST_GYM");
            for (Model_Gym gym : model_gym) {
                if (previousIdGym == gym.getIdGym()) {
                    Platform.runLater(() -> this.comboBoxGyms.getSelectionModel().select(gym));
                    break;
                }
            }
            this.comboBoxGyms.valueProperty().addListener((observable, oldValue, newValue) -> UserPreferences.setPreference("LAST_GYM", Application.getCurrentGym().getIdGym()));
            this.comboBoxGyms.focusedProperty().addListener((observableValue, oldValue, newValue) -> comboBoxGyms.getStyleClass().remove("red-border-input-line"));
        });
    }

}