package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.Controller_App;
import com.ocielgp.controller.Controller_Dashboard;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.models.Model_Gym;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.TimeZone;

public class Application {
    public static final String version = "1.0";
    public static Controller_App controllerApp;
    public static Controller_Dashboard controllerDashboard;
    public static Stage STAGE_PRIMARY;
    public static Stage STAGE_SECONDARY;
    public static Stage STAGE_POPUP;
    public static boolean isSecureMode = false;
    private static Model_Admin modelAdmin;
    private static JFXComboBox<Model_Gym> comboBoxGyms;

    static {
        Locale.setDefault(new Locale(
                UserPreferences.GetPreferenceString("LANGUAGE"),
                UserPreferences.GetPreferenceString("COUNTRY")
        ));
        TimeZone.setDefault(TimeZone.getTimeZone(
                UserPreferences.GetPreferenceString("TIMEZONE")
        ));
    }

    public static void SetAppController(Controller_App controllerApp, JFXComboBox<Model_Gym> comboBoxGymsNode) {
        Application.controllerApp = controllerApp;
        Application.comboBoxGyms = comboBoxGymsNode;
    }

    public static void SetDashboardController(Controller_Dashboard controllerDashboard) {
        Application.controllerDashboard = controllerDashboard;
    }

    public static void SetModelAdmin(Model_Admin modelAdmin) {
        Application.modelAdmin = modelAdmin;
    }

    public static Model_Admin GetModelAdmin() {
        return Application.modelAdmin;
    }

    public static void RequestFocus() {
        if (Application.STAGE_POPUP != null && Application.STAGE_POPUP.getScene() != null) {
            Platform.runLater(() -> {
                if (Application.STAGE_POPUP != null) {
                    Application.STAGE_POPUP.requestFocus();
                }
            });
        } else if (Application.STAGE_SECONDARY != null && Application.STAGE_SECONDARY.getScene() != null) {
            Platform.runLater(() -> {
                if (Application.STAGE_SECONDARY != null) {
                    Application.STAGE_SECONDARY.requestFocus();
                }
            });
        } else if (Application.STAGE_PRIMARY != null && Application.STAGE_PRIMARY.getScene() != null) {
            Platform.runLater(() -> {
                if (Application.STAGE_PRIMARY != null) {
                    Application.STAGE_PRIMARY.requestFocus();
                }
            });
        }
    }

    public static JFXComboBox<Model_Gym> GetCurrentGymNode() {
        return Application.comboBoxGyms;
    }

    public static Model_Gym GetCurrentGym() {
        return Application.comboBoxGyms.getValue();
    }

    public static void ShowUserInfo(String style, Image photo, int idMember, String name, String gym, String membership) {
        if (Application.controllerDashboard != null)
            Application.controllerDashboard.showUserInfo(style, photo, idMember, name, gym, membership);
    }
}
