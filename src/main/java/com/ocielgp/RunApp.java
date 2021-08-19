package com.ocielgp;

import com.ocielgp.app.GlobalController;
import com.ocielgp.controller.AppController;
import com.ocielgp.database.members.MODEL_MEMBERS;
import com.ocielgp.database.staff.MODEL_STAFF_MEMBERS;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.utilities.Loader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class RunApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.getIcons().setAll(ConfigFiles.getIconApp());
        // Load root parent
        AppController appController = new AppController();
        GlobalController.setAppController(appController);
        BorderPane appView = (BorderPane) Loader.Load(
                "app.fxml",
                "RunApp",
                false,
                appController
        );

        // Place content on scene
        Scene scene = new Scene(appView, 1366, 768); // HD
        scene.getStylesheets().add(String.valueOf(RunApp.class.getClassLoader().getResource("styles.css")));

        // Show app
        primaryStage.setTitle("Gym App");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Init notification system
        GlobalController.setPrimaryStage(primaryStage);

        // Kill all threads when an event closing occur
        primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, evt -> {
            Platform.exit();
            System.exit(0);
        });

        MODEL_STAFF_MEMBERS modelStaffMembers = new MODEL_STAFF_MEMBERS();
        modelStaffMembers.setPassword("a94cbdca65dd4582c45c2b8dd97aec782baa8fbad32b73b547bf5b0e52ef58f3");
        modelStaffMembers.setIdRole(2);
        MODEL_MEMBERS modelMembers = new MODEL_MEMBERS();
        modelMembers.setIdMember(2);
        modelMembers.setName("Ociel");
        modelMembers.setLastName("Garcia");
        modelMembers.setModelStaffMembers(modelStaffMembers);

        GlobalController.setStaffUserModel(modelMembers);
        Node dashboardFXML = Loader.Load(
                "dashboard.fxml",
                "Login",
                true
        );
        appView.setCenter(dashboardFXML);
    }
}
