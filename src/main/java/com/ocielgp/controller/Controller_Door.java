package com.ocielgp.controller;

import com.fazecast.jSerialComm.SerialPort;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;
import javafx.application.Platform;

public class Controller_Door {
    private static SerialPort serialPort;

    public static void Start() {
        for (SerialPort availablePort : SerialPort.getCommPorts()) {
            if (availablePort.getDescriptivePortName().contains("CH340")) {
                Controller_Door.serialPort = availablePort;
                Controller_Door.serialPort.setBaudRate(9600);
                break;
            }
        }

        try {
            if (Controller_Door.serialPort.openPort()) {
                System.out.println("[Arduino][Connected]");
                Notifications.BuildNotification("gmi-meeting-room", "Puerta", "Puerta conectada", 3, Styles.EPIC);
            }
        } catch (Exception exception) {
            Notifications.BuildNotification("gmi-no-meeting-room", "Puerta", "La puerta no se ha podido conectar\n" + exception.getMessage(), 10, Styles.DANGER);
            System.out.println("[Arduino][Disconnected]");
            Controller_Door.serialPort = null;
        }

    }

    public static void Busy() {
        if (Controller_Door.serialPort != null) {
            Controller_Door.serialWrite("BUSY\n");
        } else {
            Start();
        }
    }

    public static void Valid() {
        if (Controller_Door.serialPort != null) {
            Controller_Door.serialWrite("VALID\n");
        } else {
            Start();
        }
    }

    public static void Invalid() {
        if (Controller_Door.serialPort != null) {
            Controller_Door.serialWrite("INVALID\n");
        } else {
            Start();
        }
    }

    private static void serialWrite(String status) {
        System.out.println("[Arduino][" + status.substring(0, status.length() - 1) + "]");
        Platform.runLater(() -> Controller_Door.serialPort.writeBytes(status.getBytes(), status.length()));
    }
}
