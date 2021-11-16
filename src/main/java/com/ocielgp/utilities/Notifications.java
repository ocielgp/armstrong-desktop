package com.ocielgp.utilities;

import animatefx.animation.FadeInRight;
import animatefx.animation.FadeOutRight;
import animatefx.animation.ZoomOutRight;
import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

class NotificationView implements Initializable {
    @FXML
    private GridPane gridPaneNotification;
    @FXML
    private FontIcon fontIcon;
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelContent;

    // attributes
    private final String icon;
    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty content = new SimpleStringProperty();
    private final String style;

    public NotificationView(String fontIcon, String title, String content, String style) {
        this.icon = fontIcon;
        this.title.set(title);
        this.content.set(content);
        this.style = style;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.gridPaneNotification.getStyleClass().addAll(UserPreferences.GetPreferenceString("THEME"), style);
        this.fontIcon.setIconLiteral(icon);
        this.labelTitle.textProperty().bind(title);
        this.labelContent.textProperty().bind(content);
    }
}

public class Notifications {
    private static final Stage stage = new Stage(StageStyle.TRANSPARENT);
    private static final Scene scene = new Scene(new GridPane());
    private static final LinkedList<GridPane> notificationViews = new LinkedList<>();
    private static final LinkedList<Double> notificationSeconds = new LinkedList<>();
    private static final int margin = 20;
    private static Timer timer = new Timer("Notification Thread");
    private static FadeInRight fadeInRightNotification;
    private static final double DEFAULT_SECONDS = 3;

    public static void Start() {
        stage.initOwner(Application.STAGE_PRIMARY);
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Application.RequestFocus();
            }
        });
        scene.getStylesheets().add("notifications.css");
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
    }

    synchronized public static void BuildNotification(String icon, String title, String content, double seconds, String style) {
        CompletableFuture.runAsync(() -> {
            NotificationView notificationController = new NotificationView(icon, title, content, style);
            GridPane notificationView = (GridPane) Loader.Load("notification.fxml", "Notifications", false, notificationController);
            scene.setOnMouseClicked(mouseEvent -> {
                if (!scene.getRoot().isDisable()) {
                    timer.cancel();
                    timer = new Timer("Notification Thread");
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        if (fadeInRightNotification != null) {
                            Platform.runLater(() -> {
                                scene.getRoot().setDisable(true);
                                fadeInRightNotification.stop();
                                fadeInRightNotification = null;
                            });
                        }
                        HiddenNotification();
                    } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                        if (fadeInRightNotification != null) {
                            Platform.runLater(() -> {
                                scene.getRoot().setDisable(true);
                                fadeInRightNotification.stop();
                                fadeInRightNotification = null;
                            });
                        }
                        ClearAllNotifications();
                    }
                }
            });
            notificationViews.add(notificationView);
            notificationSeconds.add(seconds);
            if (notificationViews.size() == 1 && !stage.isShowing()) {
                ShowNotification();
            }
        });
    }

    synchronized private static void ShowNotification() {
        CompletableFuture.runAsync(() -> {
            GridPane notificationView = notificationViews.getFirst();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    HiddenNotification();
                }
            }, notificationSeconds.getFirst().longValue() * 1000);
            Platform.runLater(() -> {
                scene.setRoot(notificationView);
                stage.show();
                stage.setX(
                        Screen.getPrimary().getVisualBounds().getWidth() - stage.getWidth() - margin
                );
                stage.setY(
                        Screen.getPrimary().getVisualBounds().getHeight() - stage.getHeight() - margin
                );
                fadeInRightNotification = new FadeInRight(notificationView);
                fadeInRightNotification.play();
            });
        });
    }

    synchronized public static void HiddenNotification() {
        CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> scene.getRoot().setDisable(true));
            notificationViews.pop();
            notificationSeconds.pop();
            FadeOutRight fadeOutRightNotification = new FadeOutRight(scene.getRoot());
            fadeOutRightNotification.setOnFinished(actionEvent -> {
                Platform.runLater(stage::close);

                if (!notificationViews.isEmpty()) {
                    ShowNotification();
                }
            });
            Platform.runLater(fadeOutRightNotification::play);
        });
    }

    private static void ClearAllNotifications() {
        CompletableFuture.runAsync(() -> {
            notificationViews.clear();
            notificationSeconds.clear();
            ZoomOutRight zoomOutRightNotification = new ZoomOutRight(scene.getRoot());
            zoomOutRightNotification.setOnFinished(actionEvent -> Platform.runLater(stage::close));
            Platform.runLater(zoomOutRightNotification::play);
        });
    }

    public static void Default(String icon, String title, String content, double seconds) {
        Notifications.BuildNotification(icon, title, content, seconds, Styles.DEFAULT);
    }

    public static void Default(String icon, String title, String content) {
        Notifications.BuildNotification(icon, title, content, DEFAULT_SECONDS, Styles.DEFAULT);
    }

    public static void Success(String icon, String title, String content, double seconds) {
        Notifications.BuildNotification(icon, title, content, seconds, Styles.SUCCESS);
    }

    public static void Success(String title, String content, double seconds) {
        Notifications.BuildNotification("gmi-check", title, content + ".", seconds, Styles.SUCCESS);
    }

    public static void Success(String title, String content) {
        Notifications.BuildNotification("gmi-check", title, content, DEFAULT_SECONDS, Styles.SUCCESS);
    }

    public static void Warn(String icon, String title, String content, double seconds) {
        Notifications.BuildNotification(icon, title, content, seconds, Styles.WARN);
    }

    public static void Warn(String title, String content, double seconds) {
        Notifications.BuildNotification("gmi-priority-high", title, content, seconds, Styles.WARN);
    }

    public static void Warn(String title, String content) {
        Notifications.BuildNotification("gmi-priority-high", title, content, DEFAULT_SECONDS, Styles.WARN);
    }

    public static void Danger(String icon, String title, String content, double seconds) {
        Notifications.BuildNotification(icon, title, content, seconds, Styles.DANGER);
    }

    public static void Danger(String title, String content, double seconds) {
        Notifications.BuildNotification("gmi-close", title, content, seconds, Styles.DANGER);
    }

    public static void Danger(String title, String content) {
        Notifications.BuildNotification("gmi-close", title, content, DEFAULT_SECONDS, Styles.DANGER);
    }

    public static void CatchException(String className, StackTraceElement exceptionMetaData, Exception exception) {
        Notifications.BuildNotification(
                "gmi-sync-problem",
                className,
                "[" + exceptionMetaData.getMethodName() + " : " + exceptionMetaData.getLineNumber() + " line]\n" + exception.getMessage(),
                20,
                Styles.DANGER
        );
        exception.printStackTrace();
        Loading.closeNow();
    }

    public static void CatchSqlException(String className, StackTraceElement exceptionMetaData, SQLException sqlException) {
        Notifications.BuildNotification(
                "gmi-sync-problem",
                className,
                "[" + exceptionMetaData.getMethodName() + " : " + exceptionMetaData.getLineNumber() + " line]\n" + "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                20,
                Styles.DANGER
        );
        sqlException.printStackTrace();
        Loading.closeNow();
    }

}
