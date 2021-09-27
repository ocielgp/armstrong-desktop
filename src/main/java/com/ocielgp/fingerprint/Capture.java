package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.*;
import com.ocielgp.dao.JDBC_Member_Fingerprint;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Notifications;
import javafx.application.Platform;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.invoke.MethodHandles;

public class Capture
        implements ActionListener {

    private VBox fingerprintPane;
    private CaptureThread captureThread;
    private ImagePanel fingerprintImage;
    private final Reader reader;
    private Fmd[] fingerprintsArray;

    private Capture(Reader reader) {
        this.reader = reader;
        this.captureThread = new CaptureThread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        System.out.println("Captura creada 1");
    }

    private Capture(Reader reader, VBox fingerprintPane) {
        this.reader = reader;
        this.captureThread = new CaptureThread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        this.fingerprintImage = new ImagePanel();
        this.fingerprintPane = fingerprintPane;
        this.fingerprintsArray = new Fmd[2];
        Platform.runLater(() -> this.fingerprintPane.getChildren().setAll(fingerprintImage));
        System.out.println("Captura creada 2");
    }

    private void StartCaptureThread() {
        captureThread = new CaptureThread(reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        captureThread.start(this);
    }

    private void StopCaptureThread() {
        if (null != captureThread) captureThread.cancel();
    }

    private void WaitForCaptureThread() {
        if (null != captureThread) captureThread.join(1000);
    }

    public static Capture Run(Reader reader) { // run capture in background
        Capture capture = new Capture(reader);
        capture.startCapture();
        return capture;
    }

    public static Capture Run(Reader reader, VBox pane) { // run capture at the interface
        Capture capture = new Capture(reader, pane);
        capture.startCapture();
        return capture;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("captura");
        if (e.getActionCommand().equals(CaptureThread.ACT_CAPTURE)) {
            // Event from capture thread
            CaptureThread.CaptureEvent evt = (CaptureThread.CaptureEvent) e;
            boolean bCanceled = false;

            if (evt.capture_result != null) {
                if (evt.capture_result.image != null && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                    if (this.fingerprintPane != null) {
                        // Display image
                        fingerprintImage.showImage(evt.capture_result.image);
                        ProcessCaptureResult(evt);
                    } else {
                        try {
                            Loading.show();
                            System.out.println("convertido");
                            JDBC_Member_Fingerprint.ReadFindFingerprint(
                                    UareUGlobal.GetEngine().CreateFmd(
                                            evt.capture_result.image, Fmd.Format.ANSI_378_2004
                                    )
                            );
                        } catch (UareUException uareUException) {
                            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                        }
                    }
                    System.out.println("[Data]: " + evt.capture_result.image.getData());
                } else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                    // capture or streaming was canceled, just quit
                    bCanceled = true;
                } else {
                    // bad quality
                    System.out.println(evt.capture_result.quality);
                }
            } else if (evt.exception != null) {
                // Exception during capture
                System.out.println("murio 1");
                System.out.println("Capture: " + evt.exception);
                bCanceled = true;
                Fingerprint.setStatusCode(0); // fingerprint Off
            } else if (evt.reader_status != null) {
                System.out.println(evt.reader_status);
                bCanceled = true;
            }

            if (!bCanceled) {
                // restart capture thread
                WaitForCaptureThread();
                StartCaptureThread();
            }
        }
    }

    private boolean ProcessCaptureResult(CaptureThread.CaptureEvent evt) {
        boolean bCanceled = false;

        if (evt.capture_result != null) {
            if (evt.capture_result.image != null && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                Engine engine = UareUGlobal.GetEngine(); // extract features

                try {
                    Fmd fmd = engine.CreateFmd(evt.capture_result.image, Fmd.Format.ANSI_378_2004);
                    Fingerprint.FB_CompareLocalFingerprints(fmd).thenAccept(isNewFingerprint -> {
                        if (isNewFingerprint) {
                            if (fingerprintsArray[0] == null) fingerprintsArray[0] = fmd;
                            else if (fingerprintsArray[1] == null) fingerprintsArray[1] = fmd;

                            if (fingerprintsArray[0] != null && fingerprintsArray[1] != null) { // perform comparison
                                try {
                                    int falseMatchRate = engine.Compare(fingerprintsArray[0], 0, fingerprintsArray[1], 0);

                                    int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
                                    if (falseMatchRate < targetFalseMatchRate) {
                                        Fingerprint.FB_AddFingerprint(fingerprintsArray[0]);
                                        Notifications.success("Lector de Huellas ( 2 / 2 )", "Huella guardada", 1.5);
                                    } else {
                                        Fingerprint.FB_Shake();
                                        Notifications.danger("Lector de huellas", "Las huellas son diferentes, vuelve a intentar", 2);
                                    }
                                } catch (UareUException uareUException) {
                                    Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                                }

                                // discard FMDs
                                fingerprintsArray[0] = null;
                                fingerprintsArray[1] = null;
                                // the new loop starts
                            } else {
                                // The loop continues
                                Notifications.buildNotification("gmi-fingerprint", "Lector de huellas ( 1 / 2 )", "Colocar de nuevo la huella", 1.5);
                            }
                        } else {
                            // discard FMDs
                            fingerprintsArray[0] = null;
                            fingerprintsArray[1] = null;
                        }
                    });
                } catch (UareUException uareUException) {
                    Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                }
            } else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                // capture or streaming was canceled, just quit
                bCanceled = true;
            } else {
                // bad quality
                System.out.println("Bad Quality " + evt.capture_result.quality);
            }
        } else if (evt.exception != null) {
            // exception during capture
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], evt.exception.getMessage(), evt.exception);
            Fingerprint.setStatusCode(0); // fingerprint Off
            bCanceled = true;
        } else if (null != evt.reader_status) {
            // reader failure
            System.out.println("Bad Status " + evt.reader_status);
            bCanceled = true;
        }

        return !bCanceled;
    }

    private void startCapture() {
        try {
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException uareUException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
        }
        StartCaptureThread();
    }

    public void Stop() {
        // cancel capture
        StopCaptureThread();

        // wait for capture thread to finish
        WaitForCaptureThread();

        // close reader
        try {
            reader.Close();
        } catch (UareUException ignored) {
        }
    }

}