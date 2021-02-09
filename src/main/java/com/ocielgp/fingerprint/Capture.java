package com.ocielgp.fingerprint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import javafx.scene.layout.VBox;

public class Capture
        extends VBox
        implements ActionListener {

    private VBox fingerprintPane;
    private CaptureThread captureThread;
    private final Reader reader;
    private final ImagePanel fingerprintImage;
    private final boolean bStreaming;

    private Capture(Reader reader, boolean bStreaming) {
        this.reader = reader;
        this.bStreaming = bStreaming;

        captureThread = new CaptureThread(this.reader, this.bStreaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);

        fingerprintImage = new ImagePanel();
        super.getChildren().add(fingerprintImage);
        super.setPrefWidth(400);
        super.setPrefHeight(400);

        System.out.println("creado");
    }

    private void StartCaptureThread() {
        captureThread = new CaptureThread(reader, bStreaming, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        captureThread.start(this);
    }

    private void StopCaptureThread() {
        if (null != captureThread) captureThread.cancel();
    }

    private void WaitForCaptureThread() {
        if (null != captureThread) captureThread.join(1000);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("disparado");

        if (e.getActionCommand().equals("Hi")) {
            //event from "back" button
            //cancel capture
            StopCaptureThread();
        } else if (e.getActionCommand().equals(CaptureThread.ACT_CAPTURE)) {
            System.out.println("captura");
            //event from capture thread
            CaptureThread.CaptureEvent evt = (CaptureThread.CaptureEvent) e;
            boolean bCanceled = false;

            if (null != evt.capture_result) {
                if (null != evt.capture_result.image && Reader.CaptureQuality.GOOD == evt.capture_result.quality) {
                    //display image
                    fingerprintImage.showImage(evt.capture_result.image);
                    System.out.println("data; " + evt.capture_result.image.getData());
                } else if (Reader.CaptureQuality.CANCELED == evt.capture_result.quality) {
                    //capture or streaming was canceled, just quit
                    bCanceled = true;
                } else {
                    //bad quality
                    System.out.println(evt.capture_result.quality);
//                    MessageBox.BadQuality(evt.capture_result.quality);
                }
            } else if (null != evt.exception) {
                //exception during capture
                System.out.println("Capture");
                System.out.println(evt.exception);
//                MessageBox.DpError("Capture",  evt.exception);
                bCanceled = true;
            } else if (null != evt.reader_status) {
                System.out.println(evt.reader_status);
//                MessageBox.BadStatus(evt.reader_status);
                bCanceled = true;
            }

            if (!bCanceled) {
                if (!bStreaming) {
                    //restart capture thread
                    WaitForCaptureThread();
                    StartCaptureThread();
                }
            } else {
                //destroy dialog
                fingerprintPane.setVisible(false);
            }
        }
    }

    private void startCapture(VBox dlgParent) {
        //open reader
        try {
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException e) {
            System.out.println("Reader.Open()");
            System.out.println(e);
        }

        boolean bOk = true;
        if (bStreaming) {
            //check if streaming supported
            Reader.Capabilities rc = reader.GetCapabilities();
            if (!rc.can_stream) {
                System.out.println("This reader does not support streaming");
                bOk = false;
            }
        }

        if (bOk) {
            //start capture thread
            StartCaptureThread();
            System.out.println("hola");

            //bring up modal dialog
            fingerprintPane = dlgParent;
            fingerprintPane.getChildren().add(this);
//            m_dlgParent.setContentPane(this);
//            m_dlgParent.pack();
//            m_dlgParent.setLocationRelativeTo(null);
            fingerprintPane.toFront();
            fingerprintPane.setVisible(true);
//            m_dlgParent.dispose();

            //cancel capture
//            StopCaptureThread();

            //wait for capture thread to finish
//            WaitForCaptureThread();
            System.out.println("esperando");
        }

        //close reader
        /*try {
            m_reader.Close();
        } catch (UareUException e) {
//            MessageBox.DpError("Reader.Close()", e);
        }*/
    }

    public static void Run(Reader reader, boolean bStreaming, VBox pane) {
//        JDialog dlg = new JDialog((JDialog) null, "Put your finger on the reader", true);
        Capture capture = new Capture(reader, bStreaming);
        capture.startCapture(pane);
    }
}