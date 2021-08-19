package com.ocielgp.controller;

import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXDialog;
import com.ocielgp.app.GlobalController;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.HashMap;
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
    @FXML
    private HBox navSecureMode;

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

    public void showUserInfo(Styles style, byte[] photo, String idMember, String name, String gym, String membership) {
        this.user_container.getStyleClass().setAll(GlobalController.getThemeType(), Input.styleToColor(style));
        if (photo == null) {
            this.user_photo.setImage(ConfigFiles.loadImage("no-user-image.png"));
        } else {
            this.user_photo.setImage(ConfigFiles.loadImage(photo));
        }

        this.user_id.setText(idMember);
        this.user_name.setText(name);
        this.user_gym.setText(gym);
        this.user_membership.setText(membership);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GlobalController.setDashboardController(this);

        this.user_photo.setImage(ConfigFiles.loadImage("no-user-image.png"));
        this.userImage.setImage(ConfigFiles.loadImage("no-user-image.png"));
        this.logo.setImage(ConfigFiles.loadImage("img.jpg"));

        // Update content
        this.nombres.setText(GlobalController.getStaffUserModel().getName());

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
                        false
                );
                content.setContent(navFXML);
                Input.getScrollEvent(content);
                Fingerprint.VerifyBackgroundReader();
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

        this.navSecureMode.setOnMouseClicked(this.eventHandlerSecureMode());

        Platform.runLater(() -> {
//            Node summaryFXML = Loader.Load(
//                    "summary.fxml",
//                    "Dashboard",
//                    true
//            );
//            this.content.setContent(summaryFXML);
            new FadeInUp(GlobalController.getCurrentGymNode()).play();

            Node members = Loader.Load(
                    "members.fxml",
                    "Dashboard",
                    true
            );
            this.content.setContent(members);
        });
    }

    // event handlers
    private EventHandler<MouseEvent> eventHandlerSecureMode() {
        return mouseEvent -> {
            if (GlobalController.isSecureMode()) {
                // TODO: DON'T LOST FOCUS
                Dialog dialog = new Dialog(
                        Styles.WARN,
                        "Modo seguro",
                        "Ingresa tu contraseña para desactivar el modo seguro",
                        DialogTypes.PASSWORD,
                        Dialog.OK, Dialog.NO
                );
                if (dialog.show()) {
                    GlobalController.setSecureMode(false);
                }
            } else {
                Dialog dialog = new Dialog(
                        Styles.WARN,
                        "Modo seguro",
                        "El modo seguro bloquea la interfaz pero el sistema sigue funcionando, útil para ausentarse con seguridad",
                        DialogTypes.MESSAGE,
                        Dialog.OK, Dialog.NO
                );
                if (dialog.show()) {
                    GlobalController.setSecureMode(true);
                }
            }

            this.content.setDisable(GlobalController.isSecureMode());
        };
    }

}
