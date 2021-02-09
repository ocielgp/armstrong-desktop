package com.digitalpersona.uareu;

public interface Engine {
    public static final int PROBABILITY_ONE = Integer.MAX_VALUE;

    void SelectEngine(final EngineType p0) throws UareUException;

    Fmd CreateFmd(final Fid p0, final Fmd.Format p1) throws UareUException;

    Fmd CreateFmd(final byte[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final Fmd.Format p6) throws UareUException;

    int Compare(final Fmd p0, final int p1, final Fmd p2, final int p3) throws UareUException;

    Candidate[] Identify(final Fmd p0, final int p1, final Fmd[] p2, final int p3, final int p4) throws UareUException;

    Fmd CreateEnrollmentFmd(final Fmd.Format p0, final EnrollmentCallback p1) throws UareUException;

    public static class Candidate {
        public int fmd_index;
        public int view_index;
    }

    public static class PreEnrollmentFmd {
        public Fmd fmd;
        public int view_index;
    }

    public enum EngineType {
        ENGINE_DPFJ,
        ENGINE_INNOVATRICS_ANSIISO;
    }

    public interface EnrollmentCallback {
        PreEnrollmentFmd GetFmd(final Fmd.Format p0);
    }
}