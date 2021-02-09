package com.digitalpersona.uareu;

public interface Fmd {
    int getCbeffId();

    int getCaptureEquipmentCompliance();

    int getCaptureEquipmentId();

    int getWidth();

    int getHeight();

    int getResolution();

    int getViewCnt();

    Format getFormat();

    Fmv[] getViews();

    byte[] getData();

    public enum Format {
        ANSI_378_2004,
        ISO_19794_2_2005,
        DP_PRE_REG_FEATURES,
        DP_REG_FEATURES,
        DP_VER_FEATURES;
    }

    public interface Fmv {
        int getFingerPosition();

        int getViewNumber();

        int getImpressionType();

        int getQuality();

        int getMinutiaCnt();

        byte[] getData();

        byte[] getExtBlockData();
    }
}