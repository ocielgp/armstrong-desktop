package com.ocielgp.utilities;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.ocielgp.RunApp;
import com.ocielgp.app.AppController;
import com.ocielgp.app.NotificationController;
import com.ocielgp.model.NotificationModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class NotificationHandler {
    // Containers
    private static final Stage stage;

    // Attributes
    private static final LinkedList<GridPane> notificationContainers = new LinkedList<>();
    private static final LinkedList<NotificationModel> notifications = new LinkedList<>();
    private static final int margin = 20;
    private static Timeline threadHandler;
    private static FadeInRight fadeInRight;

    // Styles
    public static final String DEFAULT_STYLE = "default-style";
    public static final String SUCESS_STYLE = "sucess-style";
    public static final String WARN_STYLE = "warn-style";
    public static final String DANGER_STYLE = "danger-style";
    public static final String EPIC_STYLE = "epic-style";

    static {
        // Init notifications container
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true); // Fix notification, always on front
    }

    public static void createNotification(String icon, String title, String content, int seconds, String style) {
        NotificationModel notification = new NotificationModel(
                icon,
                title,
                content,
                seconds,
                style
        );
        FXMLLoader template = new FXMLLoader(
                Objects.requireNonNull(NotificationHandler.class.getClassLoader().getResource("notification.fxml"))
        );
        NotificationController controller = new NotificationController(notification);
        template.setController(controller);
        GridPane gridPane = null;
        try {
            gridPane = template.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridPane finalGridPane = gridPane;
        gridPane.addEventHandler(EventType.ROOT, new EventHandler<>() {
            @Override
            public void handle(Event event) {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                    if (fadeInRight != null) {
                        fadeInRight.stop();
                        fadeInRight = null;
                    }
                    threadHandler.stop();
                    hiddenNotification();
                    finalGridPane.removeEventHandler(EventType.ROOT, this);
                }
            }
        });
        notificationContainers.add(gridPane);
        notifications.add(notification);
        if (notificationContainers.size() == 1) {
            threadHandler = new Timeline(scheduleNotification(notifications.getFirst().getTime()));
            threadHandler.play(); // Run thread
        }
    }

    public static KeyFrame scheduleNotification(int seconds) {
        // Show notification
        GridPane notificationUI = notificationContainers.getFirst();
        NotificationModel notificationController = notifications.getFirst();
        showNotification(notificationUI, notificationController);

        return new KeyFrame(Duration.seconds(seconds), evt -> hiddenNotification());
    }

    private static void showNotification(GridPane notificationUI, NotificationModel notificationController) {
        Scene scene = new Scene(notificationUI);
        scene.getStylesheets().add(String.valueOf(RunApp.class.getClassLoader().getResource("notifications.css")));

        stage.setTitle(notificationController.getTitle());
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        notificationUI.setOpacity(0);
        stage.show(); // Calculate UI pixels

        if (AppController.getPrimaryStage() != null) {
            AppController.getPrimaryStage().requestFocus();
        }

        // Set new position to Stage
        stage.setX(
                Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth() - margin
        );
        stage.setY(
                Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight() - margin
        );
        fadeInRight = new FadeInRight(notificationUI);
        fadeInRight.play();
    }

    public static void hiddenNotification() {
        notificationContainers.getFirst().setDisable(true);
        FadeOutRight fadeOutRight = new FadeOutRight(notificationContainers.getFirst());
        fadeOutRight.setOnFinished(actionEvent -> {
            notificationContainers.pop();
            notifications.pop();
            stage.hide();

            if (!notificationContainers.isEmpty()) {
                threadHandler = new Timeline(scheduleNotification(notifications.getFirst().getTime()));
                threadHandler.play(); // Run thread
            }
        });
        fadeOutRight.play();
    }

    public static void notify(String icon, String title, String content, int seconds) {
        NotificationHandler.createNotification(
                icon,
                title,
                content,
                seconds,
                NotificationHandler.DEFAULT_STYLE
        );
    }

    public static void sucess(String title, String content, int seconds) {
        NotificationHandler.createNotification(
                "gmi-check",
                title,
                content,
                seconds,
                NotificationHandler.SUCESS_STYLE
        );
    }

    public static void warn(String title, String content, int seconds) {
        NotificationHandler.createNotification(
                "gmi-priority-high",
                title,
                content,
                seconds,
                NotificationHandler.WARN_STYLE
        );
    }

    public static void danger(String title, String content, int seconds) {
        NotificationHandler.createNotification(
                "gmi-close",
                title,
                content,
                seconds,
                NotificationHandler.DANGER_STYLE
        );
    }

    public static void catchError(String className, StackTraceElement exceptionMetaData, String body, Exception exception, String... icon) {
        NotificationHandler.createNotification(
                (icon.length > 0) ? icon[0] : "gmi-sync-problem",
                className,
                "[" + exceptionMetaData.getMethodName() + " : " + exceptionMetaData.getLineNumber() + " line]\n" + body,
                20,
                NotificationHandler.DANGER_STYLE
        );
        exception.printStackTrace();
    }

}
