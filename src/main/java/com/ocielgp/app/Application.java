package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.Controller_App;
import com.ocielgp.controller.Controller_Dashboard;
import com.ocielgp.models.Model_Gym;
import com.ocielgp.models.Model_Member;
import com.ocielgp.utilities.Styles;
import javafx.stage.Stage;

import java.util.Locale;

public class Application {
    public static Controller_App controllerApp;
    public static Controller_Dashboard controllerDashboard;
    private static Model_Member staffUserModel;
    private static Stage primaryStage;
    private static boolean secureMode = false;

    static {
        Locale.setDefault(new Locale("es", "MX"));
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        Application.primaryStage = primaryStage;
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
    public static String getThemeType() {
        return controllerApp.getTheme();
    }

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

    public static boolean isSecureMode() {
        return secureMode;
    }

    public static void setSecureMode(boolean secureMode) {
        Application.secureMode = secureMode;
    }
}
