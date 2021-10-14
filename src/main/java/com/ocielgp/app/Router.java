package com.ocielgp.app;

import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Loader;
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
    private static AtomicReference<HBox> previousNav = new AtomicReference<>();
    private static ScrollPane scrollPaneDashboard;
    private static Label labelSection;

    public static boolean isRouterAvailable = true;

    public static void initRouter(String initialRoute, Label section, ScrollPane boxContent, HashMap<HBox, Pair<String, String>> dashboardRoutes) {
        CompletableFuture.runAsync(() -> {
            routes = dashboardRoutes;
            labelSection = section;
            scrollPaneDashboard = boxContent;
            for (HBox navBox : routes.keySet()) {
                isRouterAvailable = false;
                navBox.setOnMouseClicked(mouseEvent -> eventChangeContent(navBox));
                navBox.setDisable(false);
                if (routes.get(navBox).getKey().equals(initialRoute)) {
                    Platform.runLater(() -> navBox.getStyleClass().add("selected"));
                    previousNav.set(navBox);
                    Node initialPage = Loader.Load(
                            routes.get(navBox).getKey(),
                            "Router",
                            false
                    );
                    Platform.runLater(() -> {
                        scrollPaneDashboard.setContent(initialPage);
                    });
                    navBox.setDisable(true);
                }
                isRouterAvailable = true;
            }
        });
    }

    private static void eventChangeContent(HBox currentNavOption) {
        if (isRouterAvailable) {
            CompletableFuture.runAsync(() -> {
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
                        "Dashboard",
                        false
                );
                Platform.runLater(() -> {
                    labelSection.setText(routes.get(currentNavOption).getValue());
                    scrollPaneDashboard.setContent(page);
                    Input.getScrollEvent(scrollPaneDashboard);
                    Fingerprint_Controller.BackgroundReader();
                    isRouterAvailable = true; // TODO: test if is working
                });
            });
        }
    }
}
