package com.ocielgp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeInUp;
import com.ocielgp.app.Application;
import com.ocielgp.app.Router;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Application.setDashboardController(this);
        this.ci_imgPhoto.setImage(FileLoader.loadImage("no-user-image.png"));
        this.imageViewUser.setImage(FileLoader.loadImage("no-user-image.png"));
        this.imageViewLogo.setImage(FileLoader.loadImage("img.jpg"));

        // update scrollPaneContent
        this.labelStaffName.setText(Application.getStaffUserModel().getName());

        /* Routing */
        HashMap<HBox, Pair<String, String>> routes = new HashMap<>();
        routes.put(navSummary, new Pair<>(Router.SUMMARY, "Resumen"));
        routes.put(navMembers, new Pair<>(Router.MEMBERS, "Socios"));
        Router.initRouter(
                Router.MEMBERS, // TODO: change to summary route
                labelSection,
                scrollPaneContent,
                routes
        );
        /* End Routing */

        // Fingerprint_Controller
        Fingerprint_Controller.initializeUI(this.fontIconFingerprint, this.labelFingerprintStatus);

        this.navSecureMode.setOnMouseClicked(mouseEvent -> eventSecureMode());

        Platform.runLater(() -> {
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


    private void eventSecureMode() {
        if (Application.isSecureMode()) {
            Controller_Popup popup = new Controller_Popup();
            popup.fillView(
                    Styles.WARN,
                    "Desbloquear modo seguro",
                    "Ingresa tu contraseña para desbloquear",
                    Controller_Popup.POPUP_SECURE_MODE
            );
            if (popup.showAndWait()) {
                Application.setSecureMode(false);
            }
        } else {
            Controller_Popup popup = new Controller_Popup();
            popup.fillView(
                    Styles.WARN,
                    "Modo Seguro",
                    "Bloquea la interfaz pero el sistema sigue funcionando",
                    Controller_Popup.POPUP_CONFIRM
            );
            if (popup.showAndWait()) {
                Application.setSecureMode(true);
            }
        }
        Router.isRouterAvailable = !Application.isSecureMode();
        this.scrollPaneContent.setEffect((Application.isSecureMode()) ? new GaussianBlur() : null);
        this.scrollPaneContent.setDisable(Application.isSecureMode());
    }

}
