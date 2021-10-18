package com.ocielgp;

import com.ocielgp.app.Application;
import com.ocielgp.controller.Controller_App;
import com.ocielgp.models.Model_Member;
import com.ocielgp.models.Model_Staff_Member;
import com.ocielgp.utilities.FileLoader;
import com.ocielgp.utilities.Loader;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.HashMap;

public class RunApp extends javafx.application.Application {
    private final HashMap<String, Image> appIcon = new HashMap<>();

    public static void main(String[] args) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        appIcon.put("focus", FileLoader.getIconApp());
        appIcon.put("unfocused", FileLoader.loadImage("app-icon-unfocused.png"));
        primaryStage.getIcons().setAll(FileLoader.getIconApp());
        Controller_App controllerApp = new Controller_App();

        Application.STAGE_PRIMARY = primaryStage;
        Application.setAppController(controllerApp);

        BorderPane appView = (BorderPane) Loader.Load(
                "app.fxml",
                "RunApp",
                false,
                controllerApp
        );

        // scene
        Scene scene = new Scene(appView, 1280, 720); // HD
        scene.getStylesheets().add("styles.css");

        // show app
        primaryStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                primaryStage.getIcons().setAll(
                        (newValue) ? this.appIcon.get("focus") : this.appIcon.get("unfocused")
                );
            });
        });
        primaryStage.setTitle("Gym App by Ociel");
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
        Model_Staff_Member modelStaffMember = new Model_Staff_Member();
        modelStaffMember.setPassword("a94cbdca65dd4582c45c2b8dd97aec782baa8fbad32b73b547bf5b0e52ef58f3");
        modelStaffMember.setIdRole(2);
        Model_Member modelMembers = new Model_Member();
        modelMembers.setIdMember(2);
        modelMembers.setName("Ociel");
        modelMembers.setLastName("Garcia");
        modelMembers.setModelStaffMember(modelStaffMember);

        Application.setStaffUserModel(modelMembers);
        Node loginFXML = Loader.Load(
                "dashboard.fxml",
                "Login",
                true
        );
        appView.setCenter(loginFXML);
    }
}
