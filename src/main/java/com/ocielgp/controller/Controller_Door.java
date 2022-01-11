package com.ocielgp.controller;

import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Controller_Door {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String endpoint = "http://localhost:3000";

    public static void Start() {
        Controller_Door.createRequest("GREEN").thenAccept(stringHttpResponse -> {
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

    public static void Busy() {
    }

    public static void GREEN() {
        Controller_Door.createRequest("GREEN");
    }

    public static void YELLOW() {
        Controller_Door.createRequest("YELLOW");
    }

    public static void RED() {
        Controller_Door.createRequest("RED");
    }

    private static CompletableFuture<HttpResponse<String>> createRequest(String status) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(Controller_Door.endpoint))
                .headers("Content-Type", "text/plain; charset=UTF-8")
                .PUT(HttpRequest.BodyPublishers.ofString(status))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
