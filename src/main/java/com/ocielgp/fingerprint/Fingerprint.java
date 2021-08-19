package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.*;
import com.jfoenix.controls.JFXButton;
import com.ocielgp.utilities.Notifications;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.ListIterator;

public class Fingerprint {
    private static Reader reader;

    /**
     * status
     * 0 = Disconnected
     * 1 = Background task
     * 2 = Capture task
     */
    private static int fingerprintStatusCode = 0;
    private static final String[] fingerprintStatusDescription = new String[]{
            "DESCONECTADO",
            "CONECTADO",
            "CAPTURANDO"
    };

    private static Capture captureFingerprint;
    private static FontIcon fingerprintIcon; // Dashboard
    private static Label fingerprintLabel; // Dashboard
    private static FingerprintUI fingerprintUI; // Fingerprint Box Controller
    private static final EventHandler<MouseEvent> fingerprintEvent = mouseEvent -> Fingerprint.Scanner();

    public static void initializeUI(FontIcon fontIconFingerprint, Label labelStatus) {
        fingerprintIcon = fontIconFingerprint;
        fingerprintLabel = labelStatus;
        RefreshDashboard();
    }

    public static void loadFingerprints(int idMember) {
        fingerprintUI.loadFingerprints(idMember);
    }

    public static void setFingerprintBox(VBox container, VBox fmdContainer, Label labelCounter, JFXButton startCaptureButton, JFXButton restartCaptureButton) {
        fingerprintUI = new FingerprintUI(container, fmdContainer, labelCounter, startCaptureButton, restartCaptureButton);
        RefreshDashboard();
    }

    public static void setStatusCode(int statusCode) {
        if (statusCode == 0) {
            captureFingerprint.Stop();
            captureFingerprint = null;
            Notifications.warn("gmi-fingerprint", "Lector de Huellas", "Lector de huellas desconectado", 2);
        }
        fingerprintStatusCode = statusCode;
        RefreshDashboard();
    }

    public static int getStatusCode() {
        return fingerprintStatusCode;
    }

    public static String getStatusDescription() {
        return fingerprintStatusDescription[fingerprintStatusCode];
    }

    private static boolean Scan() {
        try {
            ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
            readerCollection.GetReaders();
            reader = readerCollection.get(0); // Fast test to get first lector
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void Scanner() {
        if (Scan()) {
            Notifications.success("gmi-fingerprint", "Lector de Huellas", "Lector de huellas conectado", 2);
            StartCapture();
        } else {
            fingerprintStatusCode = 0;
            Notifications.warn("gmi-fingerprint", "Lector de Huellas", "Lector de huellas no detectado", 2);
        }
        RefreshDashboard();
    }

    public static void StopCapture() {
        if (captureFingerprint != null) {
            captureFingerprint.Stop();
            captureFingerprint = null;

            if (fingerprintUI != null) {
                fingerprintUI.resetUI();
            }
            RefreshDashboard();
        }
    }

    public static void VerifyBackgroundReader() { // Verify current status
        if (fingerprintStatusCode > 1) {
            if (captureFingerprint != null) {
                StopCapture();
            }
            captureFingerprint = Capture.Run(reader);
            fingerprintStatusCode = 1;
            RefreshDashboard();
        }
    }

    public static void StartCapture() { // Background task
        if (captureFingerprint != null) {
            StopCapture();
        }
        captureFingerprint = Capture.Run(reader);
        fingerprintStatusCode = 1;
        RefreshDashboard();
    }

    public static void StartCapture(VBox pane) { // UI task
        if (captureFingerprint != null) {
            StopCapture();
        }
        if (fingerprintStatusCode > 0 && fingerprintStatusCode != 2) {
            captureFingerprint = Capture.Run(reader, pane);
            fingerprintStatusCode = 2;
            RefreshDashboard();
        }
    }

    public static void ResetFingerprintUI() {
        if (fingerprintUI != null) {
            fingerprintUI.resetUI();
        }
    }

    public static void RestartFingerprintUI() {
        if (fingerprintUI != null) {
            fingerprintUI.restartUI();
        }
    }

    public static void RefreshDashboard() {
        if (fingerprintIcon != null && fingerprintLabel != null) {
            String styleClass;
            if (fingerprintStatusCode == 0) {
                fingerprintIcon.addEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                styleClass = "off";
            } else {
                fingerprintIcon.removeEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                styleClass = "on";
            }
            fingerprintIcon.getStyleClass().set(2, styleClass);
            fingerprintLabel.setText(Fingerprint.getStatusDescription());
        }

        if (fingerprintUI != null) {
            if (fingerprintStatusCode == 0) {
                fingerprintUI.restartUI();
                fingerprintUI.hide();
            } else {
                fingerprintUI.show();
            }
        }
    }

    public static void AddFingerprint(Fmd fmd) {
        if (fingerprintUI != null) {
            fingerprintUI.add(fmd);
        }
    }

    public static boolean compareFingerprint(Fmd fmdMember) {
        if (fingerprintUI != null) {
            ArrayList<Fmd> fmds = fingerprintUI.getFmds();
            Engine engine = UareUGlobal.GetEngine();
            for (Fmd fmd : fmds) {
                try {
                    int falsematch_rate = engine.Compare(fmdMember, 0, fmd, 0);

                    int target_falsematch_rate = Engine.PROBABILITY_ONE / 100000; //target rate is 0.00001
                    if (falsematch_rate < target_falsematch_rate) {
                        Notifications.warn("Lector de Huellas", "Esa huella ya ha sido agregada.", 2);
                        return false;
                    }
                } catch (UareUException e) {
                    System.out.println("Engine.CreateFmd()" + e);
                }
            }
        }
        return true;
    }

    public static boolean compareFingerprint(Fmd fingerprint1, Fmd fingerprint2) {
        Engine engine = UareUGlobal.GetEngine();
        try {
            int falsematch_rate = engine.Compare(fingerprint1, 0, fingerprint2, 0);

            int target_falsematch_rate = Engine.PROBABILITY_ONE / 100000; //target rate is 0.00001
            if (falsematch_rate < target_falsematch_rate) {
                return true;
            }
        } catch (UareUException e) {
            System.out.println("Engine.CreateFmd()" + e);
        }
        return false;
    }

    public static ListIterator<Fmd> getFingerprints() {
        if (fingerprintUI != null) {
            return fingerprintUI.getFingerprints();
        } else {
            return null;
        }
    }
}
