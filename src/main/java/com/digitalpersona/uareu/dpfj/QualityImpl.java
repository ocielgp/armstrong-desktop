package com.digitalpersona.uareu.dpfj;

import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.jni.DpfjQuality;
import com.digitalpersona.uareu.Quality;

public class QualityImpl implements Quality {
    private final DpfjQuality m_dpfjq;

    public QualityImpl() {
        this.m_dpfjq = new DpfjQuality();
    }

    @Override
    public int NfiqFid(final Fid fid, final int view_index, final QualityAlgorithm quality_alg) throws UareUException {
        return this.m_dpfjq.nfiq_fid(fid, view_index, quality_alg);
    }

    @Override
    public int NfiqRaw(final byte[] data, final int width, final int height, final int dpi, final int bpp, final QualityAlgorithm quality_alg) throws UareUException {
        return this.m_dpfjq.nfiq_raw(data, width, height, dpi, bpp, quality_alg);
    }
}