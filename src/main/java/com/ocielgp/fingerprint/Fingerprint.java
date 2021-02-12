package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.*;
import com.jfoenix.controls.JFXButton;
import com.ocielgp.utilities.NotificationHandler;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;

public class Fingerprint {
    private static Reader reader;
    private static ReaderCollection readerCollection;
    private static ArrayList<Fmd> fingerprintArray;

    /**
     * status
     * 0 = Fingerprint scanner isn't connected
     * 1 = Fingerprint scanner is connected
     * 2 = Fingerprint is capturing
     */
    private static int fingerprintStatusCode = 0;
    private static final String[] fingerprintStatusDescription = new String[]{
            "DESCONECTADO",
            "CONECTADO",
            "CAPTURANDO"
    };
    private static Capture captureFingerprint;
    private static FontIcon FingerprintIcon; // Dashboard
    private static Label fingerprintLabel; // Dashboard
    private static FingerprintUI fingerprintUI;

    public static void setFingerprintUI(VBox fingerprintPane, VBox fingeprintFmd, Label fingerprintCounter, JFXButton restartButton) {
        Fingerprint.fingerprintUI = new FingerprintUI(fingerprintPane, fingeprintFmd, fingerprintCounter, restartButton);
        if (captureFingerprint != null) {
            captureFingerprint.Stop();
            captureFingerprint = null;
            fingerprintStatusCode = 1;
            RefreshDashboard();
        }
    }

    public static void setFingerprintIcon(FontIcon fingerprintIcon) {
        FingerprintIcon = fingerprintIcon;
        RefreshDashboard();
    }

    public static void setFingerprintLabel(Label fingerprintLabel) {
        Fingerprint.fingerprintLabel = fingerprintLabel;
        RefreshDashboard();

    }

    private static final EventHandler<MouseEvent> fingerprintEvent = mouseEvent -> Fingerprint.Scanner();

    public static String getStatusDescription() {
        return fingerprintStatusDescription[fingerprintStatusCode];
    }

    public static void Scanner() {
        if (Scan()) {
            fingerprintStatusCode = 1;
            NotificationHandler.createNotification("gmi-fingerprint", "Lector de Huellas", "Lector de huellas conectado", 2, NotificationHandler.SUCESS_STYLE);
        } else {
            fingerprintStatusCode = 0;
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

    public static void StartCapture(VBox pane, boolean verification) {
        if (fingerprintStatusCode > 0 && fingerprintStatusCode != 2) {
            captureFingerprint = Capture.Run(reader, pane, verification);
            fingerprintStatusCode = 2;
            RefreshDashboard();
        }
    }

    public static void StopCapture() {
        if (captureFingerprint != null) {
            captureFingerprint.Stop();
            captureFingerprint = null;
            fingerprintStatusCode = 1;
            RefreshDashboard();
        }
    }

    public static void AddFingerprint(Fmd fmd) {
        if (fingerprintUI != null) {
            fingerprintUI.add(fmd);
        }
    }

    public static void RestartCapture() {
        if (fingerprintUI != null) {
            fingerprintUI.restartCapture();
        }
    }

    public static boolean compareFingerprint(Fmd fmd) {
        if (fingerprintUI != null) {
            ArrayList<Fmd> fmds = fingerprintUI.getFmds();
            Engine engine = UareUGlobal.GetEngine();
            for (int i = 0; i < fmds.size(); i++) {
                try {
                    int falsematch_rate = engine.Compare(fmds.get(i), 0, fmd, 0);

                    int target_falsematch_rate = Engine.PROBABILITY_ONE / 100000; //target rate is 0.00001
                    if (falsematch_rate < target_falsematch_rate) {
                        NotificationHandler.warn("Lector de Huellas", "Esa huella ya ha sido agregada.", 2);
                        return false;
                    }
                } catch (UareUException e) {
                    System.out.println("Engine.CreateFmd()" + e);
                }
            }
        }
        return true;
    }

    public static void RefreshDashboard() {
        if (FingerprintIcon != null && fingerprintLabel != null) {
            String styleClass;
            if (fingerprintStatusCode == 0) {
                FingerprintIcon.addEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                styleClass = "off";
            } else {
                FingerprintIcon.removeEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                styleClass = "on";
            }
            FingerprintIcon.getStyleClass().set(2, styleClass);
            fingerprintLabel.setText(Fingerprint.getStatusDescription());
        }

        if (fingerprintUI != null) {
            if (fingerprintStatusCode == 0) {
                fingerprintUI.hide();
            } else {
                fingerprintUI.show();
            }
        }
    }

    public static void setFingerprintStatusCode(int status) {
        if (status == 0) {
            NotificationHandler.createNotification("gmi-fingerprint", "Lector de Huellas", "Lector de huellas desconectado", 2, NotificationHandler.WARN_STYLE);
        }
        fingerprintStatusCode = status;
        RefreshDashboard();
    }

}
