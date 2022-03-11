package com.ocielgp.controller;

import animatefx.animation.FadeInUp;
import animatefx.animation.Shake;
import animatefx.animation.ZoomIn;
import com.ocielgp.app.Application;
import com.ocielgp.app.Router;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.utilities.FileLoader;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Styles;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller_Dashboard implements Initializable {
    @FXML
    private GridPane boxRoot;
    @FXML
    private ScrollPane body;

    @FXML
    private ImageView imageViewIcon;
    @FXML
    private Label labelSection;

    @FXML
    private HBox boxLogout;
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
    private HBox navCheckIn;
    @FXML
    private HBox navAdmins;
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
        Application.SetDashboardController(this);
        this.ci_imgPhoto.setImage(FileLoader.loadImage("no-user-image.png"));
        this.imageViewUser.setImage(FileLoader.loadImage(Application.GetModelAdmin().getModelMemberPhoto().getPhoto()));
        this.imageViewIcon.setImage(FileLoader.loadImage("app-icon.png"));

        this.boxLogout.setOnMouseClicked(mouseEvent -> {
            Popup popup = new Popup();
            popup.confirm(Styles.WARN, "Cerrar sesión", "¿Estás seguro que deseas salir?");
            if (popup.showAndWait()) {
                Router.EnableDashboard();
                this.body.setEffect(null);
                Application.isSecureMode = false;
                Application.GetCurrentGymNode().setDisable(true);
                Node loginView = Loader.Load(
                        "login.fxml",
                        "Controller_App",
                        false
                );
                Application.SetModelAdmin(null);
                Application.controllerApp.borderPaneRoot.setCenter(loginView);
                new FadeInUp(loginView).play();
            }
        });

        // update scrollPaneContent
        this.labelStaffName.setText(Application.GetModelAdmin().getName());

        /* Routing */
        HashMap<HBox, Pair<String, String>> routes = new HashMap<>();
        routes.put(this.navSummary, new Pair<>(Router.SUMMARY, "Resumen"));
        routes.put(this.navMembers, new Pair<>(Router.MEMBERS, "Socios"));
        routes.put(this.navCheckIn, new Pair<>(Router.CHECK_IN, "Entradas"));
        routes.put(this.navAdmins, new Pair<>(Router.ADMINS, "Gerencia"));
        Router.InitRouter(
                Router.SUMMARY,
                labelSection,
                body,
                routes
        );
        /* End Routing */

        // Fingerprint_Controller
        Fingerprint_Controller.Start(this.fontIconFingerprint, this.labelFingerprintStatus);

        this.navSecureMode.setOnMouseClicked(mouseEvent -> secureMode());

        Platform.runLater(() -> Application.GetCurrentGymNode().setDisable(false));
    }

    public void shakeUserInfo() {
        Platform.runLater(() -> new Shake(this.ci_box).play());
    }

    public void showUserInfo(String style, Image photo, Integer idMember, String name, String gym, String membership) {
        Platform.runLater(() -> {
            this.ci_box.getStyleClass().setAll(UserPreferences.GetPreferenceString("THEME"), style);
            this.ci_imgPhoto.setImage(photo);
            this.ci_labelId.setText(idMember.toString());
            this.ci_labelName.setText(name);
            this.ci_labelGym.setText(gym);
            this.ci_labelMembership.setText(membership);
            new ZoomIn(this.ci_box).play();
        });
    }

    private void secureMode() {
        Popup popup = new Popup();
        if (Application.isSecureMode) {
            popup.password();
            if (popup.showAndWait()) {
                Router.EnableDashboard();
                this.body.setEffect(null);
                Application.isSecureMode = false;
            }
        } else {
            popup.confirm(
                    Styles.WARN,
                    "Modo Seguro",
                    "Bloquea la interfaz pero el sistema sigue funcionando"
            );
            if (popup.showAndWait()) {
                Router.DisableDashboard();
                this.body.setEffect(new GaussianBlur());
                Application.isSecureMode = true;
            }
        }
    }
}
