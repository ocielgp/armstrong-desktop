package com.ocielgp.app;

import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.utilities.InputProperties;
import com.ocielgp.utilities.Loader;
import com.ocielgp.utilities.Loading;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class Router {
    // routes
    public static final String SUMMARY = "summary.fxml";
    public static final String MEMBERS = "members.fxml";

    private static HashMap<HBox, Pair<String, String>> routes = new HashMap<>();
    private static final AtomicReference<HBox> previousNav = new AtomicReference<>();
    private static ScrollPane appDashboard;
    private static Label labelSection;

    public static boolean isRouterAvailable = true;

    public static void InitRouter(String initialRoute, Label section, ScrollPane appDashboard, HashMap<HBox, Pair<String, String>> dashboardRoutes) {
        CompletableFuture.runAsync(() -> {
            routes = dashboardRoutes;
            labelSection = section;
            Router.appDashboard = appDashboard;
            for (HBox navBox : routes.keySet()) {
                isRouterAvailable = false;
                navBox.setOnMouseClicked(mouseEvent -> changeContent(navBox));
                navBox.setDisable(false);
                if (routes.get(navBox).getKey().equals(initialRoute)) {
                    Platform.runLater(() -> navBox.getStyleClass().add("selected"));
                    previousNav.set(navBox);
                    Node initialView = Loader.Load(
                            routes.get(navBox).getKey(),
                            "Router",
                            false
                    );
                    Platform.runLater(() -> {
                        Loading.show();
                        Router.appDashboard.setContent(initialView);
                        Loading.isAnimationFinished.set(true);
                    });
                    navBox.setDisable(true);
                }
            }
        });
    }

    private static void changeContent(HBox currentNavOption) {
        if (isRouterAvailable) {
            CompletableFuture.runAsync(() -> {
                Loading.show();
                isRouterAvailable = false;
                Platform.runLater(() -> {
                    previousNav.get().getStyleClass().remove("selected");
                    previousNav.get().setDisable(false);
                    previousNav.set(currentNavOption);
                    currentNavOption.getStyleClass().add("selected");
                    currentNavOption.setDisable(true);
                });
                currentNavOption.setDisable(true);
                Node page = Loader.Load(
                        routes.get(currentNavOption).getKey(),
                        "Router",
                        false
                );
                Platform.runLater(() -> {
                    labelSection.setText(routes.get(currentNavOption).getValue());
                    appDashboard.setContent(page);
                    InputProperties.getScrollEvent(appDashboard);
                    Fingerprint_Controller.BackgroundReader();
                    Loading.isAnimationFinished.set(true);
                });
            });
        }
    }

    public static void enableDashboard() {
        Router.isRouterAvailable = true;
        Router.appDashboard.setDisable(false);
    }

    public static void disableDashboard() {
        Router.isRouterAvailable = false;
        Router.appDashboard.setDisable(true);
    }
}
