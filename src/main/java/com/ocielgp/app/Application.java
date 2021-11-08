package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.Controller_App;
import com.ocielgp.controller.Controller_Dashboard;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.models.Model_Gym;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.TimeZone;

public class Application {
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
                UserPreferences.getPreferenceString("LANGUAGE"),
                UserPreferences.getPreferenceString("COUNTRY")
        ));
        TimeZone.setDefault(TimeZone.getTimeZone(
                UserPreferences.getPreferenceString("TIMEZONE")
        ));
    }

    public static void RequestFocus() {
        if (STAGE_POPUP != null) {
            STAGE_POPUP.requestFocus();
        } else if (STAGE_SECONDARY != null) {
            STAGE_SECONDARY.requestFocus();
        } else if (STAGE_PRIMARY != null) {
            STAGE_PRIMARY.requestFocus();
        }
    }

    public static void setAppController(Controller_App controllerApp, JFXComboBox<Model_Gym> comboBoxGymsNode) {
        Application.controllerApp = controllerApp;
        comboBoxGyms = comboBoxGymsNode;
    }

    public static Model_Admin getModelAdmin() {
        return modelAdmin;
    }

    public static void setModelAdmin(Model_Admin modelAdmin) {
        Application.modelAdmin = modelAdmin;
    }

    /* Content methods */

    public static JFXComboBox<Model_Gym> getCurrentGymNode() {
        return comboBoxGyms;
    }

    public static Model_Gym getCurrentGym() {
        return comboBoxGyms.getValue();
    }

    public static Controller_Dashboard getDashboardController() {
        return controllerDashboard;
    }

    public static void setDashboardController(Controller_Dashboard controllerDashboard) {
        Application.controllerDashboard = controllerDashboard;
    }

    public static void showUserInfo(String style, byte[] photo, int idMember, String name, String gym, String membership) {
        controllerDashboard.showUserInfo(style, photo, idMember, name, gym, membership);
    }

    public static void EnableDashboard() {
        if (controllerDashboard != null) {
            Router.enableDashboard();
        }
    }

    public static void DisableDashboard() {
        if (controllerDashboard != null) {
            Router.disableDashboard();
        }
    }
}
