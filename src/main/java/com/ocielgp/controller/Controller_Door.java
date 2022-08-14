package com.ocielgp.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

public class Controller_Door {
    enum Task {
        GREEN,
        YELLOW,
        RED,
        PURPLE
    }

    private static Boolean isDoorConnected;
    private static SerialPort serialPort;

    public static void Start() {
        for (SerialPort port : SerialPort.getCommPorts()) {
//            System.out.println(port.getDescriptivePortName());
            if (port.getDescriptivePortName().contains("CH340")) {
                Controller_Door.serialPort = port;
                break;
            }
        }

        if (Controller_Door.serialPort == null) {
            Controller_Door.isDoorConnected = false;
        } else {
            Controller_Door.serialPort.openPort();
            Controller_Door.isDoorConnected = Controller_Door.serialPort.isOpen();
        }

        if (Controller_Door.isDoorConnected) {
            System.out.println("[" + Controller_Door.serialPort.getDescriptivePortName() + "]: Connected");
            Notifications.BuildNotification("gmi-meeting-room", "Puerta", "Puerta conectada", 3, Styles.EPIC);
        } else {
            Notifications.BuildNotification("gmi-no-meeting-room", "Puerta", "La puerta no se ha podido conectar", 10, Styles.DANGER);
        }
    }

    public static void GREEN() {
        Controller_Door.createRequest(Task.GREEN);
    }

    public static void YELLOW() {
        Controller_Door.createRequest(Task.YELLOW);
    }

    public static void RED() {
        Controller_Door.createRequest(Task.RED);
    }

    public static void PURPLE() {
        Controller_Door.createRequest(Task.PURPLE);
    }

    private static void createRequest(Task task) {
        if (Controller_Door.isDoorConnected) {
            serialPort.writeBytes(task.name().getBytes(), task.name().length());
//            System.out.println(task.name());
        }
    }
}
