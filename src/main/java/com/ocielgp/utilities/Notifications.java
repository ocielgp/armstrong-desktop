package com.ocielgp.utilities;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.ocielgp.RunApp;
import com.ocielgp.app.AppController;
import com.ocielgp.files.ConfigFiles;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Objects;
import java.util.ResourceBundle;

class NotificationModel {
    private String icon;
    private String title;
    private String content;
    private int time;
    private String style;

    public NotificationModel(String icon, String title, String content, int time, String style) {
        this.icon = icon;
        this.title = title;
        this.content = content;
        this.time = time;
        this.style = style;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}

class NotificationView implements Initializable {
    // Attributes
    private final NotificationModel notification;
    // Containers
    @FXML
    private GridPane container;
    // Controls
    @FXML
    private FontIcon icon;
    @FXML
    private Label title;
    @FXML
    private Label content;

    public NotificationView(NotificationModel notification) {
        this.notification = notification;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.icon.setIconLiteral(this.notification.getIcon());
        this.title.setText(this.notification.getTitle());
        this.content.setText(this.notification.getContent());

        // Add styles
        this.container.getStyleClass().addAll(AppController.getThemeType(), this.notification.getStyle());
    }
}

public class Notifications {

    // Containers
    private static final Stage stage;

    // Attributes
    private static final LinkedList<GridPane> notificationContainers = new LinkedList<>();
    private static final LinkedList<NotificationModel> notifications = new LinkedList<>();
    private static final int margin = 20;
    private static Timeline threadHandler;
    private static FadeInRight fadeInRight;

    static {
        // Init notifications container
        stage = new Stage();
        stage.getIcons().setAll(ConfigFiles.loadImage("app-icon.png"));
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
                Objects.requireNonNull(Notifications.class.getClassLoader().getResource("notification.fxml"))
        );
        NotificationView controller = new NotificationView(notification);
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
        Notifications.createNotification(
                icon,
                title,
                content,
                seconds,
                AppController.DEFAULT_STYLE
        );
    }

    public static void success(String title, String content, int seconds) {
        Notifications.createNotification(
                "gmi-check",
                title,
                content,
                seconds,
                AppController.SUCCESS_STYLE
        );
    }

    public static void success(String icon, String title, String content, int seconds) {
        Notifications.createNotification(
                icon,
                title,
                content,
                seconds,
                AppController.SUCCESS_STYLE
        );
    }

    public static void warn(String title, String content, int seconds) {
        Notifications.createNotification(
                "gmi-priority-high",
                title,
                content,
                seconds,
                AppController.WARN_STYLE
        );
    }

    public static void warn(String icon, String title, String content, int seconds) {
        Notifications.createNotification(
                icon,
                title,
                content,
                seconds,
                AppController.WARN_STYLE
        );
    }

    public static void danger(String title, String content, int seconds) {
        Notifications.createNotification(
                "gmi-close",
                title,
                content,
                seconds,
                AppController.DANGER_STYLE
        );
    }

    public static void danger(String icon, String title, String content, int seconds) {
        Notifications.createNotification(
                icon,
                title,
                content,
                seconds,
                AppController.DANGER_STYLE
        );
    }

    public static void catchError(String className, StackTraceElement exceptionMetaData, String body, Exception exception, String... icon) {
        Notifications.createNotification(
                (icon.length > 0) ? icon[0] : "gmi-sync-problem",
                className,
                "[" + exceptionMetaData.getMethodName() + " : " + exceptionMetaData.getLineNumber() + " line]\n" + body,
                20,
                AppController.DANGER_STYLE
        );
        exception.printStackTrace();
    }

}
