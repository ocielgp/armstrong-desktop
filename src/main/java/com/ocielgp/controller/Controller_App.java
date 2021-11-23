package com.ocielgp.controller;

import animatefx.animation.FadeInUp;
import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.dao.JDBC_Gym;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.models.Model_Gym;
import com.ocielgp.models.Model_Member_Photo;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Styles;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_App implements Initializable {
    @FXML
    public BorderPane borderPaneRoot;

    @FXML
    private JFXComboBox<Model_Gym> comboBoxGyms;
    @FXML
    private Label labelAbout;
    @FXML
    private FontIcon iconTheme;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.borderPaneRoot.getStyleClass().add(UserPreferences.GetPreferenceString("THEME"));
        Application.SetAppController(this, comboBoxGyms);

        this.comboBoxGyms.setDisable(true);
        this.comboBoxGyms.setFocusTraversable(false);

        this.labelAbout.setOnMouseClicked(mouseEvent -> about());

        this.iconTheme.setOnMouseClicked(mouseEvent -> eventChangeTheme());

        // recover last gym if exists
        readLastGym();

        Model_Admin modelAdmin = new Model_Admin();
        modelAdmin.setModelMemberPhoto(new Model_Member_Photo());
        modelAdmin.setPassword("a94cbdca65dd4582c45c2b8dd97aec782baa8fbad32b73b547bf5b0e52ef58f3");
        modelAdmin.setIdRole(Short.valueOf("1"));
        modelAdmin.setIdMember(2);
        modelAdmin.setName("Ociel");
        modelAdmin.setLastName("Garcia");
        Application.SetModelAdmin(modelAdmin);

        Platform.runLater(() -> {
            Node loginView = Loader.Load(
                    "login.fxml",
                    "Controller_App",
                    false
            );
            borderPaneRoot.setCenter(loginView);
            new FadeInUp(loginView).play();
        });
    }

    private void readLastGym() {
        JDBC_Gym.ReadGyms().thenAccept(model_gym -> {
            comboBoxGyms.setItems(model_gym);
            int previousIdGym = UserPreferences.GetPreferenceInt("LAST_GYM");
            for (Model_Gym gym : model_gym) {
                if (previousIdGym == gym.getIdGym()) {
                    Platform.runLater(() -> this.comboBoxGyms.getSelectionModel().select(gym));
                    break;
                }
            }
            this.comboBoxGyms.valueProperty().addListener((observable, oldValue, newValue) -> UserPreferences.SetPreference("LAST_GYM", Application.GetCurrentGym().getIdGym()));
            this.comboBoxGyms.focusedProperty().addListener((observableValue, oldValue, newValue) -> comboBoxGyms.getStyleClass().remove("red-border-input-line"));
        });
    }

    private void about() {
        Popup popup = new Popup();
        popup.alert(Styles.EPIC, "Versión " + Application.version, "Hecho con ♥ por ocielgp.com");
        popup.showAndWait();
    }

    private void eventChangeTheme() {
        Platform.runLater(() -> {
            String newTheme = (UserPreferences.GetPreferenceString("THEME").equals("day-theme")) ? "night-theme" : "day-theme";
            System.out.println(newTheme);
            this.borderPaneRoot.getStyleClass().set(1, newTheme);
            UserPreferences.SetPreference("THEME", newTheme);
        });
    }

}