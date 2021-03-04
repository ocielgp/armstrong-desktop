package com.ocielgp.app;

import com.ocielgp.controller.RootController;
import com.ocielgp.model.StaffUsersModel;
import javafx.scene.Node;

import java.util.Locale;

public class AppController {
    private static RootController rootController;
    private static StaffUsersModel staffUserModel;

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
}
