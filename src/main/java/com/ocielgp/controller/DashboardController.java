package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import com.ocielgp.fingerprint.FingerprintUI;
import com.ocielgp.model.AdministradorModel;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.NotificationHandler;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    // Containers
    @FXML
    private GridPane rootPane;
    @FXML
    private ScrollPane content;

    // Controls
    @FXML
    private ImageView logo;
    @FXML
    private Label section;
    @FXML
    private ImageView userImage;
    @FXML
    private Label nombres;
    @FXML
    private FontIcon fingerprintIcon;
    @FXML
    private Label fingerprintStatus;

    // Routes
    @FXML
    private HBox navSummary;
    @FXML
    private HBox navMembers;

    private static EventHandler<MouseEvent> fingerprintEvent;

    public static AdministradorModel administradorModel;

    public DashboardController(AdministradorModel model) {
        administradorModel = model;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.userImage.setImage(new Image(Objects.requireNonNull(LoginController.class.getClassLoader().getResourceAsStream("default-user.png"))));
//        Ellipse ellipse = new Ellipse(36, 36);
//        ellipse.setCenterX(36);
//        ellipse.setCenterY(36);
//        userImage.setClip(ellipse);

        this.logo.setImage(new Image(Objects.requireNonNull(LoginController.class.getClassLoader().getResourceAsStream("img.jpg"))));

        this.rootPane.setOpacity(0); // Hide rootPane

        // Update content
        this.nombres.setText(administradorModel.getNombres());

        /* Routing */
        HashMap<HBox, String> routes = new HashMap<>();
        routes.put(navSummary, "summary.fxml");
        routes.put(navMembers, "members.fxml");
        EventHandler<MouseEvent> routeClick = new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                for (HBox route : routes.keySet()) {
                    if (route.getStyleClass().contains("selected")) {
                        route.getStyleClass().remove("selected");
                        route.addEventFilter(MouseEvent.MOUSE_CLICKED, this);
                        break;
                    }
                }

                HBox navOption = (HBox) mouseEvent.getSource();
                navOption.getStyleClass().add("selected");
                navOption.removeEventFilter(MouseEvent.MOUSE_CLICKED, this);
                try {
                    FXMLLoader view = new FXMLLoader(
                            Objects.requireNonNull(DashboardController.class.getClassLoader().getResource(routes.get(navOption)))
                    );
                    content.setContent(view.load());
                } catch (IOException e) {
                    e.printStackTrace();
                    NotificationHandler.danger("Error", "Hubo un problema al cargar " + routes.get(navOption), 5);
                }

            }
        };
        for (HBox route : routes.keySet()) {
            if (!route.getStyleClass().contains("selected")) {
                route.addEventFilter(MouseEvent.MOUSE_CLICKED, routeClick);
            }
        }
        /* End Routing */

        // Fingerprint
        Fingerprint.setFingerprintIcon(this.fingerprintIcon);
        Fingerprint.setFingerprintLabel(this.fingerprintStatus);

        Platform.runLater(() -> {
            Loading.stopLoad(new FadeIn(rootPane)); // Show rootPane after load

            FXMLLoader summary = new FXMLLoader(
                    Objects.requireNonNull(AppController.class.getClassLoader().getResource("summary.fxml"))
            );
            summary.setController(new SummaryController());
            try {
                content.setContent(summary.load());
            } catch (IOException e) {
                Loading.stopLoad();
                NotificationHandler.danger("Error", "Hubo un problema al cargar el summary", 5);
                e.printStackTrace();
            }
        });
    }
}
