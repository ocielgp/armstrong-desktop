package com.digitalpersona.uareu.dpfj;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.jni.DpfjCompression;
import com.digitalpersona.uareu.Compression;

public class CompressionImpl implements Compression {
    private final DpfjCompression m_dpfjc;

    public CompressionImpl() {
        this.m_dpfjc = new DpfjCompression();
    }

    @Override
    public void Start() throws UareUException {
        this.m_dpfjc.start();
    }

    @Override
    public void Finish() throws UareUException {
        this.m_dpfjc.finish();
    }

    @Override
    public void SetWsqBitrate(final int bitrate_x100, final int tolerance_aw) throws UareUException {
        this.m_dpfjc.set_wsq_bitrate(bitrate_x100, tolerance_aw);
    }

    @Override
    public void SetWsqSize(final int size, final int tolerance_aw) throws UareUException {
        this.m_dpfjc.set_wsq_size(size, tolerance_aw);
    }

    @Override
    public Fid CompressFid(final Fid fid, final CompressionAlgorithm compression_alg) throws UareUException {
        return this.m_dpfjc.compress_fid(fid, compression_alg);
    }

    @Override
    public byte[] CompressRaw(final byte[] data, final int width, final int height, final int dpi, final int bpp, final CompressionAlgorithm compression_alg) throws UareUException {
        return this.m_dpfjc.compress_raw(data, width, height, dpi, bpp, compression_alg);
    }

    @Override
    public Fid ExpandFid(final Fid fid, final CompressionAlgorithm compression_alg) throws UareUException {
        return this.m_dpfjc.expand_fid(fid, compression_alg);
    }

    @Override
    public RawImage ExpandRaw(final byte[] data, final CompressionAlgorithm compression_alg) throws UareUException {
        return this.m_dpfjc.expand_raw(data, compression_alg);
    }
}