package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.*;
import com.ocielgp.utilities.Notifications;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.invoke.MethodHandles;

public class Fingerprint_Controller {
    /**
     * status
     * 0 = Disconnected
     * 1 = Background task
     * 2 = Fingerprint_Capture task
     */
    private static int fingerprintStatusCode = -1; // initial value
    private static final String[] fingerprintStatusDescription = new String[]{
            "DESCONECTADO",
            "CONECTADO",
            "CAPTURANDO"
    };
    private static Reader reader;
    private static final StringProperty fingerprintStatusProperty = new SimpleStringProperty(Fingerprint_Controller.fingerprintStatusDescription[0]);
    private static FontIcon fingerprintIcon;
    private static Fingerprint_Capture_Box fingerprintCaptureBox;
    private static Fingerprint_Capture fingerprintCaptureThread;

    private static final EventHandler<MouseEvent> fingerprintEvent = mouseEvent -> Fingerprint_Controller.Scanner();

    public static void Start(FontIcon fontIconFingerprint, Label labelStatus) {
        if (Fingerprint_Controller.fingerprintIcon == null) {
            Fingerprint_Controller.fingerprintIcon = fontIconFingerprint;
            labelStatus.textProperty().bind(Fingerprint_Controller.fingerprintStatusProperty);
            Fingerprint_Controller.Scanner();
        }
    }

    public static void setFingerprintCaptureBox(Fingerprint_Capture_Box fingerprintCaptureBox) {
        Fingerprint_Controller.fingerprintCaptureBox = fingerprintCaptureBox;
    }

    public static boolean IsConnected() {
        return Fingerprint_Controller.fingerprintStatusCode > 0;
    }

    public static boolean IsReading() {
        return Fingerprint_Controller.fingerprintStatusCode == 1;
    }

    private static void Scanner() {
        Platform.runLater(() -> {
            try {
                ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
                readerCollection.GetReaders();
                Fingerprint_Controller.reader = readerCollection.get(0); // catch exception
                Platform.runLater(() -> Notifications.Success("gmi-fingerprint", "Lector de Huellas", "Lector de huellas conectado", 2));
                BackgroundReader();
            } catch (Exception ignored) {
                Fingerprint_Controller.setStatusCode(0);
                Platform.runLater(() -> Notifications.Warn("gmi-fingerprint", "Lector de Huellas", "Lector de huellas no detectado", 2));
            }
        });
    }

    public static void setStatusCode(int statusCode) {
        if (statusCode != Fingerprint_Controller.fingerprintStatusCode) {
            Fingerprint_Controller.fingerprintStatusCode = statusCode;
            Platform.runLater(() -> {
                Fingerprint_Controller.fingerprintStatusProperty.set(Fingerprint_Controller.fingerprintStatusDescription[statusCode]);
                RefreshDashboard();
            });
        }
    }

    public static void StopCapture() {
        if (Fingerprint_Controller.fingerprintCaptureThread != null) {
            Fingerprint_Controller.fingerprintCaptureThread.Stop();
            Fingerprint_Controller.fingerprintCaptureThread = null;

            if (Fingerprint_Controller.IsConnected() && Fingerprint_Controller.fingerprintCaptureBox != null)
                Fingerprint_Controller.fingerprintCaptureBox.initialStateStartCapture();
        }
    }

    public static void BackgroundReader() {
        if (Fingerprint_Controller.IsConnected() && !Fingerprint_Controller.IsReading()) {
            if (Fingerprint_Controller.fingerprintCaptureThread != null) {
                StopCapture();
            }
            Fingerprint_Controller.fingerprintCaptureThread = new Fingerprint_Capture(Fingerprint_Controller.reader).createBackgroundThread();
            Fingerprint_Controller.setStatusCode(1);
        }
    }

    public static void ViewReader() {
        if (Fingerprint_Controller.fingerprintCaptureThread != null) {
            StopCapture();
        }
        if (fingerprintCaptureBox != null) {
            Fingerprint_Controller.fingerprintCaptureThread = new Fingerprint_Capture(Fingerprint_Controller.reader).createViewThread(fingerprintCaptureBox);
            Fingerprint_Controller.setStatusCode(2);
        }
    }

    public static void RefreshDashboard() {
//        System.out.println("RefreshDashboard()");
        if (Fingerprint_Controller.IsConnected()) {
            if (Fingerprint_Controller.fingerprintIcon != null) {
                Fingerprint_Controller.fingerprintIcon.removeEventFilter(MouseEvent.MOUSE_CLICKED, Fingerprint_Controller.fingerprintEvent);
                Fingerprint_Controller.fingerprintIcon.getStyleClass().set(2, "on");
            }
            if (Fingerprint_Controller.fingerprintCaptureBox != null)
                Fingerprint_Controller.fingerprintCaptureBox.show();
        } else {
            if (Fingerprint_Controller.fingerprintIcon != null) {
                Fingerprint_Controller.fingerprintIcon.addEventFilter(MouseEvent.MOUSE_CLICKED, Fingerprint_Controller.fingerprintEvent);
                Fingerprint_Controller.fingerprintIcon.getStyleClass().set(2, "off");
            }
            if (Fingerprint_Controller.fingerprintCaptureBox != null)
                Fingerprint_Controller.fingerprintCaptureBox.hide();
        }
    }


    public static boolean CompareFingerprints(Fmd fingerprintCaptured, byte[] fingerprintFromDatabase) {
        Engine engine = UareUGlobal.GetEngine();
        try {
            Fmd fingerprint2 = UareUGlobal.GetImporter().ImportFmd(fingerprintFromDatabase, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
            int falseMatchRate = engine.Compare(fingerprintCaptured, 0, fingerprint2, 0);

            int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
            if (falseMatchRate < targetFalseMatchRate) {
                return true;
            }
        } catch (UareUException uareUException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException);
        }
        return false;
    }
}
