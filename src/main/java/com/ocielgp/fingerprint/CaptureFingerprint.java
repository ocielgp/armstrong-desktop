package com.ocielgp.fingerprint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import javafx.scene.layout.VBox;

public class CaptureFingerprint
        extends VBox
        implements ActionListener {

    private VBox fingerprintPane;
    private CaptureThread captureThread;
    private ImagePanel fingerprintImage;
    private final Reader reader;
    private final boolean showImage;

    private CaptureFingerprint(Reader reader, boolean showImage) {
        this.reader = reader;
        this.showImage = showImage;
        captureThread = new CaptureThread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        if (showImage) {
            fingerprintImage = new ImagePanel();
            super.getChildren().add(fingerprintImage);
            super.setPrefWidth(400);
            super.setPrefHeight(400);
        }

        System.out.println("Captura creada");
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
                    if (this.showImage) {
                        // Display image
                        fingerprintImage.showImage(evt.capture_result.image);
                    }
                    byte[] bytes = Base64.getEncoder().encode(evt.capture_result.image.getData());
                    System.out.println("[Data]: " + bytes);
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
                Fingerprint.setFingerprintStatus(0); // Fingerprint Off
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

        fingerprintPane = pane;
        fingerprintPane.getChildren().add(this);
        fingerprintPane.toFront();
        fingerprintPane.setVisible(true);

    }

    public static void Run(Reader reader) { // Run in background
        CaptureFingerprint capture = new CaptureFingerprint(reader, true);
        capture.startCapture();
    }

    public static void Run(Reader reader, VBox pane) {
        CaptureFingerprint capture = new CaptureFingerprint(reader, false);
        capture.startCapture(pane);
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
        }
    }

}