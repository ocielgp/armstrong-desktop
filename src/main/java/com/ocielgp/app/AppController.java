package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.DashboardController;
import com.ocielgp.controller.RootController;
import com.ocielgp.database.models.GymsModel;
import com.ocielgp.database.models.StaffUsersModel;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.Locale;

public class AppController {
    // Styles
    public static final String DEFAULT_STYLE = "default-style";
    public static final String SUCCESS_STYLE = "success-style";
    public static final String WARN_STYLE = "warn-style";
    public static final String DANGER_STYLE = "danger-style";
    public static final String CREATIVE_STYLE = "creative-style";
    public static final String EPIC_STYLE = "epic-style";


    private static RootController rootController;
    private static StaffUsersModel staffUserModel;
    private static Stage primaryStage;
    private static DashboardController dashboardController;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage primaryStage) {
        AppController.primaryStage = primaryStage;
    }

    static {
        Locale.setDefault(new Locale("es", "MX"));
    }

    public static void startController(RootController controller) {
        rootController = controller;
    }

    public static void setStaffUserModel(StaffUsersModel model) {
        if (staffUserModel == null) {
            staffUserModel = model;
        }
    }

    public static StaffUsersModel getStaffUserModel() {
        return staffUserModel;
    }

    /* Content methods */
    public static String getThemeType() {
        return rootController.getThemeType();
    }

    public static void setCenter(Node node) {
        rootController.setCenterContent(node);
    }

    public static JFXComboBox<GymsModel> getCurrentGymNode() {
        return rootController.getGymNode();
    }

    public static GymsModel getCurrentGym() {
        return rootController.getGym();
    }

    public static DashboardController getDashboardController() {
        return dashboardController;
    }

    public static void setDashboardController(DashboardController dashboardController) {
        AppController.dashboardController = dashboardController;
    }

    public static void showUserInfo(String style, byte[] photo, String idMember, String name, String gym, String membership) {
        dashboardController.showUserInfo(style, photo, idMember, name, gym, membership);
    }
}
