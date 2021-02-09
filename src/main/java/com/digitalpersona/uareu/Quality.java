package com.digitalpersona.uareu;

public interface Quality {
    int NfiqFid(final Fid p0, final int p1, final QualityAlgorithm p2) throws UareUException;

    int NfiqRaw(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final QualityAlgorithm p5) throws UareUException;

    public enum QualityAlgorithm {
        QUALITY_NFIQ_NIST,
        QUALITY_NFIQ_AWARE;
    }
}