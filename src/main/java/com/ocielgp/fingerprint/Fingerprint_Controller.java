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

public class Fingerprint_Controller {
    private static Reader reader;

    /**
     * status
     * 0 = Disconnected
     * 1 = Background task
     * 2 = Fingerprint_Capture task
     */
    private static int fingerprintStatusCode = 0;
    private static final String[] fingerprintStatusDescription = new String[]{
            "DESCONECTADO",
            "CONECTADO",
            "CAPTURANDO"
    };
    private static final StringProperty fingerprintStatusProperty = new SimpleStringProperty(Fingerprint_Controller.getStatusDescription()); // Dashboard
    private static FontIcon fingerprintIcon;
    private static Fingerprint_Capture captureFingerprint;
    private static Fingerprint_Capture_Box fingerprintBox;

    public static void Scanner() {
        Platform.runLater(() -> {
            try {
                ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
                readerCollection.GetReaders();
                reader = readerCollection.get(0); // catch exception

                Notifications.Success("gmi-fingerprint", "Lector de Huellas", "Lector de huellas conectado", 2);
                StartCapture();
            } catch (Exception ignored) {
                Fingerprint_Controller.setStatusCode(0);
                Notifications.Warn("gmi-fingerprint", "Lector de Huellas", "Lector de huellas no detectado", 2);
            }

            RefreshDashboard();
        });
    }

    private static final EventHandler<MouseEvent> fingerprintEvent = mouseEvent -> Fingerprint_Controller.Scanner();

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
                fingerprintStatusProperty.set(Fingerprint_Controller.getStatusDescription());
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

    public static void initializeUI(FontIcon fontIconFingerprint, Label labelStatus) {
        fingerprintIcon = fontIconFingerprint;
        labelStatus.textProperty().bind(fingerprintStatusProperty);
        RefreshDashboard();
    }

    public static void setFingerprintBox(VBox container, VBox fmdContainer, Label labelCounter, JFXButton startCaptureButton, JFXButton restartCaptureButton) {
        fingerprintBox = new Fingerprint_Capture_Box(container, fmdContainer, labelCounter, startCaptureButton, restartCaptureButton);
        RefreshDashboard();
    }

    public static void loadFingerprints(int idMember) {
        fingerprintBox.loadFingerprints(idMember);
    }

    public static void BackgroundReader() {
        if (fingerprintStatusCode > 1) { // verify background status
            if (captureFingerprint != null) {
                StopCapture();
            }
            captureFingerprint = Fingerprint_Capture.Run(reader);
            Fingerprint_Controller.setStatusCode(1);
        }
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

    public static void StartCapture() { // start background task
        if (captureFingerprint != null) {
            StopCapture();
        }
        captureFingerprint = Fingerprint_Capture.Run(reader);
        Fingerprint_Controller.setStatusCode(1);
    }

    public static void StartCapture(VBox pane) { // start UI task
        if (captureFingerprint != null) {
            StopCapture();
        }
        if (fingerprintStatusCode > 0 && fingerprintStatusCode != 2) {
            captureFingerprint = Fingerprint_Capture.Run(reader, pane);
            Fingerprint_Controller.setStatusCode(2);
        }
    }

    public static void FB_Shake() {
        if (fingerprintBox != null) {
            Platform.runLater(() -> new Shake(fingerprintBox.boxFingerprintPane).play());
            Fingerprint_Controller.FB_ClearFingerprintPane();
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
                            Notifications.Warn("Lector de Huellas", "Esa huella ya ha sido agregada.", 1.5);
                            Fingerprint_Controller.FB_Shake();
                            return false;
                        }
                    } catch (UareUException uareUException) {
                        Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                    }
                }
            }
            return true;
        });
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
            Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
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
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
            }
            return false;
        });
    }

    public static ListIterator<Fmd> getFingerprints() {
        return (fingerprintBox == null) ? null : fingerprintBox.getFingerprints();
    }
}
