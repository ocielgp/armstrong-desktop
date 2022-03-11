package com.ocielgp.utilities;

import com.jfoenix.controls.JFXSpinner;
import com.ocielgp.app.Application;
import com.ocielgp.app.Router;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    public static BooleanProperty isAnimationFinished = new SimpleBooleanProperty(false);
    public static BooleanProperty isChildLoaded = new SimpleBooleanProperty(false);

    public static void Start() {
        Platform.runLater(() -> {
            isAnimationFinished.addListener((observableValue, oldValue, newValue) -> Loading.close());
            isChildLoaded.addListener((observableValue, oldValue, newValue) -> Loading.close());

            buildSpinner();

            stage.initModality(Modality.NONE);
            stage.initOwner(Application.STAGE_PRIMARY);
            stage.showingProperty().addListener(((observableValue, oldValue, newValue) -> {
                if (newValue) {
                    Platform.runLater(() -> {
                        stage.setX(Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2);
                        stage.setY(Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2);
                    });
                    Router.DisableDashboard();
                } else {
                    if (!Application.isSecureMode) Router.EnableDashboard();
                    Loading.isAnimationFinished.set(false);
                    Loading.isChildLoaded.set(false);
                }
            }));
        });
    }

    public static void show() {
        Platform.runLater(() -> {
            if (!stage.isShowing()) {
                stage.show();
            }
        });
    }

    public static void close() {
//        System.out.println("close, isAnimationfinished: "+Loading.isAnimationFinished.get()+" childLoaded: "+isChildLoaded);
        Platform.runLater(() -> {
            if (stage.isShowing() && Loading.isAnimationFinished.get() && Loading.isChildLoaded.get()) {
                stage.close();
            }
        });
    }

    public static void closeNow() {
        Platform.runLater(() -> {
            if (stage.isShowing()) {
                stage.close();
            }
        });
    }

    private static void buildSpinner() {
        Platform.runLater(() -> {
            JFXSpinner spinner = new JFXSpinner();
            spinner.setPrefWidth(spinnerSize);
            spinner.setPrefHeight(spinnerSize);
            VBox boxSpinner = new VBox(spinner);
            boxSpinner.setPadding(new Insets(5, 5, 5, 5));
            boxSpinner.setBackground(Background.EMPTY);

            Scene scene = new Scene(boxSpinner);
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
        });
    }
}
