package com.ocielgp.utilities;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.ocielgp.RunApp;
import com.ocielgp.app.GlobalController;
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
import javafx.scene.input.MouseButton;
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

class NotificationView implements Initializable {
    // Containers
    @FXML
    private GridPane container;

    // Controls
    @FXML
    private FontIcon fontIcon;
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelContent;

    // Attributes
    private final String icon;
    private final String title;
    private final String content;
    private int seconds;
    private final Styles style;

    public NotificationView(String icon, String title, String content, int seconds, Styles style) {
        this.icon = icon;
        this.title = title;
        this.content = content;
        this.seconds = seconds;
        this.style = style;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.fontIcon.setIconLiteral(this.icon);
        this.labelTitle.setText(this.title);
        this.labelContent.setText(this.content);

        // Add styles
        this.container.getStyleClass().addAll(GlobalController.getThemeType(), Input.styleToColor(this.style));
    }

    public String getTitle() {
        return title;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }
}

public class Notifications {

    // Containers
    private static final Stage stage;

    // Attributes
    private static final LinkedList<GridPane> notificationContainers = new LinkedList<>();
    private static final LinkedList<NotificationView> notifications = new LinkedList<>();
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

    public static void createNotification(String icon, String title, String content, int seconds, Styles style) {
        FXMLLoader template = new FXMLLoader(
                Objects.requireNonNull(Notifications.class.getClassLoader().getResource("notification.fxml"))
        );
        NotificationView notification = new NotificationView(
                icon,
                title,
                content,
                seconds,
                style
        );
        template.setController(notification);
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
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED && ((MouseEvent) event).getButton() == MouseButton.PRIMARY) {
                    if (fadeInRight != null) {
                        fadeInRight.stop();
                        fadeInRight = null;
                    }
                    threadHandler.stop();
                    hiddenNotification(false);
                    finalGridPane.removeEventHandler(EventType.ROOT, this);
                } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED && ((MouseEvent) event).getButton() == MouseButton.SECONDARY) {
                    if (fadeInRight != null) {
                        fadeInRight.stop();
                        fadeInRight = null;
                    }
                    threadHandler.stop();
                    hiddenNotification(true);
                    finalGridPane.removeEventHandler(EventType.ROOT, this);
                }
            }
        });
        notificationContainers.add(gridPane);
        notifications.add(notification);
        if (notificationContainers.size() == 1) {
            threadHandler = new Timeline(scheduleNotification(notifications.getFirst().getSeconds()));
            threadHandler.play(); // Run thread
        }
    }

    public static KeyFrame scheduleNotification(int seconds) {
        // Show notification
        GridPane notificationUI = notificationContainers.getFirst();
        NotificationView notificationView = notifications.getFirst();
        showNotification(notificationUI, notificationView);

        return new KeyFrame(Duration.seconds(seconds), evt -> hiddenNotification(false));
    }

    private static void showNotification(GridPane notificationUI, NotificationView notificationController) {
        Scene scene = new Scene(notificationUI);
        scene.getStylesheets().add(String.valueOf(RunApp.class.getClassLoader().getResource("notifications.css")));

        stage.setTitle(notificationController.getTitle());
        stage.setScene(scene);
        scene.setFill(Color.TRANSPARENT);
        notificationUI.setOpacity(0);
        stage.show(); // Calculate UI pixels

        if (GlobalController.getPrimaryStage() != null) {
            GlobalController.getPrimaryStage().requestFocus();
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

    public static void hiddenNotification(boolean clearAll) {
        notificationContainers.getFirst().setDisable(true);
        FadeOutRight fadeOutRight = new FadeOutRight(notificationContainers.getFirst());
        fadeOutRight.setOnFinished(actionEvent -> {
            notificationContainers.pop();
            notifications.pop();
            stage.hide();

            if (clearAll) {
                notificationContainers.clear();
                notifications.clear();
            }

            if (!notificationContainers.isEmpty()) {
                threadHandler = new Timeline(scheduleNotification(notifications.getFirst().getSeconds()));
                threadHandler.play(); // Run thread
            }
        });
        fadeOutRight.play();
    }

    private static final int SECONDS = 3;

    public static void buildNotification(String icon, String title, String content, int seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.DEFAULT);
    }

    public static void buildNotification(String icon, String title, String content) {
        Notifications.createNotification(icon, title, content, SECONDS, Styles.DEFAULT);
    }

    public static void success(String icon, String title, String content, int seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.SUCCESS);
    }

    public static void success(String title, String content, int seconds) {
        Notifications.createNotification("gmi-check", title, content, seconds, Styles.SUCCESS);
    }

    public static void success(String title, String content) {
        Notifications.createNotification("gmi-check", title, content, SECONDS, Styles.SUCCESS);
    }

    public static void warn(String icon, String title, String content, int seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.WARN);
    }

    public static void warn(String title, String content, int seconds) {
        Notifications.createNotification("gmi-priority-high", title, content, seconds, Styles.WARN);
    }

    public static void warn(String title, String content) {
        Notifications.createNotification("gmi-priority-high", title, content, SECONDS, Styles.WARN);
    }

    public static void danger(String icon, String title, String content, int seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.DANGER);
    }

    public static void danger(String title, String content, int seconds) {
        Notifications.createNotification("gmi-close", title, content, seconds, Styles.DANGER);
    }

    public static void danger(String title, String content) {
        Notifications.createNotification("gmi-close", title, content, SECONDS, Styles.DANGER);
    }

    public static void catchError(String className, StackTraceElement exceptionMetaData, String body, Exception exception) {
        Notifications.createNotification(
                "gmi-sync-problem",
                className,
                "[" + exceptionMetaData.getMethodName() + " : " + exceptionMetaData.getLineNumber() + " line]\n" + body,
                20,
                Styles.DANGER
        );
        exception.printStackTrace();
    }

    public static void catchError(String className, StackTraceElement exceptionMetaData, String body, Exception exception, String icon) {
        Notifications.createNotification(
                icon,
                className,
                "[" + exceptionMetaData.getMethodName() + " : " + exceptionMetaData.getLineNumber() + " line]\n" + body,
                20,
                Styles.DANGER
        );
        exception.printStackTrace();
    }

}
