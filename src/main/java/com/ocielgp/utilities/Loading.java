package com.ocielgp.utilities;

import animatefx.animation.*;
import com.ocielgp.controller.AppController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

public class Loading {
    private static final Stage stage;
    private static final Scene scene;
    private static final VBox box;
    private static final FontIcon fontIcon;
    private static ZoomIn zoomIn;
    private static final Tada tada;
    private static final double iconSize = 100;


    static {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);

        fontIcon = new FontIcon("gmi-cloud-circle");
        fontIcon.setIconSize((int) iconSize);
        tada = new Tada(fontIcon);
        box = new VBox(fontIcon);
        box.setStyle("-fx-background-color: transparent");
        box.getStyleClass().add(AppController.themeType);
        box.setOpacity(0); // Hide

        scene = new Scene(box);
        scene.getStylesheets().add("colors.css");
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        stage.setX(
                (Screen.getPrimary().getBounds().getWidth() / 2) - (iconSize / 2)
        );
        stage.setY(
                (Screen.getPrimary().getBounds().getHeight() / 2) - (iconSize / 2)
        );
        stage.setAlwaysOnTop(true);
    }

    public static void startLoad() {
        if (zoomIn == null) {
            box.getStyleClass().set(1, AppController.themeType);
            stage.show();

            zoomIn = new ZoomIn(box);
            zoomIn.setOnFinished(actionEvent -> {
                tada.setCycleCount(AnimationFX.INDEFINITE);
                tada.play();
            });
            zoomIn.play();
        }
    }

    public static void stopLoad() {
        if (zoomIn != null) {
            tada.stop();
            ZoomOut zoomOut = new ZoomOut(box);
            zoomOut.setOnFinished(actionEvent -> stage.hide());
            zoomIn = null;
            zoomOut.play();
        }
    }

    public static void stopLoad(AnimationFX animationFX) {
        if (zoomIn != null) {
            tada.stop();
            ZoomOut zoomOut = new ZoomOut(box);
            zoomOut.setOnFinished(actionEvent -> {
                stage.hide();
                animationFX.play();
            });
            zoomIn = null;
            zoomOut.play();
        }
    }

}
