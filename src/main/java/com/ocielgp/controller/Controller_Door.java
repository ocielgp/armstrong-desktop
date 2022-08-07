package com.ocielgp.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class Controller_Door {
    enum Task {
        WHITE, // led white
        GREEN, // led green and door open
        YELLOW, // led yellow and door open
        RED, // led red and door closes
        OFF // led turn off and closes door
    }

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final int WAITING_SECONDS = 4;
    private static ScheduledFuture<?> LAST_TASK;
    private static Boolean isDoorConnected;
    private static SerialPort serialPort;

    public static void Start() {
        Controller_Door.serialPort = SerialPort.getCommPort("COM3");
        Controller_Door.serialPort.openPort();
        Controller_Door.isDoorConnected = Controller_Door.serialPort.isOpen();
        if (Controller_Door.isDoorConnected) {
            Notifications.BuildNotification("gmi-meeting-room", "Puerta", "Puerta conectada", 3, Styles.EPIC);
        } else {
            Notifications.BuildNotification("gmi-no-meeting-room", "Puerta", "La puerta no se ha podido conectar", 10, Styles.DANGER);
        }
    }

    private static void scheduleCloseDoor(int seconds) {
        if (Controller_Door.LAST_TASK != null && !Controller_Door.LAST_TASK.isDone()) {
            Controller_Door.LAST_TASK.cancel(true);
        }

        Controller_Door.LAST_TASK = Controller_Door.executorService.schedule(() -> Controller_Door.createRequest(Task.OFF), seconds, TimeUnit.SECONDS);
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

    public static void OFF() {
        Controller_Door.createRequest(Task.OFF);
    }

    private static void createRequest(Task task) {
        if (Controller_Door.isDoorConnected) {
            serialPort.writeBytes(task.name().getBytes(StandardCharsets.UTF_8), task.name().length());
            System.out.println(task.name());
        }
    }
}
