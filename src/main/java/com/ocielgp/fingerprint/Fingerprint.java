package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.controller.DashboardController;
import com.ocielgp.utilities.NotificationHandler;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Fingerprint {
    private static Reader reader;
    private static ReaderCollection readerCollection;

    /**
     * status
     * 0 = Fingerprint scanner isn't connected
     * 1 = Fingerprint scanner is connected
     */
    private static int fingerprintStatus = 0;
    private static ScheduledService<Void> service;
    private static final String[] fingerprintStatusDescription = new String[]{
            "DESCONECTADO",
            "CONECTADO"
    };
    private CaptureFingerprint captureFingerprint;

    public static String getStatus() {
        return fingerprintStatusDescription[fingerprintStatus];
    }

    public static int getStatusCode() {
        return fingerprintStatus;
    }

    public static Reader getReader() {
        return reader;
    }

    public static void Scanner() {
        if (Scan()) {
            CaptureFingerprint.Run(reader);
            fingerprintStatus = 1;
            NotificationHandler.createNotification("gmi-fingerprint", "Lector de Huellas", "Lector de huellas conectado", 2, NotificationHandler.SUCESS_STYLE);
        } else {
            fingerprintStatus = 0;
            NotificationHandler.createNotification("gmi-fingerprint", "Lector de Huellas", "Lector de huellas no detectado", 2, NotificationHandler.WARN_STYLE);
        }
        DashboardUpdate(); // Update UI Fingerprint Dashboard
    }

    private static boolean Scan() {
        try {
            readerCollection = UareUGlobal.GetReaderCollection();
            readerCollection.GetReaders();
            reader = readerCollection.get(0); // Fast test to get first lector
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void DashboardUpdate() {
        if (Fingerprint.getStatusCode() == 0) { // Fingerprint off
            DashboardController.fingerprintUI(Fingerprint.getStatus(), "off");
        } else { // Fingerprint on
            DashboardController.fingerprintUI(Fingerprint.getStatus(), "on");
        }
    }

    public static void setFingerprintStatus(int status) {
        switch (status) {
            case 0: {
                NotificationHandler.createNotification("gmi-fingerprint", "Lector de Huellas", "Lector de huellas desconectado", 2, NotificationHandler.WARN_STYLE);
            }
            break;
        }
        fingerprintStatus = status;
        DashboardUpdate();
    }
}
