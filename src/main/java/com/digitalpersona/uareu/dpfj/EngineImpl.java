package com.digitalpersona.uareu.dpfj;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.jni.Dpfj;
import com.digitalpersona.uareu.Engine;

public class EngineImpl implements Engine {
    private final Dpfj m_dpfj;

    public EngineImpl() {
        this.m_dpfj = new Dpfj();
    }

    @Override
    public void SelectEngine(final EngineType engine) throws UareUException {
        this.m_dpfj.select_engine(engine);
    }

    @Override
    public Fmd CreateFmd(final Fid fid, final Fmd.Format format) throws UareUException {
        return this.m_dpfj.create_fmd_from_fid(fid, format);
    }

    @Override
    public Fmd CreateFmd(final byte[] data, final int width, final int height, final int resolution, final int finger_position, final int cbeff_id, final Fmd.Format format) throws UareUException {
        return this.m_dpfj.create_fmd_from_raw(data, width, height, resolution, finger_position, cbeff_id, format);
    }

    @Override
    public int Compare(final Fmd fmd1, final int view_index1, final Fmd fmd2, final int view_index2) throws UareUException {
        return this.m_dpfj.compare(fmd1, view_index1, fmd2, view_index2);
    }

    @Override
    public Candidate[] Identify(final Fmd fmd1, final int view_index1, final Fmd[] fmds, final int threshold_score, final int candidates_requested) throws UareUException {
        return this.m_dpfj.identify(fmd1, view_index1, fmds, threshold_score, candidates_requested);
    }

    @Override
    public Fmd CreateEnrollmentFmd(final Fmd.Format format, final EnrollmentCallback enrollment_callback) throws UareUException {
        Fmd.Format pre_format = Fmd.Format.ANSI_378_2004;
        switch (format) {
            case ANSI_378_2004: {
                pre_format = Fmd.Format.ANSI_378_2004;
                break;
            }
            case ISO_19794_2_2005: {
                pre_format = Fmd.Format.ISO_19794_2_2005;
                break;
            }
            default: {
                pre_format = Fmd.Format.DP_PRE_REG_FEATURES;
                break;
            }
        }
        this.m_dpfj.start_enrollment(format);
        UareUException ex = null;
        Fmd fmd = null;
        try {
            boolean bReady;
            PreEnrollmentFmd pre_fmd;
            for (bReady = false; !bReady; bReady = this.m_dpfj.add_to_enrollment(pre_fmd.fmd, pre_fmd.view_index)) {
                pre_fmd = enrollment_callback.GetFmd(pre_format);
                if (null == pre_fmd) {
                    break;
                }
                if (null == pre_fmd.fmd) {
                    break;
                }
            }
            if (bReady) {
                fmd = this.m_dpfj.create_enrollment_fmd();
            }
        } catch (UareUException e) {
            ex = e;
        }
        this.m_dpfj.finish_enrollment();
        if (null != ex) {
            throw ex;
        }
        return fmd;
    }
}