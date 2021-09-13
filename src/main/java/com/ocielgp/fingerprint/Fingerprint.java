package com.ocielgp.fingerprint;

import animatefx.animation.Shake;
import com.digitalpersona.uareu.*;
import com.jfoenix.controls.JFXButton;
import com.ocielgp.utilities.Notifications;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.invoke.MethodHandles;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;

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
    private static FontIcon fingerprintIcon;
    private static final StringProperty fingerprintStatusProperty = new SimpleStringProperty(Fingerprint.getStatusDescription()); // Dashboard
    private static FingerprintBox fingerprintBox;
    private static final EventHandler<MouseEvent> fingerprintEvent = mouseEvent -> Fingerprint.Scanner();

    private static CompletableFuture<Boolean> Scan() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
                readerCollection.GetReaders();
                reader = readerCollection.get(0); // catch exception
                return true;
            } catch (UareUException ignored) {
                return false;
            }
        });
    }

    public static void Scanner() {
        Fingerprint.Scan().thenAccept(scannerConnected -> {
            if (scannerConnected) {
                Notifications.success("gmi-fingerprint", "Lector de Huellas", "Lector de huellas conectado", 2);
                StartCapture();
            } else {
                Fingerprint.setStatusCode(0);
                Notifications.warn("gmi-fingerprint", "Lector de Huellas", "Lector de huellas no detectado", 2);
            }
            RefreshDashboard();
        });
    }

    public static void initializeUI(FontIcon fontIconFingerprint, Label labelStatus) {
        fingerprintIcon = fontIconFingerprint;
        labelStatus.textProperty().bind(fingerprintStatusProperty);
        RefreshDashboard();
    }

    public static void RefreshDashboard() {
        Platform.runLater(() -> {
            if (fingerprintIcon != null) {
                String styleClass;
                if (fingerprintStatusCode == 0) { // disconnected
                    fingerprintIcon.addEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                    styleClass = "off";
                } else { // connected
                    fingerprintIcon.removeEventFilter(MouseEvent.MOUSE_CLICKED, fingerprintEvent);
                    styleClass = "on";
                }
                fingerprintIcon.getStyleClass().set(2, styleClass);
                fingerprintStatusProperty.set(Fingerprint.getStatusDescription());
            }

            if (fingerprintBox != null) {
                if (fingerprintStatusCode == 0) { // disconnected
                    fingerprintBox.hide();
                } else {
                    fingerprintBox.show();
                }
            }
        });
    }

    public static void loadFingerprints(int idMember) {
        fingerprintBox.loadFingerprints(idMember);
    }

    public static void setFingerprintBox(VBox container, VBox fmdContainer, Label labelCounter, JFXButton startCaptureButton, JFXButton restartCaptureButton) {
        fingerprintBox = new FingerprintBox(container, fmdContainer, labelCounter, startCaptureButton, restartCaptureButton);
        RefreshDashboard();
    }

    public static void setStatusCode(int statusCode) {
        if (statusCode == 0 && captureFingerprint != null) {
            captureFingerprint.Stop();
            captureFingerprint = null;
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

    public static void StopCapture() {
        if (captureFingerprint != null) {
            captureFingerprint.Stop();
            captureFingerprint = null;
            RefreshDashboard();
        }
    }

    public static void BackgroundReader() {
        if (fingerprintStatusCode > 1) { // verify background status
            if (captureFingerprint != null) {
                StopCapture();
            }
            captureFingerprint = Capture.Run(reader);
            Fingerprint.setStatusCode(1);
        }
    }

    public static void StartCapture() { // start background task
        if (captureFingerprint != null) {
            StopCapture();
        }
        captureFingerprint = Capture.Run(reader);
        Fingerprint.setStatusCode(1);
    }

    public static void StartCapture(VBox pane) { // start UI task
        if (captureFingerprint != null) {
            StopCapture();
        }
        if (fingerprintStatusCode > 0 && fingerprintStatusCode != 2) {
            captureFingerprint = Capture.Run(reader, pane);
            Fingerprint.setStatusCode(2);
        }
    }

    public static void FB_StopReader() {
        if (fingerprintBox != null) {
            fingerprintBox.stopReader();
        }
    }

    public static void FB_RestartCapture() {
        if (fingerprintBox != null) {
            fingerprintBox.restartCaptureEvent();
        }
    }

    public static void FB_Shake() {
        if (fingerprintBox != null) {
            Platform.runLater(() -> new Shake(fingerprintBox.boxFingerprintPane).play());
            Fingerprint.FB_ClearFingerprintPane();
        }
    }

    public static void FB_AddFingerprint(Fmd fmd) {
        if (fingerprintBox != null) {
            Platform.runLater(() -> fingerprintBox.addFingerprint(fmd));
        }

    }

    public static void FB_ClearFingerprintPane() {
        if (fingerprintBox != null) {
            Platform.runLater(() -> fingerprintBox.clearFingerprintPane());
        }

    }

    public static CompletableFuture<Boolean> FB_CompareLocalFingerprints(Fmd fmdMember) {
        return CompletableFuture.supplyAsync(() -> {
            if (fingerprintBox != null) {
                ListIterator<Fmd> fingerprints = fingerprintBox.getFingerprints();
                Engine engine = UareUGlobal.GetEngine();
                while (fingerprints.hasNext()) {
                    try {
                        int falseMatchRate = engine.Compare(fmdMember, 0, fingerprints.next(), 0);
                        int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
                        if (falseMatchRate < targetFalseMatchRate) {
                            Notifications.warn("Lector de Huellas", "Esa huella ya ha sido agregada.", 1.5);
                            Fingerprint.FB_Shake();
                            return false;
                        }
                    } catch (UareUException uareUException) {
                        Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                    }
                }
            }
            return true;
        });
    }

    public static boolean CompareFingerprints(Fmd fingerprintCaptured, byte[] fingerprintDatabase) {
        Engine engine = UareUGlobal.GetEngine();
        try {
            Fmd fingerprint2 = UareUGlobal.GetImporter().ImportFmd(fingerprintDatabase, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
            int falseMatchRate = engine.Compare(fingerprintCaptured, 0, fingerprint2, 0);

            int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
            if (falseMatchRate < targetFalseMatchRate) {
                return true;
            }
        } catch (UareUException uareUException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
        }
        return false;
    }

    public static CompletableFuture<Boolean> FB_CompareLocalFingerprints(Fmd fingerprint1, Fmd fingerprint2) {
        return CompletableFuture.supplyAsync(() -> {
            Engine engine = UareUGlobal.GetEngine();
            try {
                int falseMatchRate = engine.Compare(fingerprint1, 0, fingerprint2, 0);

                int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
                if (falseMatchRate < targetFalseMatchRate) {
                    return true;
                }
            } catch (UareUException uareUException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
            }
            return false;
        });
    }

    public static ListIterator<Fmd> getFingerprints() {
        return (fingerprintBox == null) ? null : fingerprintBox.getFingerprints();
    }
}
