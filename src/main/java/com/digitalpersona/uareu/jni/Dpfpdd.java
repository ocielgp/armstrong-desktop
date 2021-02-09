package com.digitalpersona.uareu.jni;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.UareUException;

import java.security.AccessControlException;

import com.digitalpersona.uareu.dpfpdd.FidImpl;
import com.digitalpersona.uareu.Reader;

public class Dpfpdd {
    public native int DpfpddInit();

    public native int DpfpddExit();

    public native int DpfpddQueryDevices(final IntReference p0, final Reader.Description[] p1, final IntReference[] p2, final IntReference[] p3);

    public native int DpfpddOpen(final String p0, final int p1, final LongReference p2);

    public native int DpfpddClose(final long p0);

    public native int DpfpddGetCapabilities(final long p0, final Reader.Capabilities p1);

    public native int DpfpddGetStatus(final long p0, final Reader.Status p1, final IntReference p2);

    public native int DpfpddReset(final long p0);

    public native int DpfpddCalibrate(final long p0);

    public native int DpfpddCapture(final long p0, final int p1, final int p2, final int p3, final int p4, final int p5, final IntReference p6, final IntReference p7, final FidImpl p8);

    public native int DpfpddCaptureCancel(final long p0);

    public native int DpfpddStartStream(final long p0);

    public native int DpfpddStopStream(final long p0);

    public native int DpfpddGetStreamImage(final long p0, final int p1, final int p2, final int p3, final int p4, final IntReference p5, final IntReference p6, final FidImpl p7);

    public Dpfpdd() {
        try {
            if (System.getProperty("java.vendor").equals("The Android Project")) {
                System.loadLibrary("dpuvc");
                System.loadLibrary("dpfpdd");
                System.loadLibrary("dpfpdd5000");
                System.loadLibrary("dpfpdd_tcd58");
                System.loadLibrary("dpfj");
            }
            System.loadLibrary("dpuareu_jni");
        } catch (AccessControlException ex) {
        }
    }

    public void init() throws UareUException {
        synchronized (this) {
            final int result = this.DpfpddInit();
            if (0 != result) {
                throw new UareUException(result);
            }
        }
    }

    public void exit() throws UareUException {
        synchronized (this) {
            final int result = this.DpfpddExit();
            if (0 != result) {
                throw new UareUException(result);
            }
        }
    }

    public Reader.Description[] query_devices() throws UareUException {
        Reader.Description[] descriptions = new Reader.Description[0];
        IntReference[] technologies = new IntReference[0];
        IntReference[] modalities = new IntReference[0];
        final IntReference cnt = new IntReference(0);
        while (true) {
            final int result = this.DpfpddQueryDevices(cnt, descriptions, technologies, modalities);
            if (0 == result) {
                for (int i = 0; i < descriptions.length; ++i) {
                    descriptions[i].technology = toTechnology(technologies[i].value);
                    descriptions[i].modality = toModality(modalities[i].value);
                }
                return descriptions;
            }
            if (0 == result) {
                continue;
            }
            if (96075789 != result) {
                throw new UareUException(result);
            }
            descriptions = new Reader.Description[cnt.value];
            technologies = new IntReference[cnt.value];
            modalities = new IntReference[cnt.value];
            for (int i = 0; i < descriptions.length; ++i) {
                descriptions[i] = new Reader.Description();
                technologies[i] = new IntReference(0);
                modalities[i] = new IntReference(0);
            }
        }
    }

    public long open(final String strReaderName, final Reader.Priority prio) throws UareUException {
        final LongReference hReader = new LongReference(0L);
        final int result = this.DpfpddOpen(strReaderName, fromPriority(prio), hReader);
        if (0 != result) {
            throw new UareUException(result);
        }
        return hReader.value;
    }

    public void close(final long hReader) throws UareUException {
        final int result = this.DpfpddClose(hReader);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public Reader.Capabilities get_capabilities(final long hReader) throws UareUException {
        final Reader.Capabilities caps = new Reader.Capabilities();
        final int result = this.DpfpddGetCapabilities(hReader, caps);
        if (0 != result) {
            throw new UareUException(result);
        }
        return caps;
    }

    public Reader.Status get_status(final long hReader) throws UareUException {
        final Reader.Status status = new Reader.Status();
        final IntReference IntStatus = new IntReference(0);
        final int result = this.DpfpddGetStatus(hReader, status, IntStatus);
        if (0 != result) {
            throw new UareUException(result);
        }
        status.status = toStatus(IntStatus.value);
        return status;
    }

    public void reset(final long hReader) throws UareUException {
        final int result = this.DpfpddReset(hReader);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public void calibrate(final long hReader) throws UareUException {
        final int result = this.DpfpddCalibrate(hReader);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public Reader.CaptureResult capture(final long hReader, final int size_expected, final Fid.Format img_format, final Reader.ImageProcessing img_proc, final int resolution, final int timeout) throws UareUException {
        final IntReference score = new IntReference(0);
        final IntReference quality = new IntReference(0);
        final FidImpl fid = new FidImpl(img_format, 1);
        final int result = this.DpfpddCapture(hReader, size_expected, fromFormat(img_format), fromImageProcessing(img_proc), resolution, timeout, score, quality, fid);
        if (0 != result) {
            throw new UareUException(result);
        }
        final Reader.CaptureResult cres = new Reader.CaptureResult();
        cres.score = score.value;
        cres.quality = toQuality(quality.value);
        if (null != fid.getData() && 0 != fid.getData().length) {
            cres.image = fid;
        }
        return cres;
    }

    public void capture_cancel(final long hReader) throws UareUException {
        final int result = this.DpfpddCaptureCancel(hReader);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public void start_stream(final long hReader) throws UareUException {
        final int result = this.DpfpddStartStream(hReader);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public void stop_stream(final long hReader) throws UareUException {
        final int result = this.DpfpddStopStream(hReader);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public Reader.CaptureResult get_stream_image(final long hReader, final int size_expected, final Fid.Format img_format, final Reader.ImageProcessing img_proc, final int resolution) throws UareUException {
        final IntReference score = new IntReference(0);
        final IntReference quality = new IntReference(0);
        final FidImpl fid = new FidImpl(img_format, 1);
        final int result = this.DpfpddGetStreamImage(hReader, size_expected, fromFormat(img_format), fromImageProcessing(img_proc), resolution, score, quality, fid);
        if (0 != result) {
            throw new UareUException(result);
        }
        final Reader.CaptureResult cres = new Reader.CaptureResult();
        cres.score = score.value;
        cres.quality = toQuality(quality.value);
        if (null != fid.getData() && 0 != fid.getData().length) {
            cres.image = fid;
        }
        return cres;
    }

    private static Reader.Technology toTechnology(final int n) {
        switch (n) {
            case 0: {
                return Reader.Technology.HW_TECHNOLOGY_UNKNOWN;
            }
            case 1: {
                return Reader.Technology.HW_TECHNOLOGY_OPTICAL;
            }
            case 2: {
                return Reader.Technology.HW_TECHNOLOGY_CAPACITIVE;
            }
            case 3: {
                return Reader.Technology.HW_TECHNOLOGY_THERMAL;
            }
            case 4: {
                return Reader.Technology.HW_TECHNOLOGY_PRESSURE;
            }
            default: {
                return Reader.Technology.HW_TECHNOLOGY_UNKNOWN;
            }
        }
    }

    private static Reader.Modality toModality(final int n) {
        switch (n) {
            case 0: {
                return Reader.Modality.HW_MODALITY_UNKNOWN;
            }
            case 1: {
                return Reader.Modality.HW_MODALITY_SWIPE;
            }
            case 2: {
                return Reader.Modality.HW_MODALITY_AREA;
            }
            default: {
                return Reader.Modality.HW_MODALITY_UNKNOWN;
            }
        }
    }

    private static Reader.ReaderStatus toStatus(final int n) {
        switch (n) {
            case 0: {
                return Reader.ReaderStatus.READY;
            }
            case 1: {
                return Reader.ReaderStatus.BUSY;
            }
            case 2: {
                return Reader.ReaderStatus.NEED_CALIBRATION;
            }
            case 3: {
                return Reader.ReaderStatus.FAILURE;
            }
            default: {
                return Reader.ReaderStatus.FAILURE;
            }
        }
    }

    private static int fromFormat(final Fid.Format fmt) {
        switch (fmt) {
            case ANSI_381_2004: {
                return 1770497;
            }
            case ISO_19794_4_2005: {
                return 16842759;
            }
            default: {
                return 0;
            }
        }
    }

    private static int fromImageProcessing(final Reader.ImageProcessing proc) {
        switch (proc) {
            case IMG_PROC_DEFAULT: {
                return 0;
            }
            case IMG_PROC_PIV: {
                return 1;
            }
            case IMG_PROC_ENHANCED: {
                return 2;
            }
            case IMG_PROC_UNPROCESSED: {
                return 1382119241;
            }
            default: {
                return 0;
            }
        }
    }

    private static int fromPriority(final Reader.Priority prio) {
        switch (prio) {
            case COOPERATIVE: {
                return 2;
            }
            case EXCLUSIVE: {
                return 4;
            }
            default: {
                return 2;
            }
        }
    }

    private static Reader.CaptureQuality toQuality(final int n) {
        switch (n) {
            case 0: {
                return Reader.CaptureQuality.GOOD;
            }
            case 1: {
                return Reader.CaptureQuality.TIMED_OUT;
            }
            case 2: {
                return Reader.CaptureQuality.CANCELED;
            }
            case 4: {
                return Reader.CaptureQuality.NO_FINGER;
            }
            case 8: {
                return Reader.CaptureQuality.FAKE_FINGER;
            }
            case 16: {
                return Reader.CaptureQuality.FINGER_TOO_LEFT;
            }
            case 32: {
                return Reader.CaptureQuality.FINGER_TOO_RIGHT;
            }
            case 64: {
                return Reader.CaptureQuality.FINGER_TOO_HIGH;
            }
            case 128: {
                return Reader.CaptureQuality.FINGER_TOO_LOW;
            }
            case 256: {
                return Reader.CaptureQuality.FINGER_OFF_CENTER;
            }
            case 512: {
                return Reader.CaptureQuality.SCAN_SKEWED;
            }
            case 1024: {
                return Reader.CaptureQuality.SCAN_TOO_SHORT;
            }
            case 2048: {
                return Reader.CaptureQuality.SCAN_TOO_LONG;
            }
            case 4096: {
                return Reader.CaptureQuality.SCAN_TOO_SLOW;
            }
            case 8192: {
                return Reader.CaptureQuality.SCAN_TOO_FAST;
            }
            case 16384: {
                return Reader.CaptureQuality.SCAN_WRONG_DIRECTION;
            }
            case 32768: {
                return Reader.CaptureQuality.READER_DIRTY;
            }
            default: {
                return Reader.CaptureQuality.NO_FINGER;
            }
        }
    }

    private class IntReference {
        protected int value;

        protected IntReference(final int n) {
            this.value = n;
        }
    }

    private class LongReference {
        protected long value;

        protected LongReference(final long n) {
            this.value = n;
        }
    }
}