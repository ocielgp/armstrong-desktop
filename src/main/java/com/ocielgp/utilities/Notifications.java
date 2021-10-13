package com.ocielgp.utilities;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

class NotificationView implements Initializable {
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
    private final double seconds;
    @FXML
    private GridPane gridPane;
    private final String style;

    public NotificationView(String icon, String title, String content, double seconds, String style) {
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
        this.gridPane.getStyleClass().addAll(UserPreferences.getPreferenceString("THEME"), this.style);
    }

    public String getTitle() {
        return title;
    }

    public double getSeconds() {
        return seconds;
    }

}

public class Notifications {
    private static final Stage stage = new Stage(StageStyle.TRANSPARENT);
    private static final LinkedList<GridPane> notificationViews = new LinkedList<>();
    private static final LinkedList<NotificationView> notificationControllers = new LinkedList<>();
    private static final int margin = 20;
    private static Timeline threadHandler;
    private static FadeInRight fadeInRight;

    static {
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
    }

    public static void initializeNotificationSystem() {
        stage.initOwner(Application.getPrimaryStage());
    }

    private static final double SECONDS = 3;

    public static void createNotification(String icon, String title, String content, double seconds, String style) {
        NotificationView notificationController = new NotificationView(icon, title, content, seconds, style);
        GridPane notificationView = (GridPane) Loader.Load("notification.fxml", "Notifications", false, notificationController);
        notificationView.addEventHandler(EventType.ROOT, new EventHandler<>() {
            @Override
            public void handle(Event event) {
                if (event.getEventType() == MouseEvent.MOUSE_CLICKED && ((MouseEvent) event).getButton() == MouseButton.PRIMARY) {
                    if (fadeInRight != null) {
                        fadeInRight.stop();
                        fadeInRight = null;
                    }
                    threadHandler.stop();
                    hiddenNotification(false);
                    notificationView.removeEventHandler(EventType.ROOT, this);
                } else if (event.getEventType() == MouseEvent.MOUSE_CLICKED && ((MouseEvent) event).getButton() == MouseButton.SECONDARY) {
                    if (fadeInRight != null) {
                        fadeInRight.stop();
                        fadeInRight = null;
                    }
                    threadHandler.stop();
                    hiddenNotification(true);
                    notificationView.removeEventHandler(EventType.ROOT, this);
                }
            }
        });
        notificationViews.add(notificationView);
        notificationControllers.add(notificationController);
        if (notificationViews.size() == 1) {
            // Run thread
            Platform.runLater(() -> {
                threadHandler = new Timeline(scheduleNotification(Notifications.notificationControllers.getFirst().getSeconds()));
                threadHandler.play();
            });
        }
    }

    private static void showNotification(GridPane notificationView) {
        Scene scene = new Scene(notificationView);
        scene.getStylesheets().add(String.valueOf(Notifications.class.getClassLoader().getResource("notifications.css")));
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        Platform.runLater(() -> {
            stage.show();
            Application.getPrimaryStage().requestFocus();

            // Set new position to Stage
            stage.setX(
                    Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth() - margin
            );
            stage.setY(
                    Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight() - margin
            );
            fadeInRight = new FadeInRight(notificationView);
            fadeInRight.play();
        });

    }

    public static void hiddenNotification(boolean clearAll) {
        notificationViews.getFirst().setDisable(true);
        FadeOutRight fadeOutRight = new FadeOutRight(notificationViews.getFirst());
        fadeOutRight.setOnFinished(actionEvent -> {
            notificationViews.pop();
            notificationControllers.pop();
            stage.hide();

            if (clearAll) {
                notificationViews.clear();
                notificationControllers.clear();
            }

            if (!notificationViews.isEmpty()) {
                threadHandler = new Timeline(scheduleNotification(notificationControllers.getFirst().getSeconds()));
                threadHandler.play(); // Run thread
            }
        });
        fadeOutRight.play();
    }

    public static KeyFrame scheduleNotification(double seconds) {
        GridPane notificationView = notificationViews.getFirst();
        showNotification(notificationView);
        return new KeyFrame(Duration.seconds(seconds), evt -> hiddenNotification(false));
    }

    public static void buildNotification(String icon, String title, String content, double seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.DEFAULT);
    }

    public static void buildNotification(String icon, String title, String content) {
        Notifications.createNotification(icon, title, content, SECONDS, Styles.DEFAULT);
    }

    public static void success(String icon, String title, String content, double seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.SUCCESS);
    }

    public static void success(String title, String content, double seconds) {
        Notifications.createNotification("gmi-check", title, content + ".", seconds, Styles.SUCCESS);
    }

    public static void success(String title, String content) {
        Notifications.createNotification("gmi-check", title, content, SECONDS, Styles.SUCCESS);
    }

    public static void warn(String icon, String title, String content, double seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.WARN);
    }

    public static void warn(String title, String content, double seconds) {
        Notifications.createNotification("gmi-priority-high", title, content, seconds, Styles.WARN);
    }

    public static void warn(String title, String content) {
        Notifications.createNotification("gmi-priority-high", title, content, SECONDS, Styles.WARN);
    }

    public static void danger(String icon, String title, String content, double seconds) {
        Notifications.createNotification(icon, title, content, seconds, Styles.DANGER);
    }

    public static void danger(String title, String content, double seconds) {
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
