package com.ocielgp.app;

import com.jfoenix.controls.JFXComboBox;
import com.ocielgp.controller.RootController;
import com.ocielgp.model.GymsModel;
import com.ocielgp.model.StaffUsersModel;
import com.ocielgp.utilities.NotificationHandler;
import com.ocielgp.utilities.Pagination;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

public class AppController {
    private static RootController rootController;
    private static StaffUsersModel staffUserModel;
    private static Stage primaryStage;
    private static Pagination pagination;

    public static void setPagination(Pagination pagination) {
        AppController.pagination = pagination;
    }

    public static void refreshTable() {
        if (pagination != null) {
            pagination.loadData(1);
        }
    }

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

    public static void saveProperty(String file, String property, String value) {
        InputStream inputStream = AppController.class.getClassLoader().getResourceAsStream(file);
        Properties properties = new Properties();
        if (inputStream == null) {
            try {
                NotificationHandler.danger("Error", "[AppController][saveProperty]: " + file + " no encontrado", 5);
                throw new FileNotFoundException("[AppController][saveProperty]: " + file + " no encontrado");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                properties.load(inputStream);
                properties.setProperty(property, value);
                properties.store(new FileOutputStream(
                                Objects.requireNonNull(
                                        AppController.class.getClassLoader().getResource(file)
                                ).getPath()
                        ),
                        null
                );
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readProperty(String file, String property) {
        InputStream inputStream = AppController.class.getClassLoader().getResourceAsStream(file);
        Properties properties = new Properties();
        if (inputStream == null) {
            try {
                NotificationHandler.danger("Error", "[AppController][readProperty][file]: " + file + " no encontrado.", 5);
                throw new FileNotFoundException("[AppController][readProperty][file]: " + file + " no encontrado");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                properties.load(inputStream);
                String propertyString = properties.getProperty(property);
                inputStream.close();
                if (propertyString == null) {
                    NotificationHandler.danger("Error", "[AppController][readProperty][property]: " + property + " no encontrado.", 5);
                    throw new FileNotFoundException("[AppController][readProperty][property]: " + property + " no encontrado");
                } else {
                    return propertyString;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
