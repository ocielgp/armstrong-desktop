package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.RootController;
import com.ocielgp.model.GymsModel;
import com.ocielgp.model.StaffUsersModel;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.Locale;

public class AppController {
    private static RootController rootController;
    private static StaffUsersModel staffUserModel;
    private static Stage primaryStage;

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
}
