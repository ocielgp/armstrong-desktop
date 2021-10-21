package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.Controller_App;
import com.ocielgp.controller.Controller_Dashboard;
import com.ocielgp.models.Model_Gym;
import com.ocielgp.models.Model_Member;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.TimeZone;

public class Application {
    public static Controller_App controllerApp;
    public static Controller_Dashboard controllerDashboard;
    private static Model_Member staffUserModel;
    public static Stage STAGE_PRIMARY;
    public static Stage STAGE_SECONDARY;
    public static Stage STAGE_POPUP;

    static {
        Locale.setDefault(
                new Locale(
                        UserPreferences.getPreferenceString("LANGUAGE"),
                        UserPreferences.getPreferenceString("COUNTRY")
                )
        );
        TimeZone.setDefault(
                TimeZone.getTimeZone(
                        UserPreferences.getPreferenceString("TIMEZONE")
                )
        );
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

    public static void setAppController(Controller_App controllerApp) {
        Application.controllerApp = controllerApp;
    }

    public static Model_Member getStaffUserModel() {
        return staffUserModel;
    }

    public static void setStaffUserModel(Model_Member staffUserModel) {
        Application.staffUserModel = staffUserModel;
    }

    /* Content methods */

    public static JFXComboBox<Model_Gym> getCurrentGymNode() {
        return controllerApp.getGymNode();
    }

    public static Model_Gym getCurrentGym() {
        return controllerApp.getGym();
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
            controllerDashboard.eventEnableDashboard();
        }
    }

    public static void DisableDashboard() {
        if (controllerDashboard != null) {
            controllerDashboard.eventDisableDashboard();
        }
    }
}
