package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.*;
import com.ocielgp.database.FingerprintData;
import com.ocielgp.utilities.NotificationHandler;
import javafx.scene.layout.VBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;

public class Capture
        implements ActionListener {

    private VBox fingerprintPane;
    private CaptureThread captureThread;
    private ImagePanel fingerprintImage;
    private final Reader reader;
    private boolean verification;
    private Fmd[] m_fmds;

    private Capture(Reader reader) {
        this.reader = reader;
        captureThread = new CaptureThread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        System.out.println("Captura creada 1");
    }

    private Capture(Reader reader, VBox fingerprintPane, boolean verification) {
        this.reader = reader;
        this.captureThread = new CaptureThread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        this.fingerprintImage = new ImagePanel();
        this.fingerprintPane = fingerprintPane;
        if (verification) {
            this.verification = true;
            this.m_fmds = new Fmd[2];
            NotificationHandler.notify("gmi-fingerprint", "Lector de huellas", "Coloca la huella sobre el lector.", 2);
        }
        this.fingerprintPane.getChildren().setAll(fingerprintImage);
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

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(CaptureThread.ACT_CAPTURE)) {
            // Event from capture thread
            CaptureThread.CaptureEvent evt = (CaptureThread.CaptureEvent) e;
            boolean bCanceled = false;

            if (evt.capture_result != null) {
                if (evt.capture_result.image != null && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                    if (this.fingerprintPane != null) {
                        // Display image
                        fingerprintImage.showImage(evt.capture_result.image);

                        if (verification) {
                            ProcessCaptureResult(evt);
                        }
                    } else if (Fingerprint.getStatusCode() == 1) {
                        try {
                            System.out.println("convertido");
                            FingerprintData.searchFingerprint(UareUGlobal.GetEngine().CreateFmd(evt.capture_result.image, Fmd.Format.ANSI_378_2004));
                        } catch (UareUException uareUException) {
                            uareUException.printStackTrace();
                        }
                    }
                    System.out.println("[Data]: " + evt.capture_result.image.getData());
                } else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                    // Capture or streaming was canceled, just quit
                    bCanceled = true;
                } else {
                    // Bad quality
                    System.out.println(evt.capture_result.quality);
                }
            } else if (evt.exception != null) {
                // Exception during capture
                System.out.println("Capture: " + evt.exception);
                bCanceled = true;
                Fingerprint.setFingerprintStatusCode(0); // Fingerprint Off
            } else if (evt.reader_status != null) {
                System.out.println(evt.reader_status);
                bCanceled = true;
            }

            if (!bCanceled) {
                // Restart capture thread
                WaitForCaptureThread();
                StartCaptureThread();
            }
        }
    }

    private boolean ProcessCaptureResult(CaptureThread.CaptureEvent evt) {
        boolean bCanceled = false;

        if (null != evt.capture_result) {
            if (null != evt.capture_result.image && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                //extract features
                Engine engine = UareUGlobal.GetEngine();

                try {
                    Fmd fmd = engine.CreateFmd(evt.capture_result.image, Fmd.Format.ANSI_378_2004);
                    if (Fingerprint.compareFingerprint(fmd)) {
                        if (null == m_fmds[0]) m_fmds[0] = fmd;
                        else if (null == m_fmds[1]) m_fmds[1] = fmd;

                        if (null != m_fmds[0] && null != m_fmds[1]) {
                            //perform comparison
                            try {
                                int falsematch_rate = engine.Compare(m_fmds[0], 0, m_fmds[1], 0);

                                int target_falsematch_rate = Engine.PROBABILITY_ONE / 100000; //target rate is 0.00001
                                if (falsematch_rate < target_falsematch_rate) {
                                    Fingerprint.AddFingerprint(m_fmds[0]);
                                    NotificationHandler.sucess("Lector de Huellas", "Huellas coinciden.", 2);
                                } else {
                                    NotificationHandler.danger("Lector de huellas", "Las huellas no coinciden.", 2);
                                }
                            } catch (UareUException e) {
                                System.out.println("Engine.CreateFmd()" + e);
                            }

                            //discard FMDs
                            m_fmds[0] = null;
                            m_fmds[1] = null;

                            //the new loop starts
                        } else {
                            NotificationHandler.notify("gmi-fingerprint", "Lector de huellas", "Vuelve a colocar la huella sobre el lector.", 2);
                            //the loop continues
//                    m_text.append(m_strPrompt2);
                        }
                    } else {
                        //discard FMDs
                        m_fmds[0] = null;
                        m_fmds[1] = null;
                    }
                } catch (UareUException e) {
                    System.out.println("Engine.CreateFmd()" + e);
                }
            } else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                //capture or streaming was canceled, just quit
                bCanceled = true;
            } else {
                //bad quality
                System.out.println("Bad Quality " + evt.capture_result.quality);
            }
        } else if (null != evt.exception) {
            //exception during capture
            System.out.println("Capture " + evt.exception);
            bCanceled = true;
        } else if (null != evt.reader_status) {
            //reader failure
            System.out.println("Bad Status " + evt.reader_status);
            bCanceled = true;
        }

        return !bCanceled;
    }

    private void startCapture() {
        // Open reader
        try {
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException e) {
            System.out.println("Reader.Open()");
            System.out.println(e);
        }

        StartCaptureThread();
    }

    private void startCapture(VBox pane) {
        // Open reader
        try {
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException e) {
            System.out.println("Reader.Open()");
            System.out.println(e);
        }

        StartCaptureThread();

    }

    public static Capture Run(Reader reader) { // Run in background
        Capture capture = new Capture(reader);
        capture.startCapture();
        return capture;
    }

    public static Capture Run(Reader reader, VBox pane, boolean verification) {
        Capture capture = new Capture(reader, pane, verification);
        capture.startCapture(pane);
        return capture;
    }

    public void Stop() {
        // Cancel capture
        StopCaptureThread();

        // Wait for capture thread to finish
        WaitForCaptureThread();

        // Close reader
        try {
            reader.Close();
        } catch (UareUException e) {
            System.out.println("Reader.Close()");
            System.out.println(e);
            Fingerprint.RestartCapture();
        }
    }

}