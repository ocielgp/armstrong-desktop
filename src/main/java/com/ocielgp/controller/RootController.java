package com.ocielgp.controller;

import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.app.AppController;
import com.ocielgp.database.DataServer;
import com.ocielgp.database.GymsData;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.model.GymsModel;
import com.ocielgp.utilities.Loader;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController implements Initializable {
    // Containers
    @FXML
    private BorderPane rootPane;

    // Controls
    @FXML
    private Button theme;
    @FXML
    private FontIcon iconTheme;
    @FXML
    private JFXComboBox<GymsModel> comboBoxGyms;

    // Attributes
    private String themeType = "night-theme"; // Initial theme

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // AppController Global
        AppController.startController(this);

        this.rootPane.getStyleClass().add(themeType);
        theme.setOnAction(actionEvent -> {
            if (rootPane.getStyleClass().get(1).equals("day-theme")) {
                rootPane.getStyleClass().set(1, "night-theme");
                themeType = "night-theme";
            } else {
                rootPane.getStyleClass().set(1, "day-theme");
                themeType = "day-theme";
            }
            /*String resp = DialogHandler.createDialog(
                    "gmi-snooze",
                    "Hola",
                    "Te quiero",
                    new JFXButton[]{CustomButton.create("Aceptar", CustomButton.PRIMARY_TYPE, CustomButton.SUCESS_STYLE)}
            );*/
//            System.out.println("h: " + resp);
//            new Administrator().login("uno", "dos");
//            NotificationHandler.createNotification("gmi-brightness-6", "El pepe", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 3, NotificationHandler.DEFAULT_STYLE);

//            NotificationHandler.createNotification("gmi-brightness-6", "El pepe", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 3, NotificationHandler.DEFAULT_STYLE);
//            NotificationHandler.createNotification("gmi-brightness-6", "El pepe", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 3, NotificationHandler.SUCESS_STYLE);
//            NotificationHandler.createNotification("gmi-brightness-6", "El pepe", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 3, NotificationHandler.WARNING_STYLE);
//            NotificationHandler.createNotification("gmi-brightness-6", "El pepe", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 3, NotificationHandler.DANGER_STYLE);
//            NotificationHandler.createNotification("gmi-brightness-6", "El pepe", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", 3, NotificationHandler.EPIC_STYLE);
        });

        Node loginFXML = Loader.Load( // Login view
                "login.fxml",
                "Root",
                false
        );
        this.rootPane.setCenter(loginFXML);
        new FadeInUp(loginFXML);

        // Connect to data source
        if (DataServer.getConnection() != null) {
            ObservableList<GymsModel> gyms = GymsData.getGyms();
            if (gyms == null) {
                this.comboBoxGyms.setDisable(true);
            } else {
                this.comboBoxGyms.setItems(GymsData.getGyms());
                this.comboBoxGyms.focusedProperty().addListener((observableValue, oldValue, newValue) -> this.comboBoxGyms.getStyleClass().remove("red-border-input-line"));
            }
        }

        // Check if fingerprint scanner is connected
        Platform.runLater(Fingerprint::Scanner);
    }

    public GymsModel getGym() {
        return this.comboBoxGyms.getValue();
    }

    public JFXComboBox<GymsModel> getGymNode() {
        return this.comboBoxGyms;
    }

    public String getThemeType() {
        return this.themeType;
    }

    public void setCenterContent(Node node) {
        this.rootPane.setCenter(node);
    }
}