package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.database.DataServer;
import com.ocielgp.database.system.DATA_GYMS;
import com.ocielgp.database.system.MODEL_GYMS;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.fingerprint.Fingerprint;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    // Containers
    @FXML
    public BorderPane borderPane;

    // Controls
    @FXML
    private Button buttonTheme;
    @FXML
    private FontIcon iconTheme;
    @FXML
    private JFXComboBox<MODEL_GYMS> comboBoxGyms;

    // Attributes
    private String theme; // Initial theme

    public AppController() {
        this.theme = ConfigFiles.readProperty(ConfigFiles.File.APP, "theme");
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
            ConfigFiles.saveProperty(ConfigFiles.File.APP, "theme", this.theme);
        });

        /*Node loginFXML = Loader.Load(
                "login.fxml",
                "Root",
                false
        );
        this.borderPane.setCenter(loginFXML);*/

        // Connect to data source
        if (DataServer.getConnection() != null) {
            ObservableList<MODEL_GYMS> gyms = DATA_GYMS.ReadGyms();
            if (gyms == null) {
                this.comboBoxGyms.setDisable(true);
            } else {
                this.comboBoxGyms.setItems(gyms);
                int previousIdGym = Integer.parseInt(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "idGym")));
                for (MODEL_GYMS gym : gyms) {
                    if (previousIdGym == gym.getIdGym()) {
                        this.comboBoxGyms.getSelectionModel().select(gym);
                        break;
                    }
                }
                this.comboBoxGyms.valueProperty().addListener((observable, oldValue, newValue) -> ConfigFiles.saveProperty(ConfigFiles.File.APP, "idGym", String.valueOf(this.comboBoxGyms.getSelectionModel().getSelectedItem().getIdGym())));
                this.comboBoxGyms.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.comboBoxGyms.getStyleClass().remove("red-border-input-line"));
            }
        }

        // Check if fingerprint scanner is connected
        Platform.runLater(() -> {
            Fingerprint.Scanner();
            new FadeIn(this.borderPane).play();
        });
    }

    public MODEL_GYMS getGym() {
        return this.comboBoxGyms.getValue();
    }

    public JFXComboBox<MODEL_GYMS> getGymNode() {
        return this.comboBoxGyms;
    }

    public String getTheme() {
        return this.theme;
    }
}