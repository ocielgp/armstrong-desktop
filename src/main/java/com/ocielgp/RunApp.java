package com.ocielgp;

import com.ocielgp.app.AppController;
import com.ocielgp.controller.RootController;
import com.ocielgp.model.StaffUsersModel;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.NotificationHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;

public class RunApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load root parent
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(RunApp.class.getClassLoader().getResource("app.fxml"))
        );
        Parent root = view.load();

        // Place content on scene
        Scene scene = new Scene(root, 1366, 768); // HD

        // Add stylesheets
        scene.getStylesheets().add(String.valueOf(RunApp.class.getClassLoader().getResource("styles.css")));

        // Show app
        primaryStage.setTitle("Ármstrong");
        primaryStage.setScene(scene);
//        primaryStage.setMaximized(true);
        primaryStage.show();

        // Init notification system
        AppController.setPrimaryStage(primaryStage);
//        NotificationHandler.primaryStage = primaryStage;

        // Kill all threads when an event closing occur
        primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, evt -> {
            Platform.exit();
            System.exit(0);
        });


        StaffUsersModel staffUserModel = new StaffUsersModel();
        staffUserModel.setName("Ociel");
        staffUserModel.setIdStaffUser(2);
        AppController.setStaffUserModel(staffUserModel);
        Node dashboardFXML = Loader.Load(
                "dashboard.fxml",
                "Login",
                true
        );
        BorderPane pane = (BorderPane) root;
        pane.setCenter(dashboardFXML);
    }
}
