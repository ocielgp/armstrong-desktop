package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.AppController;
import com.ocielgp.controller.DashboardController;
import com.ocielgp.database.members.MODEL_MEMBERS;
import com.ocielgp.database.system.MODEL_GYMS;
import com.ocielgp.utilities.Styles;
import javafx.stage.Stage;

import java.util.Locale;

public class GlobalController {
    public static AppController appController;
    private static MODEL_MEMBERS staffUserModel;
    private static Stage primaryStage;
    public static DashboardController dashboardController;
    private static boolean secureMode = false;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        GlobalController.primaryStage = primaryStage;
    }

    static {
        Locale.setDefault(new Locale("es", "MX"));
    }

    public static void setAppController(AppController appController) {
        GlobalController.appController = appController;
    }

    public static MODEL_MEMBERS getStaffUserModel() {
        return staffUserModel;
    }

    public static void setStaffUserModel(MODEL_MEMBERS staffUserModel) {
        GlobalController.staffUserModel = staffUserModel;
    }

    /* Content methods */
    public static String getThemeType() {
        return appController.getTheme();
    }

    public static JFXComboBox<MODEL_GYMS> getCurrentGymNode() {
        return appController.getGymNode();
    }

    public static MODEL_GYMS getCurrentGym() {
        return appController.getGym();
    }

    public static DashboardController getDashboardController() {
        return dashboardController;
    }

    public static void setDashboardController(DashboardController dashboardController) {
        GlobalController.dashboardController = dashboardController;
    }

    public static void showUserInfo(Styles style, byte[] photo, String idMember, String name, String gym, String membership) {
        dashboardController.showUserInfo(style, photo, idMember, name, gym, membership);
    }

    public static boolean isSecureMode() {
        return secureMode;
    }

    public static void setSecureMode(boolean secureMode) {
        GlobalController.secureMode = secureMode;
    }
}
