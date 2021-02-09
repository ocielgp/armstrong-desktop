package com.digitalpersona.uareu;

public interface Importer {
    Fid ImportRaw(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final Fid.Format p6, final int p7, final boolean p8) throws UareUException;

    Fid ImportDPFid(final byte[] p0, final Fid.Format p1, final int p2, final boolean p3) throws UareUException;

    Fid ImportFid(final byte[] p0, final Fid.Format p1) throws UareUException;

    Fmd ImportFmd(final byte[] p0, final Fmd.Format p1, final Fmd.Format p2) throws UareUException;
}