package com.ocielgp.controller;

import animatefx.animation.FadeInUp;
import com.ocielgp.app.AppController;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Loader;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

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

    // User box
    @FXML
    private HBox user_container;
    @FXML
    private ImageView user_photo;
    @FXML
    private Label user_id;
    @FXML
    private Label user_name;
    @FXML
    private Label user_gym;
    @FXML
    private Label user_membership;

    public void showUserInfo(String style, Image photo, String id, String name, String gym, String membership) {
        this.user_container.getStyleClass().setAll(AppController.getThemeType(), style);
        this.user_photo.setImage(photo);
        this.user_id.setText(id);
        this.user_name.setText(name);
        this.user_gym.setText(gym);
        this.user_membership.setText(membership);

    }

    private static EventHandler<MouseEvent> fingerprintEvent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppController.setDashboardController(this);

        this.userImage.setImage(new Image(Objects.requireNonNull(LoginController.class.getClassLoader().getResourceAsStream("default-user.png"))));
        this.logo.setImage(new Image(Objects.requireNonNull(LoginController.class.getClassLoader().getResourceAsStream("img.jpg"))));

        // Update content
        this.nombres.setText(AppController.getStaffUserModel().getName());

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
                Node navFXML = Loader.Load(
                        routes.get(navOption),
                        "Dashboard",
                        true
                );
                content.setContent(navFXML);
                Input.getScrollEvent(content);
            }
        };
        for (HBox route : routes.keySet()) {
            if (!route.getStyleClass().contains("selected")) {
                route.addEventFilter(MouseEvent.MOUSE_CLICKED, routeClick);
            }
        }
        /* End Routing */

        // Fingerprint
        Fingerprint.initializeUI(this.fingerprintIcon, this.fingerprintStatus);

        Platform.runLater(() -> {
            Node summaryFXML = Loader.Load(
                    "summary.fxml",
                    "Dashboard",
                    true
            );
            this.content.setContent(summaryFXML);
            new FadeInUp(AppController.getCurrentGymNode()).play();

//            Node members = Loader.Load(
//                    "members.fxml",
//                    "Dashboard",
//                    true
//            );
//            this.content.setContent(members);
        });
    }
}
