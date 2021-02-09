package com.digitalpersona.uareu;

public interface Compression {
    void Start() throws UareUException;

    void Finish() throws UareUException;

    void SetWsqBitrate(final int p0, final int p1) throws UareUException;

    void SetWsqSize(final int p0, final int p1) throws UareUException;

    Fid CompressFid(final Fid p0, final CompressionAlgorithm p1) throws UareUException;

    byte[] CompressRaw(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final CompressionAlgorithm p5) throws UareUException;

    Fid ExpandFid(final Fid p0, final CompressionAlgorithm p1) throws UareUException;

    RawImage ExpandRaw(final byte[] p0, final CompressionAlgorithm p1) throws UareUException;

    public static class RawImage {
        public int width;
        public int height;
        public int dpi;
        public int bpp;
        public byte[] data;

        public RawImage(final int image_width, final int image_height, final int image_dpi, final int image_bpp, final byte[] image_data) {
            this.width = image_width;
            this.height = image_height;
            this.dpi = image_dpi;
            this.bpp = image_bpp;
            this.data = image_data;
        }
    }

    public enum CompressionAlgorithm {
        COMPRESSION_WSQ_NIST,
        COMPRESSION_WSQ_AWARE;
    }
}