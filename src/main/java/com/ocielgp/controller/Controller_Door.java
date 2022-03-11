package com.ocielgp.controller;

import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;

public class Controller_Door {
    enum Task {
        EMPTY, // connection purpose
        WHITE, // led white
        GREEN, // led green and door open
        YELLOW, // led yellow and door open
        RED, // led red and door closes
        CLOSE // led turn off and closes door
    }

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final int WAITING_SECONDS = 4;
    private static ScheduledFuture<?> LAST_TASK;
    private static final String EDNPOINT = "http://192.168.1.200:80";

    public static void Start() {
        Controller_Door.createRequest(Task.EMPTY).thenAccept(stringHttpResponse -> {
            if (stringHttpResponse.statusCode() == 200) {
                System.out.println("[Arduino][Connected][" + stringHttpResponse.statusCode() + "]");
                Notifications.BuildNotification("gmi-meeting-room", "Puerta", "Puerta conectada", 3, Styles.EPIC);
            } else {
                System.out.println("[Arduino][Disconnected][" + stringHttpResponse + "]");
            }
        }).exceptionally(throwable -> {
            Notifications.BuildNotification("gmi-no-meeting-room", "Puerta", "La puerta no se ha podido conectar", 10, Styles.DANGER);
            System.out.println("[Arduino][Disconnected][" + throwable.getMessage() + "]");
            return null;
        });
    }

    private static void scheduleCloseDoor(int seconds) {
        if (Controller_Door.LAST_TASK != null && !Controller_Door.LAST_TASK.isDone()) {
            Controller_Door.LAST_TASK.cancel(true);
        }

        Controller_Door.LAST_TASK = Controller_Door.executorService.schedule(() -> {
            Controller_Door.createRequest(Task.CLOSE);
        }, seconds, TimeUnit.SECONDS);
    }

    public static void WHITE() {
        Controller_Door.createRequest(Task.WHITE);
    }

    public static void GREEN() {
        Controller_Door.createRequest(Task.GREEN);
        Controller_Door.scheduleCloseDoor(Controller_Door.WAITING_SECONDS);
    }

    public static void YELLOW() {
        Controller_Door.createRequest(Task.YELLOW);
        Controller_Door.scheduleCloseDoor(Controller_Door.WAITING_SECONDS);
    }

    public static void RED() {
        Controller_Door.createRequest(Task.RED);
        Controller_Door.scheduleCloseDoor(2);
    }

    private static CompletableFuture<HttpResponse<String>> createRequest(Task task) {
//        System.out.println(task);
        HttpRequest request = HttpRequest.newBuilder(URI.create(Controller_Door.EDNPOINT))
                .headers("Color", task.name())
                .PUT(HttpRequest.BodyPublishers.noBody())
//                .PUT(HttpRequest.BodyPublishers.ofString(task.name()))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
