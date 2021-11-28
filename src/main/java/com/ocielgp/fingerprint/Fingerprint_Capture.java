package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.*;
import com.ocielgp.dao.JDBC_Member_Fingerprint;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Notifications;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;

public class Fingerprint_Capture implements ActionListener {

    private Fingerprint_Capture_Box fingerprintCaptureBox;
    private Fingerprint_Capture_Thread captureThread;
    private final Reader reader;
    private Fmd[] fingerprintsArray;

    public Fingerprint_Capture(Reader reader) {
        this.reader = reader;
    }

    public Fingerprint_Capture createBackgroundThread() {
        this.captureThread = new Fingerprint_Capture_Thread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        startCapture();
        System.out.println("[Fingerprint_Capture][Background]");
        return this;
    }

    public Fingerprint_Capture createViewThread(Fingerprint_Capture_Box fingerprintCaptureBox) {
        this.captureThread = new Fingerprint_Capture_Thread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        startCapture();
        this.fingerprintCaptureBox = fingerprintCaptureBox;
        this.fingerprintsArray = new Fmd[2];
        System.out.println("[Fingerprint_Capture][View]");
        return this;
    }

    private void StopCaptureThread() {
        if (captureThread != null) captureThread.cancel();
    }

    private void WaitForCaptureThread() {
        if (captureThread != null) captureThread.join(1000);
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
        this.fingerprintCaptureBox.getBoxFingerprintView().getChildren().setAll(imageView);
    }

    public void actionPerformed(ActionEvent e) {
        if (JDBC_Member_Fingerprint.isReaderAvailable) {
            if (e.getActionCommand().equals(Fingerprint_Capture_Thread.ACT_CAPTURE)) {
                // event from capture thread
                Fingerprint_Capture_Thread.CaptureEvent evt = (Fingerprint_Capture_Thread.CaptureEvent) e;
                boolean bCanceled = false;

                if (evt.capture_result != null) {
                    if (evt.capture_result.image != null && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                        if (this.fingerprintCaptureBox != null) {
                            // display image
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
                                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException);
                            }
                        }
                        System.out.println("[Fingerprint_Capture][Captured]");
                    } else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                        // capture or streaming was canceled, just quit
                        bCanceled = true;
                    } else {
                        // bad quality
                        System.out.println("[Fingerprint_Capture][Bad Quality] " + evt.capture_result.quality);
                    }
                } else if (evt.exception != null) {
                    // exception during capture
                    System.out.println("[Fingerprint_Capture][Disconnected]");
                    bCanceled = true;
                    Fingerprint_Controller.setStatusCode(0); // fingerprint off
                    Notifications.Warn("gmi-fingerprint", "Lector de Huellas", "Lector de huellas desconectado", 3);
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
                    this.fingerprintCaptureBox.compareLocalFingerprints(fmd).thenAccept(isNewFingerprint -> {
                        if (isNewFingerprint) {
                            if (fingerprintsArray[0] == null) fingerprintsArray[0] = fmd;
                            else if (fingerprintsArray[1] == null) fingerprintsArray[1] = fmd;

                            if (fingerprintsArray[0] != null && fingerprintsArray[1] != null) { // perform comparison
                                try {
                                    int falseMatchRate = engine.Compare(fingerprintsArray[0], 0, fingerprintsArray[1], 0);

                                    int targetFalseMatchRate = Engine.PROBABILITY_ONE / 100000; // target rate is 0.00001
                                    if (falseMatchRate < targetFalseMatchRate) {
                                        this.fingerprintCaptureBox.addFingerprintToList(fingerprintsArray[1]);
                                        Notifications.Success("Lector de Huellas ( 2 / 2 )", "Huella guardada", 1.5);
                                    } else {
                                        this.fingerprintCaptureBox.shakeFingerprintView();
                                        Notifications.Danger("Lector de huellas", "Las huellas son diferentes, vuelve a intentar", 1.5);
                                    }
                                } catch (UareUException uareUException) {
                                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException);
                                }

                                // discard FMDs
                                fingerprintsArray[0] = null;
                                fingerprintsArray[1] = null;
                                // the new loop starts
                            } else {
                                // the loop continues
                                Notifications.Default("gmi-fingerprint", "Lector de huellas ( 1 / 2 )", "Colocar de nuevo la huella", 2);
                            }
                        } else {
                            // discard FMDs
                            fingerprintsArray[0] = null;
                            fingerprintsArray[1] = null;
                        }
                    });
                } catch (UareUException uareUException) {
                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException);
                }
            } else {
                // bad quality
                System.out.println("[Fingerprint_Capture][Bad Quality] " + evt.capture_result.quality);
            }
        } else if (evt.exception != null) {
            // exception during capture
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], evt.exception);
            Fingerprint_Controller.setStatusCode(0); // fingerprint Off
        } else if (evt.reader_status != null) {
            // reader failure
            System.out.println("[Fingerprint_Capture][Bad Status] " + evt.reader_status);
        }

    }

    public void startCapture() {
        try {
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException uareUException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException);
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