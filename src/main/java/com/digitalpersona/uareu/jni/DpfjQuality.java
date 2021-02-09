package com.digitalpersona.uareu.jni;

import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.Quality;
import com.digitalpersona.uareu.Fid;

import java.security.AccessControlException;

public class DpfjQuality {
    public native int DpfjQ_NfiqFid(final byte[] p0, final int p1, final int p2, final int p3, final IntReference p4);

    public native int DpfjQ_NfiqRaw(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final IntReference p6);

    public DpfjQuality() {
        try {
            if (System.getProperty("java.vendor").equals("The Android Project")) {
                System.loadLibrary("dpfj");
            }
            System.loadLibrary("dpuareu_jni");
        } catch (AccessControlException ex) {
        }
    }

    public int nfiq_fid(final Fid fid, final int view_index, final Quality.QualityAlgorithm quality_alg) throws UareUException {
        final int fid_fmt = fromFidFormat(fid.getFormat());
        final int alg = fromAlgType(quality_alg);
        final IntReference outNfiq = new IntReference(0);
        final int result = this.DpfjQ_NfiqFid(fid.getData(), fid_fmt, view_index, alg, outNfiq);
        if (0 != result) {
            throw new UareUException(result);
        }
        return outNfiq.value;
    }

    public int nfiq_raw(final byte[] data, final int width, final int height, final int dpi, final int bpp, final Quality.QualityAlgorithm quality_alg) throws UareUException {
        final int alg = fromAlgType(quality_alg);
        final IntReference outNfiq = new IntReference(0);
        final int result = this.DpfjQ_NfiqRaw(data, width, height, dpi, bpp, alg, outNfiq);
        if (0 != result) {
            throw new UareUException(result);
        }
        return outNfiq.value;
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

    private static int fromAlgType(final Quality.QualityAlgorithm alg) {
        switch (alg) {
            case QUALITY_NFIQ_NIST: {
                return 1;
            }
            case QUALITY_NFIQ_AWARE: {
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
}