package com.ocielgp.utilities;

import com.jfoenix.controls.JFXSpinner;
import com.ocielgp.app.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Loading {
    private static final Stage stage = new Stage(StageStyle.TRANSPARENT);
    private static final int spinnerSize = 100;

    static {
        JFXSpinner spinner = new JFXSpinner();
        spinner.setPrefWidth(spinnerSize);
        spinner.setPrefHeight(spinnerSize);
        VBox boxSpinner = new VBox(
                spinner
        );
        boxSpinner.setPadding(new Insets(5, 5, 5, 5));
        boxSpinner.setBackground(Background.EMPTY);
        Scene scene = new Scene(boxSpinner);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.NONE);
        stage.initOwner(Application.STAGE_PRIMARY);
        stage.showingProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (newValue) {
                Application.DisableDashboard();
            } else {
                Application.EnableDashboard();
            }
        }));
    }

    public static void show() {
        if (!stage.isShowing()) {
            Platform.runLater(() -> {
                stage.show();
                stage.setX(Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2);
                stage.setY(Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2);
            });
        }
    }

    public static void close() {
        Platform.runLater(stage::close);
    }
}
