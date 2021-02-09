package com.ocielgp.controller;

import com.ocielgp.database.DataServer;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.NotificationHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    // Containers
    @FXML
    private BorderPane rootPane;

    // Controls
    @FXML
    private Button theme;
    @FXML
    private FontIcon iconTheme;

    // Attributes
    public static String themeType = "day-theme"; // Initial theme
    public static AppController root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // AppController Global
        AppController.root = this;

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

        try { // Login view
            FXMLLoader login = new FXMLLoader(
                    Objects.requireNonNull(AppController.class.getClassLoader().getResource("login.fxml"))
//                    Objects.requireNonNull(AppController.class.getClassLoader().getResource("summary.fxml"))
            );
            login.setController(new LoginController());
//            login.setController(new SummaryController());
            AppController.root.setCenter(login.load());
        } catch (IOException e) {
            NotificationHandler.danger("Error", "Hubo un problema al cargar el login", 5);
            e.printStackTrace();
        }

        // Connect to data source
        DataServer.getConnection();

        // Check if fingerprint scanner is connected
        Platform.runLater(Fingerprint::Scanner);
    }

    public void setTop(Node node) {
        rootPane.setTop(node);
    }

    public void setLeft(Node node) {
        this.rootPane.setLeft(node);
    }

    public void setCenter(Node node) {
        this.rootPane.setCenter(node);
    }

    public Node getCenter() {
        return this.rootPane.getCenter();
    }

    public void setRight(Node node) {
        this.rootPane.setRight(node);
    }
}