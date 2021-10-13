package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.utilities.*;
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
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class Controller_Dashboard implements Initializable {
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

    // routes
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

    // variables
    private boolean boolRoutesEnabled = true;
    private HashMap<HBox, Pair<String, String>> routes;
    private final String initialRoute;


    public Controller_Dashboard() {
        this.initialRoute = "summary.fxml"; // default route
    }

    public Controller_Dashboard(String initialRoute) {
        this.initialRoute = initialRoute;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Application.setDashboardController(this);
        this.ci_imgPhoto.setImage(FileLoader.loadImage("no-user-image.png"));
        this.imageViewUser.setImage(FileLoader.loadImage("no-user-image.png"));
        this.imageViewLogo.setImage(FileLoader.loadImage("img.jpg"));

        // update scrollPaneContent
        this.labelStaffName.setText(Application.getStaffUserModel().getName());

        /* Routing */
        this.routes = new HashMap<>();
        routes.put(navSummary, new Pair<>("summary.fxml", "Resumen"));
        routes.put(navMembers, new Pair<>("members.fxml", "Socios"));

        for (HBox navBox : routes.keySet()) {
            navBox.setOnMouseClicked(mouseEvent -> eventChangeContent(navBox));
            if (initialRoute.)
            if (initialRoute.isEmpty()) {
                navSummary.getStyleClass().add("selected");
                navSummary.setDisable(false);
            } else {
                if (routes.get(navBox).getKey().equals(initialRoute)) {
                    navBox.getStyleClass().add("selected");
                    navSummary.setDisable(false);
                }
            }
        }
        /* End Routing */

        // Fingerprint_Controller
        Fingerprint_Controller.initializeUI(this.fontIconFingerprint, this.labelFingerprintStatus);

        this.navSecureMode.setOnMouseClicked(this.eventHandlerSecureMode());

        Platform.runLater(() -> {
            Node summaryFXML = Loader.Load(
                    "members.fxml",
                    "Dashboard",
                    true
            );
            this.scrollPaneContent.setContent(summaryFXML);
            new FadeInUp(Application.getCurrentGymNode()).play();
        });
    }

    public void showUserInfo(String style, byte[] photo, Integer idMember, String name, String gym, String membership) {
        Image loadImage;
        if (photo == null) {
            loadImage = FileLoader.getDefaultImage();
        } else {
            loadImage = FileLoader.loadImage(photo);
        }

        Platform.runLater(() -> {
            this.ci_box.getStyleClass().setAll(UserPreferences.getPreferenceString("THEME"), style);
            this.ci_imgPhoto.setImage(loadImage);
            this.ci_labelId.setText(idMember.toString());
            this.ci_labelName.setText(name);
            this.ci_labelGym.setText(gym);
            this.ci_labelMembership.setText(membership);
            new FadeIn(this.ci_box).play();
        });
    }

    // event handlers
    private void eventChangeContent(HBox navBox) {
        navBox.getStyleClass().add("selected");


        navBox.setDisable(true);
        if (navBox.getStyleClass().contains("selected")) {
            System.out.println("salio");
        }
        Node navFXML = Loader.Load(
                null,
                "Dashboard",
                false
        );
        Platform.runLater(() -> {
            scrollPaneContent.setContent(navFXML);
            Input.getScrollEvent(scrollPaneContent);
            Fingerprint_Controller.BackgroundReader();
        });
    }

    private EventHandler<MouseEvent> eventHandlerSecureMode() {
        return mouseEvent -> {
            if (Application.isSecureMode()) {
                // TODO: DON'T LOST FOCUS
                Dialog dialog = new Dialog(
                        Styles.WARN,
                        "Modo seguro",
                        "Ingresa tu contraseña para desactivar el modo seguro",
                        DialogTypes.PASSWORD,
                        Dialog.OK, Dialog.NO
                );
                if (dialog.show()) {
                    Application.setSecureMode(false);
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
                    Application.setSecureMode(true);
                }
            }

            this.scrollPaneContent.setDisable(Application.isSecureMode());
        };
    }

}
