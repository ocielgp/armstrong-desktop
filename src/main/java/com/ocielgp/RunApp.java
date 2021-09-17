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
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

public class RunApp extends Application {
    private final HashMap<String, Image> appIcon = new HashMap<>();

    public static void main(String[] args) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        appIcon.put("focus", ConfigFiles.getIconApp());
        appIcon.put("unfocused", ConfigFiles.loadImage("app-icon-unfocused.png"));
        primaryStage.getIcons().setAll(ConfigFiles.getIconApp());
        AppController appController = new AppController();

        GlobalController.setPrimaryStage(primaryStage);
        GlobalController.setAppController(appController);

        BorderPane appView = (BorderPane) Loader.Load(
                "app.fxml",
                "RunApp",
                false,
                appController
        );

        // scene
        Scene scene = new Scene(appView, 1366, 768); // HD
        scene.getStylesheets().add(String.valueOf(RunApp.class.getClassLoader().getResource("styles.css")));

        // show app
        primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                primaryStage.getIcons().setAll(
                        (newValue) ? this.appIcon.get("focus") : this.appIcon.get("unfocused")
                );
            });
        });
        primaryStage.setTitle("Gym App");
        primaryStage.setScene(scene);
//        primaryStage.setAlwaysOnTop(true);
//        primaryStage.setMaximized(true);
        primaryStage.show();


        // kill all threads when a closing event occur
        primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, evt -> {
            Platform.exit();
            System.exit(0);
        });

        // TODO: FIX BUG WINDOWS IS BLANK
        MODEL_STAFF_MEMBERS modelStaffMembers = new MODEL_STAFF_MEMBERS();
        modelStaffMembers.setPassword("a94cbdca65dd4582c45c2b8dd97aec782baa8fbad32b73b547bf5b0e52ef58f3");
        modelStaffMembers.setIdRole(2);
        MODEL_MEMBERS modelMembers = new MODEL_MEMBERS();
        modelMembers.setIdMember(2);
        modelMembers.setName("Ociel");
        modelMembers.setLastName("Garcia");
        modelMembers.setModelStaffMembers(modelStaffMembers);

        GlobalController.setStaffUserModel(modelMembers);
        Node loginFXML = Loader.Load(
                "dashboard.fxml",
                "Login",
                true
        );
        appView.setCenter(loginFXML);
    }
}
