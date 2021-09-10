package com.ocielgp.controller;

import animatefx.animation.FadeInUp;
import com.ocielgp.app.GlobalController;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class DashboardController implements Initializable {
    @FXML
    private GridPane boxDashboard;
    @FXML
    private ScrollPane scrollPaneContent;

    @FXML
    private ImageView imageViewLogo;
    @FXML
    private Label labelSection;
    @FXML
    private ImageView imageViewUser;
    @FXML
    private Label labelStaffName;
    @FXML
    private FontIcon fontIconFingerprint;
    @FXML
    private Label labelFingerprintStatus;

    // Routes
    @FXML
    private HBox navSummary;
    @FXML
    private HBox navMembers;
    @FXML
    private HBox navSecureMode;

    // -> check in (ci)
    @FXML
    private HBox ci_box;
    @FXML
    private ImageView ci_imgPhoto;
    @FXML
    private Label ci_labelId;
    @FXML
    private Label ci_labelName;
    @FXML
    private Label ci_labelGym;
    @FXML
    private Label ci_labelMembership;

    // attributes
    private boolean boolRoutesEnabled = true;
    private HashMap<HBox, String> routes;

    public void enableRoutes() {
        if (!boolRoutesEnabled) {
            for (HBox nav : routes.keySet()) {
                nav.setDisable(false);
            }
            this.boolRoutesEnabled = true;
        }
    }

    private void disableRoutes() {
        Loading.show();
        for (HBox nav : routes.keySet()) {
            nav.setDisable(true);
        }
        this.boolRoutesEnabled = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GlobalController.setDashboardController(this);
        ConfigFiles.loadImage("no-user-image.png").thenAccept(image -> Platform.runLater(() -> this.ci_imgPhoto.setImage(image)));
        ConfigFiles.loadImage("no-user-image.png").thenAccept(image -> Platform.runLater(() -> this.imageViewUser.setImage(image)));
        ConfigFiles.loadImage("img.jpg").thenAccept(image -> Platform.runLater(() -> this.imageViewLogo.setImage(image)));

        // Update scrollPaneContent
        this.labelStaffName.setText(GlobalController.getStaffUserModel().getName());

        /* Routing */
        this.routes = new HashMap<>();
        routes.put(navSummary, "summary.fxml");
        routes.put(navMembers, "members.fxml");

        for (HBox navBox : routes.keySet()) {
            if (!navBox.getStyleClass().contains("selected")) {
                navBox.addEventFilter(MouseEvent.MOUSE_CLICKED, eventEventHandlerNav());
            }
        }
        /* End Routing */

        // Fingerprint
        Fingerprint.initializeUI(this.fontIconFingerprint, this.labelFingerprintStatus);

        this.navSecureMode.setOnMouseClicked(this.eventHandlerSecureMode());

        Platform.runLater(() -> {
            Node summaryFXML = Loader.Load(
                    "members.fxml",
                    "Dashboard",
                    true
            );
            this.scrollPaneContent.setContent(summaryFXML);
            new FadeInUp(GlobalController.getCurrentGymNode()).play();
        });
    }

    public void showUserInfo(Styles style, byte[] photo, String idMember, String name, String gym, String membership) {
        this.ci_box.getStyleClass().setAll(GlobalController.getThemeType(), Input.styleToColor(style));
        if (photo == null) {
            ConfigFiles.loadImage("no-user-image.png").thenAccept(image -> Platform.runLater(() -> this.ci_imgPhoto.setImage(image)));
        } else {
            ConfigFiles.loadImage(photo).thenAccept(image -> Platform.runLater(() -> this.ci_imgPhoto.setImage(image)));
        }

        this.ci_labelId.setText(idMember);
        this.ci_labelName.setText(name);
        this.ci_labelGym.setText(gym);
        this.ci_labelMembership.setText(membership);
    }

    // event handlers
    private EventHandler<MouseEvent> eventEventHandlerNav() {
        return new EventHandler<>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                disableRoutes();
                CompletableFuture.runAsync(() -> {
                    for (HBox navBox : routes.keySet()) {
                        if (navBox.getStyleClass().contains("selected")) {
                            navBox.getStyleClass().remove("selected");
                            navBox.addEventFilter(MouseEvent.MOUSE_CLICKED, this);
                            break;
                        }
                    }

                    HBox navBox = (HBox) mouseEvent.getSource();
                    navBox.getStyleClass().add("selected");
                    navBox.removeEventFilter(MouseEvent.MOUSE_CLICKED, this);
                    Node navFXML = Loader.Load(
                            routes.get(navBox),
                            "Dashboard",
                            false
                    );
                    Platform.runLater(() -> {
                        scrollPaneContent.setContent(navFXML);
                        Input.getScrollEvent(scrollPaneContent);
                        Fingerprint.BackgroundReader();
                    });
                });
            }
        };
    }

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

            this.scrollPaneContent.setDisable(GlobalController.isSecureMode());
        };
    }

}
