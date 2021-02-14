package com.ocielgp;

import com.ocielgp.controller.AppController;
import com.ocielgp.utilities.NotificationHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        // Load fxml initial
        FXMLLoader view = new FXMLLoader(
                Objects.requireNonNull(RunApp.class.getClassLoader().getResource("app.fxml"))
        );
        AppController appController = new AppController();
        view.setController(appController);
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
        NotificationHandler.primaryStage = primaryStage;

        // Kill all threads when an event closing occur
        primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, evt -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
