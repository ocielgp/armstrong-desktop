package com.digitalpersona.uareu;

public interface Fid {
    int getCbeffId();

    int getCaptureDeviceId();

    int getAcquisitionLevel();

    int getFingerCnt();

    int getScaleUnits();

    int getScanResolution();

    int getImageResolution();

    int getBpp();

    int getCompression();

    Format getFormat();

    Fiv[] getViews();

    byte[] getData();

    public enum Format {
        ANSI_381_2004,
        ISO_19794_4_2005;
    }

    public interface Fiv {
        int getFingerPosition();

        int getViewCnt();

        int getViewNumber();

        int getQuality();

        int getImpressionType();

        int getHeight();

        int getWidth();

        byte[] getData();

        byte[] getImageData();
    }
}