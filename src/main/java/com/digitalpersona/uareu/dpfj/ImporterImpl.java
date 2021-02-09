package com.digitalpersona.uareu.dpfj;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.jni.Dpfj;
import com.digitalpersona.uareu.Importer;

public class ImporterImpl implements Importer {
    private final Dpfj m_dpfj;

    public ImporterImpl() {
        this.m_dpfj = new Dpfj();
    }

    @Override
    public Fid ImportRaw(final byte[] data, final int width, final int height, final int dpi, final int finger_position, final int cbeff_id, final Fid.Format out_format, final int out_dpi, final boolean rotate180) throws UareUException {
        return this.m_dpfj.import_raw(data, width, height, dpi, finger_position, cbeff_id, out_format, out_dpi, rotate180);
    }

    @Override
    public Fid ImportDPFid(final byte[] data, final Fid.Format out_format, final int out_dpi, final boolean rotate180) throws UareUException {
        return this.m_dpfj.import_dp_fid(data, out_format, out_dpi, rotate180);
    }

    @Override
    public Fid ImportFid(final byte[] data, final Fid.Format format) throws UareUException {
        return this.m_dpfj.import_fid(data, format);
    }

    @Override
    public Fmd ImportFmd(final byte[] data, final Fmd.Format format, final Fmd.Format out_format) throws UareUException {
        return this.m_dpfj.import_fmd(data, format, out_format);
    }
}