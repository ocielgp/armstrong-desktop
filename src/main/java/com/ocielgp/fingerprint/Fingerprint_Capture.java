package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.*;
import com.ocielgp.dao.JDBC_Member_Fingerprint;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Notifications;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;

public class Fingerprint_Capture implements ActionListener {

    private VBox fingerprintPane;
    private Fingerprint_Capture_Thread captureThread;
    private final Reader reader;
    private Fmd[] fingerprintsArray;

    private Fingerprint_Capture(Reader reader) {
        this.reader = reader;
        this.captureThread = new Fingerprint_Capture_Thread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        System.out.println("Captura creada 1");
    }

    private Fingerprint_Capture(Reader reader, VBox fingerprintPane) {
        this.reader = reader;
        this.captureThread = new Fingerprint_Capture_Thread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        this.fingerprintPane = fingerprintPane;
        this.fingerprintsArray = new Fmd[2];
        System.out.println("Captura creada 2");
    }

    public static Fingerprint_Capture Run(Reader reader) { // run capture in background
        Fingerprint_Capture capture = new Fingerprint_Capture(reader);
        capture.startCapture();
        return capture;
    }

    private void StopCaptureThread() {
        if (null != captureThread) captureThread.cancel();
    }

    private void WaitForCaptureThread() {
        if (null != captureThread) captureThread.join(1000);
    }

    public static Fingerprint_Capture Run(Reader reader, VBox pane) { // run capture at the interface
        Fingerprint_Capture capture = new Fingerprint_Capture(reader, pane);
        capture.startCapture();
        return capture;
    }

    private void StartCaptureThread() {
        captureThread = new Fingerprint_Capture_Thread(reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        captureThread.start(this);
    }

    public void showImage(Fid image) {
        Fid.Fiv view = image.getViews()[0];
        BufferedImage bufferedImage = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        bufferedImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());

        Image toFXImage = SwingFXUtils.toFXImage(bufferedImage, null);
        ImageView imageView = new ImageView(toFXImage);
//        super.setPreserveRatio(true);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        this.fingerprintPane.getChildren().setAll(imageView);
    }

    public void actionPerformed(ActionEvent e) {
        if (JDBC_Member_Fingerprint.isReaderAvailable) {
            if (e.getActionCommand().equals(Fingerprint_Capture_Thread.ACT_CAPTURE)) {
                // event from capture thread
                Fingerprint_Capture_Thread.CaptureEvent evt = (Fingerprint_Capture_Thread.CaptureEvent) e;
                boolean bCanceled = false;

                if (evt.capture_result != null) {
                    if (evt.capture_result.image != null && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                        if (this.fingerprintPane != null) {
                            // Display image
                            this.showImage(evt.capture_result.image);
                            ProcessCaptureResult(evt);
                        } else {
                            try {
                                Loading.show();
                                JDBC_Member_Fingerprint.ReadFindFingerprint(
                                        UareUGlobal.GetEngine().CreateFmd(
                                                evt.capture_result.image, Fmd.Format.ANSI_378_2004
                                        )
                                );
                            } catch (UareUException uareUException) {
                                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
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
                    // exception during capture
                    System.out.println("murio 1");
                    bCanceled = true;
                    Fingerprint_Controller.setStatusCode(0); // fingerprint off
                    Notifications.Warn("gmi-fingerprint", "Lector de Huellas", "Lector de huellas desconectado", 2);
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
    }

    private void ProcessCaptureResult(Fingerprint_Capture_Thread.CaptureEvent evt) {
        if (evt.capture_result != null) {
            if (evt.capture_result.image != null && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                Engine engine = UareUGlobal.GetEngine(); // extract features

                try {
                    Fmd fmd = engine.CreateFmd(evt.capture_result.image, Fmd.Format.ANSI_378_2004);
                    Fingerprint_Controller.FB_CompareLocalFingerprints(fmd).thenAccept(isNewFingerprint -> {
                        if (isNewFingerprint) {
                            if (fingerprintsArray[0] == null) fingerprintsArray[0] = fmd;
                            else if (fingerprintsArray[1] == null) fingerprintsArray[1] = fmd;

                            if (fingerprintsArray[0] != null && fingerprintsArray[1] != null) { // perform comparison
                                try {
                                    int falseMatchRate = engine.Compare(fingerprintsArray[0], 0, fingerprintsArray[1], 0);

                                    int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
                                    if (falseMatchRate < targetFalseMatchRate) {
                                        Fingerprint_Controller.FB_AddFingerprint(fingerprintsArray[0]);
                                        Notifications.Success("Lector de Huellas ( 2 / 2 )", "Huella guardada", 1.5);
                                    } else {
                                        Fingerprint_Controller.FB_Shake();
                                        Notifications.Danger("Lector de huellas", "Las huellas son diferentes, vuelve a intentar", 2);
                                    }
                                } catch (UareUException uareUException) {
                                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                                }

                                // discard FMDs
                                fingerprintsArray[0] = null;
                                fingerprintsArray[1] = null;
                                // the new loop starts
                            } else {
                                // The loop continues
                                Notifications.Default("gmi-fingerprint", "Lector de huellas ( 1 / 2 )", "Colocar de nuevo la huella", 1.5);
                            }
                        } else {
                            // discard FMDs
                            fingerprintsArray[0] = null;
                            fingerprintsArray[1] = null;
                        }
                    });
                } catch (UareUException uareUException) {
                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                }
            } else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                // capture or streaming was canceled, just quit
            } else {
                // bad quality
                System.out.println("Bad Quality " + evt.capture_result.quality);
            }
        } else if (evt.exception != null) {
            // exception during capture
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], evt.exception.getMessage(), evt.exception);
            Fingerprint_Controller.setStatusCode(0); // fingerprint Off
        } else if (null != evt.reader_status) {
            // reader failure
            System.out.println("Bad Status " + evt.reader_status);
        }

    }

    private void startCapture() {
        try {
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException uareUException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
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