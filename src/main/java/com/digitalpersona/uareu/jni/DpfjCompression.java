package com.digitalpersona.uareu.jni;

import com.digitalpersona.uareu.Compression;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.UareUException;

import java.security.AccessControlException;

import com.digitalpersona.uareu.dpfpdd.FidImpl;

public class DpfjCompression {
    public native int DpfjC_StartCompression();

    public native int DpfjC_FinishCompression();

    public native int DpfjC_SetWsqBitrate(final int p0, final int p1);

    public native int DpfjC_SetWsqSize(final int p0, final int p1);

    public native int DpfjC_CompressFid(final byte[] p0, final int p1, final int p2, final FidImpl p3);

    public native int DpfjC_CompressRaw(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final ByteArrayReference p6);

    public native int DpfjC_ExpandFid(final byte[] p0, final int p1, final int p2, final FidImpl p3);

    public native int DpfjC_ExpandRaw(final byte[] p0, final int p1, final IntReference p2, final IntReference p3, final IntReference p4, final IntReference p5, final ByteArrayReference p6);

    public DpfjCompression() {
        try {
            if (System.getProperty("java.vendor").equals("The Android Project")) {
                System.loadLibrary("dpfj");
            }
            System.loadLibrary("dpuareu_jni");
        } catch (AccessControlException ex) {
        }
    }

    public void start() throws UareUException {
        final int result = this.DpfjC_StartCompression();
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public void finish() throws UareUException {
        final int result = this.DpfjC_FinishCompression();
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public void set_wsq_bitrate(final int bitrate_x100, final int tolerance_aw) throws UareUException {
        final int result = this.DpfjC_SetWsqBitrate(bitrate_x100, tolerance_aw);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public void set_wsq_size(final int size, final int tolerance_aw) throws UareUException {
        final int result = this.DpfjC_SetWsqSize(size, tolerance_aw);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public Fid compress_fid(final Fid fid, final Compression.CompressionAlgorithm compression_alg) throws UareUException {
        final int fid_fmt = fromFidFormat(fid.getFormat());
        final int alg = fromAlgType(compression_alg);
        final FidImpl out_fid = new FidImpl(fid.getFormat(), fid.getViews().length);
        final int result = this.DpfjC_CompressFid(fid.getData(), fid_fmt, alg, out_fid);
        if (0 != result) {
            throw new UareUException(result);
        }
        return out_fid;
    }

    public byte[] compress_raw(final byte[] data, final int width, final int height, final int dpi, final int bpp, final Compression.CompressionAlgorithm compression_alg) throws UareUException {
        final int alg = fromAlgType(compression_alg);
        final ByteArrayReference compressed = new ByteArrayReference();
        final int result = this.DpfjC_CompressRaw(data, width, height, dpi, bpp, alg, compressed);
        if (0 != result) {
            throw new UareUException(result);
        }
        final byte[] compressed_data = new byte[compressed.data.length];
        System.arraycopy(compressed.data, 0, compressed_data, 0, compressed.data.length);
        return compressed_data;
    }

    public Fid expand_fid(final Fid fid, final Compression.CompressionAlgorithm compression_alg) throws UareUException {
        final int fid_fmt = fromFidFormat(fid.getFormat());
        final int alg = fromAlgType(compression_alg);
        final FidImpl out_fid = new FidImpl(fid.getFormat(), fid.getViews().length);
        final int result = this.DpfjC_ExpandFid(fid.getData(), fid_fmt, alg, out_fid);
        if (0 != result) {
            throw new UareUException(result);
        }
        return out_fid;
    }

    public Compression.RawImage expand_raw(final byte[] data, final Compression.CompressionAlgorithm compression_alg) throws UareUException {
        final int alg = fromAlgType(compression_alg);
        final IntReference objWidth = new IntReference(0);
        final IntReference objHeight = new IntReference(0);
        final IntReference objDpi = new IntReference(0);
        final IntReference objBpp = new IntReference(0);
        final ByteArrayReference expanded = new ByteArrayReference();
        final int result = this.DpfjC_ExpandRaw(data, alg, objWidth, objHeight, objDpi, objBpp, expanded);
        if (0 != result) {
            throw new UareUException(result);
        }
        final Compression.RawImage img = new Compression.RawImage(objWidth.value, objHeight.value, objDpi.value, objBpp.value, expanded.data);
        return img;
    }

    private static int fromFidFormat(final Fid.Format fmt) {
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

    private static int fromAlgType(final Compression.CompressionAlgorithm alg) {
        switch (alg) {
            case COMPRESSION_WSQ_NIST: {
                return 1;
            }
            case COMPRESSION_WSQ_AWARE: {
                return 2;
            }
            default: {
                return 0;
            }
        }
    }

    private class IntReference {
        protected int value;

        protected IntReference(final int n) {
            this.value = n;
        }
    }

    private class ByteArrayReference {
        protected byte[] data;

        protected ByteArrayReference() {
            this.data = new byte[0];
        }
    }
}