package com.ocielgp.fingerprint;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;
import javafx.application.Platform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Fingerprint_Capture_Thread extends Thread {
    public static final String ACT_CAPTURE = "capture_thread_captured";

    public static class CaptureEvent extends ActionEvent {
        private static final long serialVersionUID = 101;

        public Reader.CaptureResult capture_result;
        public Reader.Status reader_status;
        public UareUException exception;

        public CaptureEvent(Object source, String action, Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
            super(source, ActionEvent.ACTION_PERFORMED, action);

            capture_result = cr;
            reader_status = st;
            exception = ex;
        }
    }

    private ActionListener listener;
    private boolean bCancel;
    private final Reader reader;
    private final boolean bStream;
    private final Fid.Format format;
    private final Reader.ImageProcessing proc;
    private CaptureEvent last_capture;

    public Fingerprint_Capture_Thread(Reader reader, boolean bStream, Fid.Format img_format, Reader.ImageProcessing img_proc) {
        bCancel = false;
        this.reader = reader;
        this.bStream = bStream;
        format = img_format;
        proc = img_proc;
    }

    public void start(ActionListener listener) {
        this.listener = listener;
        super.start();
    }

    public void join(int milliseconds) {
        try {
            super.join(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public CaptureEvent getLastCaptureEvent() {
        return last_capture;
    }

    private void Capture() {
        try {
            // wait for reader to become ready
            boolean bReady = false;
            while (!bReady && !bCancel) {
                Reader.Status rs = reader.GetStatus();
                if (Reader.ReaderStatus.BUSY == rs.status) {
                    // if busy, wait a bit
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                } else if (Reader.ReaderStatus.READY == rs.status || Reader.ReaderStatus.NEED_CALIBRATION == rs.status) {
                    // ready for capture
                    bReady = true;
                    break;
                } else {
                    // reader failure
                    NotifyListener(null, rs, null);
                    break;
                }
            }

            if (bCancel) {
                Reader.CaptureResult cr = new Reader.CaptureResult();
                cr.quality = Reader.CaptureQuality.CANCELED;
                NotifyListener(cr, null, null);
            }


            if (bReady) {
                // fingerprint_Capture
                Reader.CaptureResult cr = reader.Capture(format, proc, 500, -1);
                NotifyListener(cr, null, null);
            }
        } catch (UareUException e) {
            NotifyListener(null, null, e);
        }
    }

    private void Stream() {
        try {
            // wait for reader to become ready
            boolean bReady = false;
            while (!bReady && !bCancel) {
                Reader.Status rs = reader.GetStatus();
                if (Reader.ReaderStatus.BUSY == rs.status) {
                    // if busy, wait a bit
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                } else if (Reader.ReaderStatus.READY == rs.status || Reader.ReaderStatus.NEED_CALIBRATION == rs.status) {
                    // ready for capture
                    bReady = true;
                    break;
                } else {
                    // reader failure
                    NotifyListener(null, rs, null);
                    break;
                }
            }

            if (bReady) {
                // start streaming
                reader.StartStreaming();

                // get images
                while (!bCancel) {
                    Reader.CaptureResult cr = reader.GetStreamImage(format, proc, 500);
                    NotifyListener(cr, null, null);
                }

                // stop streaming
                reader.StopStreaming();
            }
        } catch (UareUException e) {
            NotifyListener(null, null, e);
        }

        if (bCancel) {
            Reader.CaptureResult cr = new Reader.CaptureResult();
            cr.quality = Reader.CaptureQuality.CANCELED;
            NotifyListener(cr, null, null);
        }
    }

    private void NotifyListener(Reader.CaptureResult cr, Reader.Status st, UareUException ex) {
        final CaptureEvent evt = new CaptureEvent(this, Fingerprint_Capture_Thread.ACT_CAPTURE, cr, st, ex);

        // store last capture event
        last_capture = evt;

        if (listener == null) return;

        // invoke listener on EDT thread
        Platform.runLater(() -> listener.actionPerformed(evt));
    }

    public void cancel() {
        bCancel = true;
        try {
            if (!bStream) reader.CancelCapture();
        } catch (UareUException ignored) {
        }
    }

    public void run() {
        if (bStream) {
            Stream();
        } else {
            Capture();
        }
    }
}
