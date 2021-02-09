package com.digitalpersona.uareu.dpfpdd;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.UareUException;

import java.util.Vector;

import com.digitalpersona.uareu.jni.Dpfpdd;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.Reader;

import java.util.AbstractList;

public class ReaderCollectionImpl extends AbstractList<Reader> implements ReaderCollection {
    private final Dpfpdd m_dpfpdd;
    private boolean m_isInitialized;
    Vector<ReaderImpl> m_readers;

    public ReaderCollectionImpl() throws UareUException {
        this.m_dpfpdd = new Dpfpdd();
        this.m_readers = new Vector<ReaderImpl>(4, 2);
        this.m_isInitialized = false;
    }

    @Override
    public int size() {
        return this.m_readers.size();
    }

    @Override
    public Reader get(final int n) {
        return this.m_readers.get(n);
    }

    @Override
    public void GetReaders() throws UareUException {
        synchronized (this) {
            final Reader.Description[] descriptions = this.m_dpfpdd.query_devices();
            for (int ridx = 0; ridx < this.m_readers.size(); ++ridx) {
                final ReaderImpl reader = this.m_readers.get(ridx);
                boolean bFound = false;
                for (int didx = 0; didx < descriptions.length; ++didx) {
                    if (reader.GetDescription().name.equals(descriptions[didx].name)) {
                        bFound = true;
                        break;
                    }
                }
                if (!bFound) {
                    this.m_readers.remove(ridx);
                    --ridx;
                }
            }
            for (int didx2 = 0; didx2 < descriptions.length; ++didx2) {
                boolean bFound2 = false;
                for (int ridx2 = 0; ridx2 < this.m_readers.size(); ++ridx2) {
                    if (descriptions[didx2].name.equals(this.m_readers.get(ridx2).GetDescription().name)) {
                        bFound2 = true;
                        break;
                    }
                }
                if (!bFound2) {
                    final ReaderImpl reader2 = new ReaderImpl(descriptions[didx2]);
                    this.m_readers.add(reader2);
                }
            }
        }
    }

    public void Initialize() throws UareUException {
        synchronized (this) {
            if (!this.m_isInitialized) {
                this.m_dpfpdd.init();
                this.m_isInitialized = true;
            }
        }
    }

    public void Release() throws UareUException {
        synchronized (this) {
            if (this.m_isInitialized) {
                this.m_isInitialized = false;
                this.m_dpfpdd.exit();
            }
        }
    }

    private class ReaderImpl implements Reader {
        private Description m_descr;
        private Capabilities m_caps;
        private long m_hReader;
        private int m_nImageSize;

        public ReaderImpl(final Description descr) {
            this.m_descr = descr;
            this.m_nImageSize = 0;
        }

        @Override
        public void Open(final Priority priority) throws UareUException {
            synchronized (this) {
                this.m_hReader = ReaderCollectionImpl.this.m_dpfpdd.open(this.m_descr.name, priority);
                this.m_caps = ReaderCollectionImpl.this.m_dpfpdd.get_capabilities(this.m_hReader);
            }
        }

        @Override
        public void Close() throws UareUException {
            synchronized (this) {
                final long hReader = this.m_hReader;
                this.m_hReader = 0L;
                this.m_caps = null;
                ReaderCollectionImpl.this.m_dpfpdd.close(hReader);
            }
        }

        @Override
        public Status GetStatus() throws UareUException {
            return ReaderCollectionImpl.this.m_dpfpdd.get_status(this.m_hReader);
        }

        @Override
        public Capabilities GetCapabilities() {
            return this.m_caps;
        }

        @Override
        public Description GetDescription() {
            return this.m_descr;
        }

        @Override
        public CaptureResult Capture(final Fid.Format img_format, final ImageProcessing img_proc, final int resolution, final int timeout) throws UareUException {
            final CaptureResult result = ReaderCollectionImpl.this.m_dpfpdd.capture(this.m_hReader, this.m_nImageSize, img_format, img_proc, resolution, timeout);
            if (null != result.image) {
                this.m_nImageSize = result.image.getData().length;
            }
            return result;
        }

        @Override
        public void CaptureAsync(final Fid.Format format, final ImageProcessing img_proc, final int resolution, final int timeout, final CaptureCallback capture_callback) throws UareUException {
            final Runnable r = new CaptureThread(format, img_proc, resolution, timeout, capture_callback);
            new Thread(r).start();
        }

        @Override
        public void CancelCapture() throws UareUException {
            ReaderCollectionImpl.this.m_dpfpdd.capture_cancel(this.m_hReader);
        }

        @Override
        public void StartStreaming() throws UareUException {
            ReaderCollectionImpl.this.m_dpfpdd.start_stream(this.m_hReader);
        }

        @Override
        public void StopStreaming() throws UareUException {
            ReaderCollectionImpl.this.m_dpfpdd.stop_stream(this.m_hReader);
        }

        @Override
        public CaptureResult GetStreamImage(final Fid.Format img_format, final ImageProcessing img_proc, final int resolution) throws UareUException {
            final CaptureResult result = ReaderCollectionImpl.this.m_dpfpdd.get_stream_image(this.m_hReader, this.m_nImageSize, img_format, img_proc, resolution);
            if (null != result.image) {
                this.m_nImageSize = result.image.getData().length;
            }
            return result;
        }

        @Override
        public void Calibrate() throws UareUException {
            ReaderCollectionImpl.this.m_dpfpdd.calibrate(this.m_hReader);
        }

        @Override
        public void Reset() throws UareUException {
            ReaderCollectionImpl.this.m_dpfpdd.reset(this.m_hReader);
        }

        public class CaptureThread implements Runnable {
            Fid.Format m_format;
            ImageProcessing m_image_processing;
            int m_resolution;
            int m_timeout;
            CaptureCallback m_capture_callback;

            public CaptureThread(final Fid.Format format, final ImageProcessing img_proc, final int resolution, final int timeout, final CaptureCallback capture_callback) {
                this.m_format = format;
                this.m_image_processing = img_proc;
                this.m_resolution = resolution;
                this.m_timeout = timeout;
                this.m_capture_callback = capture_callback;
            }

            @Override
            public void run() {
                Fid.Format img_format = Fid.Format.ANSI_381_2004;
                switch (this.m_format) {
                    case ANSI_381_2004: {
                        img_format = Fid.Format.ANSI_381_2004;
                        break;
                    }
                    case ISO_19794_4_2005: {
                        img_format = Fid.Format.ISO_19794_4_2005;
                        break;
                    }
                }
                UareUException ex = null;
                try {
                    boolean bReady = false;
                    while (!bReady) {
                        final CaptureResult result = ReaderCollectionImpl.this.m_dpfpdd.capture(ReaderImpl.this.m_hReader, ReaderImpl.this.m_nImageSize, img_format, this.m_image_processing, this.m_resolution, this.m_timeout);
                        if (null != result.image) {
                            ReaderImpl.this.m_nImageSize = result.image.getData().length;
                        }
                        if (null == result) {
                            break;
                        }
                        if (null == result.image) {
                            break;
                        }
                        bReady = true;
                        this.m_capture_callback.CaptureResultEvent(result);
                    }
                } catch (UareUException e) {
                    ex = e;
                }
                if (null != ex) {
                    this.m_capture_callback.CaptureResultEvent(null);
                }
            }
        }
    }
}