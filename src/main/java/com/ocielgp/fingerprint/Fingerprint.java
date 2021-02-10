package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.utilities.NotificationHandler;
import javafx.concurrent.ScheduledService;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class Fingerprint {
    private static Reader reader;
    private static ReaderCollection readerCollection;

    /**
     * status
     * 0 = Fingerprint scanner isn't connected
     * 1 = Fingerprint scanner is connected
     */
    private static int fingerprintStatus = 0;
    private static final String[] fingerprintStatusDescription = new String[]{
            "DESCONECTADO",
            "CONECTADO",
            "CAPTURANDO"
    };
    private static CaptureFingerprint captureFingerprint;
    public static FontIcon FingerprintIcon; // Dashboard
    public static Label FingerprintStatus; // Dashboard
    public static VBox FingerprintPane;

    private static final EventHandler<MouseEvent> fingerprintEvent = mouseEvent -> Fingerprint.Scanner();

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
            fingerprintStatus = 1;
            NotificationHandler.createNotification("gmi-fingerprint", "Lector de Huellas", "Lector de huellas conectado", 2, NotificationHandler.SUCESS_STYLE);
        } else {
            fingerprintStatus = 0;
            NotificationHandler.createNotification("gmi-fingerprint", "Lector de Huellas", "Lector de huellas no detectado", 2, NotificationHandler.WARN_STYLE);
        }
        RefreshDashboard(); // Update UI Fingerprint Dashboard
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

    public static void StartCapture(VBox pane) {
        if (fingerprintStatus > 0 && fingerprintStatus != 2) {
            CaptureFingerprint.Run(reader, pane);
            fingerprintStatus = 2;
            RefreshDashboard();
        }
    }

    public static void RefreshDashboard() {
        if (FingerprintIcon != null && FingerprintStatus != null) {
            String styleClass;
            if (Fingerprint.getStatusCode() == 0) {
                FingerprintIcon.addEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                styleClass = "off";
            } else {
                FingerprintIcon.removeEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                styleClass = "on";
            }
            FingerprintIcon.getStyleClass().set(2, styleClass);
            FingerprintStatus.setText(Fingerprint.getStatus());
        }

        if (FingerprintPane != null) {
            if (Fingerprint.getStatusCode() == 0) {
                FingerprintPane.setVisible(false);
                FingerprintPane.setManaged(false);
            } else {
                FingerprintPane.setVisible(true);
                FingerprintPane.setManaged(true);
            }
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
        RefreshDashboard();
    }
}
