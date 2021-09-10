package com.ocielgp.fingerprint;

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
import java.util.ArrayList;
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
    private static FontIcon fingerprintIcon; // Dashboard
    private static final StringProperty fingerprintStatusProperty = new SimpleStringProperty(Fingerprint.getStatusDescription()); // Dashboard
    private static FingerprintUI fingerprintUI; // Fingerprint Box Controller
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
        CompletableFuture.runAsync(() -> {
            fingerprintIcon = fontIconFingerprint;
            labelStatus.textProperty().bind(fingerprintStatusProperty);
            RefreshDashboard();
        });
    }

    public static void RefreshDashboard() {
        CompletableFuture.runAsync(() -> {
            if (fingerprintIcon != null) {
                Platform.runLater(() -> {
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
                });
            }

            if (fingerprintUI != null) {
                if (fingerprintStatusCode == 0) { // disconnected
                    fingerprintUI.restartUI();
                    fingerprintUI.hide();
                } else {
                    fingerprintUI.show();
                }
            }
        });
    }

    public static void loadFingerprints(int idMember) {
        fingerprintUI.loadFingerprints(idMember);
    }

    public static void setFingerprintBox(VBox container, VBox fmdContainer, Label labelCounter, JFXButton startCaptureButton, JFXButton restartCaptureButton) {
        CompletableFuture.runAsync(() -> {
            fingerprintUI = new FingerprintUI(container, fmdContainer, labelCounter, startCaptureButton, restartCaptureButton);
            RefreshDashboard();
        });
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
        CompletableFuture.runAsync(() -> {
            if (captureFingerprint != null) {
                captureFingerprint.Stop();
                captureFingerprint = null;

                if (fingerprintUI != null) {
                    fingerprintUI.resetUI();
                }
                RefreshDashboard();
            }
        });
    }

    public static void BackgroundReader() {
        CompletableFuture.runAsync(() -> {
            if (fingerprintStatusCode > 1) { // verify background status
                if (captureFingerprint != null) {
                    StopCapture();
                }
                captureFingerprint = Capture.Run(reader);
                Fingerprint.setStatusCode(1);
            }
        });
    }

    public static void StartCapture() { // start background task
        CompletableFuture.runAsync(() -> {
            if (captureFingerprint != null) {
                StopCapture();
            }
            captureFingerprint = Capture.Run(reader);
            Fingerprint.setStatusCode(1);
        });
    }

    public static void StartCapture(VBox pane) { // start UI task
        CompletableFuture.runAsync(() -> {
            if (captureFingerprint != null) {
                StopCapture();
            }
            if (fingerprintStatusCode > 0 && fingerprintStatusCode != 2) {
                captureFingerprint = Capture.Run(reader, pane);
                Fingerprint.setStatusCode(2);
            }
        });
    }

    public static void ResetFingerprintUI() {
        CompletableFuture.runAsync(() -> {
            if (fingerprintUI != null) {
                fingerprintUI.resetUI();
            }
        });
    }

    public static void AddFingerprint(Fmd fmd) {
        CompletableFuture.runAsync(() -> {
            if (fingerprintUI != null) {
                fingerprintUI.add(fmd);
            }
        });
    }

    public static CompletableFuture<Boolean> compareFingerprint(Fmd fmdMember) {
        return CompletableFuture.supplyAsync(() -> {
            if (fingerprintUI != null) {
                ArrayList<Fmd> fmds = fingerprintUI.getFmds();
                Engine engine = UareUGlobal.GetEngine();
                for (Fmd fmd : fmds) {
                    try {
                        int falseMatchRate = engine.Compare(fmdMember, 0, fmd, 0);

                        int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
                        if (falseMatchRate < targetFalseMatchRate) {
                            Notifications.warn("Lector de Huellas", "Esa huella ya ha sido agregada.", 2);
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

    public static CompletableFuture<Boolean> compareFingerprint(Fmd fingerprint1, byte[] fingerprintFromDatabase) {
        return CompletableFuture.supplyAsync(() -> {
            Engine engine = UareUGlobal.GetEngine();
            try {
                Fmd fingerprint2 = UareUGlobal.GetImporter().ImportFmd(fingerprintFromDatabase, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
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

    public static CompletableFuture<Boolean> compareFingerprint(Fmd fingerprint1, Fmd fingerprint2) {
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
        return (fingerprintUI == null) ? null : fingerprintUI.getFingerprints();
    }
}
