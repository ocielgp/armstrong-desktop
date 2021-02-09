package com.ocielgp.fingerprint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;

public class CaptureBackground
        implements ActionListener {

    private CaptureThread captureThread;
    private final Reader reader;

    private CaptureBackground(Reader reader) {
        this.reader = reader;
        captureThread = new CaptureThread(this.reader, false, Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT);
        System.out.println("creado");

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
                    System.out.println(evt.capture_result.image);
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
                //restart capture thread
                WaitForCaptureThread();
                StartCaptureThread();

            }
        }
    }

    private void startCapture() {
        //open reader
        try {
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException e) {
            System.out.println("Reader.Open()");
            System.out.println(e);
        }

        //start capture thread
        StartCaptureThread();
        System.out.println("hola");

        //cancel capture
//            StopCaptureThread();

        //wait for capture thread to finish
//        WaitForCaptureThread();
//        System.out.println("esperando");

        //close reader
        /*try {
            m_reader.Close();
        } catch (UareUException e) {
//            MessageBox.DpError("Reader.Close()", e);
        }*/
    }

    public static void Run(Reader reader) {
        CaptureBackground capture = new CaptureBackground(reader);
        capture.startCapture();
    }
}