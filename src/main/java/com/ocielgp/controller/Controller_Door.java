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
        Controller_Door.serialWrite("BUSY\n");
    }

    public static void Access() {
        Controller_Door.serialWrite("ACCESS0\n");
    }

    public static void AccessBlink() {
        Controller_Door.serialWrite("ACCESS1\n");
    }

    public static void Deny() {
        Controller_Door.serialWrite("DENY\n");
    }

    public static void Unknown() {
        Controller_Door.serialWrite("UNKNOWN\n");
    }

    private static void serialWrite(String status) {
        if (Controller_Door.serialPort != null) {
            System.out.println("[Arduino][" + status.substring(0, status.length() - 1) + "]");
            Platform.runLater(() -> Controller_Door.serialPort.writeBytes(status.getBytes(), status.length()));
        } else {
//            Start();
        }
    }
}
