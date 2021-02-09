package com.digitalpersona.uareu.jni;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.UareUException;

import java.security.AccessControlException;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.dpfpdd.FidImpl;
import com.digitalpersona.uareu.dpfj.FmdImpl;
import com.digitalpersona.uareu.Fmd;

public class Dpfj {
    private Fmd.Format m_enrollment_fmd_format;

    public native int DpfjSelectEngine(final int p0);

    public native int DpfjImportFmd(final int p0, final byte[] p1, final int p2, final FmdImpl p3);

    public native int DpfjImportDpFid(final byte[] p0, final int p1, final int p2, final int p3, final FidImpl p4);

    public native int DpfjImportFid(final byte[] p0, final int p1, final FidImpl p2);

    public native int DpfjImportRaw(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final FidImpl p9);

    public native int DpfjCreateFmdFromRaw(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final FmdImpl p7);

    public native int DpfjCreateFmdFromFid(final byte[] p0, final int p1, final int p2, final int p3, final FmdImpl p4);

    public native int DpfjCompare(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final int p5, final IntReference p6);

    public native int DpfjIdentify(final byte[] p0, final int p1, final int p2, final byte[][] p3, final int p4, final int p5, final IntReference p6, final Engine.Candidate[] p7);

    public native int DpfjStartEnrollment(final int p0);

    public native int DpfjAddToEnrollment(final byte[] p0, final int p1, final int p2);

    public native int DpfjCreateEnrollmentFmd(final int p0, final FmdImpl p1);

    public native int DpfjFinishEnrollment();

    public Dpfj() {
        try {
            if (System.getProperty("java.vendor").equals("The Android Project")) {
                System.loadLibrary("dpfj");
            }
            System.loadLibrary("dpuareu_jni");
        } catch (AccessControlException ex) {
        }
    }

    public void select_engine(final Engine.EngineType engine) throws UareUException {
        int nEngine = -1;
        switch (engine) {
            case ENGINE_DPFJ: {
                nEngine = 0;
                break;
            }
            case ENGINE_INNOVATRICS_ANSIISO: {
                nEngine = 1;
                break;
            }
        }
        final int result = this.DpfjSelectEngine(nEngine);
        if (0 != result) {
            throw new UareUException(result);
        }
    }

    public Fmd import_fmd(final byte[] fmd_data, final Fmd.Format fmd_format, final Fmd.Format fmd_format_to) throws UareUException {
        int nViewCnt = 0;
        switch (fmd_format) {
            case ANSI_378_2004: {
                if (0 == fmd_data[8] && 0 == fmd_data[9]) {
                    nViewCnt = fmd_data[28];
                    break;
                }
                nViewCnt = fmd_data[24];
                break;
            }
            case ISO_19794_2_2005: {
                nViewCnt = fmd_data[22];
                break;
            }
            case DP_PRE_REG_FEATURES:
            case DP_REG_FEATURES:
            case DP_VER_FEATURES: {
                nViewCnt = 1;
                break;
            }
        }
        if (0 == nViewCnt) {
            throw new UareUException(96075977);
        }
        final int fmd_fmt = fromFmdFormat(fmd_format);
        final int fmd_fmt_to = fromFmdFormat(fmd_format_to);
        final FmdImpl fmd_to = new FmdImpl(fmd_format_to, nViewCnt);
        final int result = this.DpfjImportFmd(fmd_fmt, fmd_data, fmd_fmt_to, fmd_to);
        if (0 != result) {
            throw new UareUException(result);
        }
        return fmd_to;
    }

    public Fid import_raw(final byte[] data, final int width, final int height, final int dpi, final int finger_position, final int cbeff_id, final Fid.Format fid_format_to, final int fid_dpi, final boolean rotate180) throws UareUException {
        final int fid_fmt_to = fromFidFormat(fid_format_to);
        final FidImpl fid = new FidImpl(fid_format_to, 1);
        final int result = this.DpfjImportRaw(data, width, height, dpi, finger_position, cbeff_id, fid_fmt_to, fid_dpi, rotate180 ? 1 : 0, fid);
        if (0 != result) {
            throw new UareUException(result);
        }
        return fid;
    }

    public Fid import_dp_fid(final byte[] data, final Fid.Format fid_format_to, final int fid_dpi, final boolean rotate180) throws UareUException {
        final int fid_fmt_to = fromFidFormat(fid_format_to);
        final FidImpl fid = new FidImpl(fid_format_to, 1);
        final int result = this.DpfjImportDpFid(data, fid_fmt_to, fid_dpi, rotate180 ? 1 : 0, fid);
        if (0 != result) {
            throw new UareUException(result);
        }
        return fid;
    }

    public Fid import_fid(final byte[] data, final Fid.Format format) throws UareUException {
        int nViewCnt = 0;
        switch (format) {
            case ANSI_381_2004: {
                nViewCnt = data[22];
                break;
            }
            case ISO_19794_4_2005: {
                nViewCnt = data[18];
                break;
            }
        }
        if (0 == nViewCnt) {
            throw new UareUException(96075877);
        }
        final int fid_fmt = fromFidFormat(format);
        final FidImpl fid = new FidImpl(format, nViewCnt);
        final int result = this.DpfjImportFid(data, fid_fmt, fid);
        if (0 != result) {
            throw new UareUException(result);
        }
        return fid;
    }

    public Fmd create_fmd_from_raw(final byte[] data, final int width, final int height, final int dpi, final int finger_position, final int cbeff_id, final Fmd.Format fmd_format) throws UareUException {
        final int format = fromFmdFormat(fmd_format);
        final FmdImpl fmd = new FmdImpl(fmd_format, 1);
        final int result = this.DpfjCreateFmdFromRaw(data, width, height, dpi, finger_position, cbeff_id, format, fmd);
        if (0 != result) {
            throw new UareUException(result);
        }
        return fmd;
    }

    public Fmd create_fmd_from_fid(final Fid fid, final Fmd.Format fmd_format) throws UareUException {
        final int fmd_fmt = fromFmdFormat(fmd_format);
        final int fid_fmt = fromFidFormat(fid.getFormat());
        final int view_cnt = fid.getViews().length;
        final FmdImpl fmd = new FmdImpl(fmd_format, fid.getViews().length);
        final int result = this.DpfjCreateFmdFromFid(fid.getData(), fid_fmt, view_cnt, fmd_fmt, fmd);
        if (0 != result) {
            throw new UareUException(result);
        }
        return fmd;
    }

    public int compare(final Fmd fmd1, final int view_index1, final Fmd fmd2, final int view_index2) throws UareUException {
        final int fmt1 = fromFmdFormat(fmd1.getFormat());
        final int fmt2 = fromFmdFormat(fmd2.getFormat());
        final IntReference objScore = new IntReference(0);
        final int result = this.DpfjCompare(fmd1.getData(), fmt1, view_index1, fmd2.getData(), fmt2, view_index2, objScore);
        if (0 != result) {
            throw new UareUException(result);
        }
        return objScore.value;
    }

    public Engine.Candidate[] identify(final Fmd fmd1, final int view_index1, final Fmd[] fmds, final int threshold, final int candidates_req) throws UareUException {
        final int fmt1 = fromFmdFormat(fmd1.getFormat());
        byte[][] fmds_data = null;
        int fmt2 = -1;
        if (null != fmds && 0 != fmds.length) {
            fmds_data = new byte[fmds.length][];
            for (int i = 0; i < fmds.length; ++i) {
                fmds_data[i] = fmds[i].getData();
            }
            fmt2 = fromFmdFormat(fmds[0].getFormat());
        }
        Engine.Candidate[] candidates = new Engine.Candidate[candidates_req];
        for (int j = 0; j < candidates_req; ++j) {
            candidates[j] = new Engine.Candidate();
        }
        final IntReference objCandidatesReq = new IntReference(candidates_req);
        final int result = this.DpfjIdentify(fmd1.getData(), fmt1, view_index1, fmds_data, fmt2, threshold, objCandidatesReq, candidates);
        if (0 != result) {
            throw new UareUException(result);
        }
        final Engine.Candidate[] candidates_res = new Engine.Candidate[objCandidatesReq.value];
        for (int k = 0; k < objCandidatesReq.value; ++k) {
            candidates_res[k] = candidates[k];
        }
        candidates = null;
        return candidates_res;
    }

    public void start_enrollment(final Fmd.Format fmd_format) throws UareUException {
        final int fmt = fromFmdFormat(fmd_format);
        final int result = this.DpfjStartEnrollment(fmt);
        if (0 != result) {
            throw new UareUException(result);
        }
        this.m_enrollment_fmd_format = fmd_format;
    }

    public boolean add_to_enrollment(final Fmd fmd, final int view_index) throws UareUException {
        final int fmt = fromFmdFormat(fmd.getFormat());
        final int result = this.DpfjAddToEnrollment(fmd.getData(), fmt, view_index);
        if (0 != result && 96075789 != result) {
            throw new UareUException(result);
        }
        return 96075789 != result;
    }

    public Fmd create_enrollment_fmd() throws UareUException {
        final FmdImpl fmd = new FmdImpl(this.m_enrollment_fmd_format, 1);
        final int fmt = fromFmdFormat(this.m_enrollment_fmd_format);
        final int result = this.DpfjCreateEnrollmentFmd(fmt, fmd);
        if (0 != result) {
            throw new UareUException(result);
        }
        return fmd;
    }

    public void finish_enrollment() throws UareUException {
        final int result = this.DpfjFinishEnrollment();
        if (0 != result) {
            throw new UareUException(result);
        }
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

    private static int fromFmdFormat(final Fmd.Format fmt) {
        switch (fmt) {
            case ANSI_378_2004: {
                return 1769473;
            }
            case ISO_19794_2_2005: {
                return 16842753;
            }
            case DP_PRE_REG_FEATURES: {
                return 0;
            }
            case DP_REG_FEATURES: {
                return 1;
            }
            case DP_VER_FEATURES: {
                return 2;
            }
            default: {
                return -1;
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