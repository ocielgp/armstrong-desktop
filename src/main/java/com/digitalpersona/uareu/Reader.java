package com.digitalpersona.uareu;

public interface Reader {
    void Open(final Priority p0) throws UareUException;

    void Close() throws UareUException;

    Status GetStatus() throws UareUException;

    Capabilities GetCapabilities();

    Description GetDescription();

    CaptureResult Capture(final Fid.Format p0, final ImageProcessing p1, final int p2, final int p3) throws UareUException;

    void CaptureAsync(final Fid.Format p0, final ImageProcessing p1, final int p2, final int p3, final CaptureCallback p4) throws UareUException;

    void CancelCapture() throws UareUException;

    void StartStreaming() throws UareUException;

    void StopStreaming() throws UareUException;

    CaptureResult GetStreamImage(final Fid.Format p0, final ImageProcessing p1, final int p2) throws UareUException;

    void Calibrate() throws UareUException;

    void Reset() throws UareUException;

    public static class Id {
        public int product_id;
        public int vendor_id;
        public String product_name;
        public String vendor_name;
    }

    public static class VersionInfo {
        public int major;
        public int minor;
        public int maintenance;
    }

    public static class Version {
        public VersionInfo firmware_version;
        public VersionInfo hardware_version;
        public int bcd_revision;

        public Version() {
            this.firmware_version = new VersionInfo();
            this.hardware_version = new VersionInfo();
        }
    }

    public enum Modality {
        HW_MODALITY_UNKNOWN,
        HW_MODALITY_SWIPE,
        HW_MODALITY_AREA;
    }

    public enum Technology {
        HW_TECHNOLOGY_UNKNOWN,
        HW_TECHNOLOGY_OPTICAL,
        HW_TECHNOLOGY_CAPACITIVE,
        HW_TECHNOLOGY_THERMAL,
        HW_TECHNOLOGY_PRESSURE;
    }

    public static class Description {
        public String name;
        public String serial_number;
        public Id id;
        public Modality modality;
        public Technology technology;
        public Version version;

        public Description() {
            this.id = new Id();
            this.modality = Modality.HW_MODALITY_UNKNOWN;
            this.technology = Technology.HW_TECHNOLOGY_UNKNOWN;
            this.version = new Version();
        }
    }

    public static class Capabilities {
        public boolean can_capture;
        public boolean can_stream;
        public boolean can_extract_features;
        public boolean can_match;
        public boolean can_identify;
        public boolean has_fingerprint_storage;
        public int indicator_type;
        public boolean has_power_management;
        public boolean has_calibration;
        public boolean piv_compliant;
        public int[] resolutions;
    }

    public enum CaptureQuality {
        GOOD,
        TIMED_OUT,
        CANCELED,
        NO_FINGER,
        FAKE_FINGER,
        FINGER_TOO_LEFT,
        FINGER_TOO_RIGHT,
        FINGER_TOO_HIGH,
        FINGER_TOO_LOW,
        FINGER_OFF_CENTER,
        SCAN_SKEWED,
        SCAN_TOO_SHORT,
        SCAN_TOO_LONG,
        SCAN_TOO_SLOW,
        SCAN_TOO_FAST,
        SCAN_WRONG_DIRECTION,
        READER_DIRTY;
    }

    public static class CaptureResult {
        public int score;
        public CaptureQuality quality;
        public Fid image;
    }

    public enum ReaderStatus {
        READY,
        BUSY,
        NEED_CALIBRATION,
        FAILURE;
    }

    public static class Status {
        public boolean finger_detected;
        public ReaderStatus status;
        public byte[] vendor_data;

        @Override
        public String toString() {
            String str = null;
            switch (this.status) {
                case READY: {
                    str = "Reader is ready for capture.";
                    break;
                }
                case BUSY: {
                    str = "Reader cannot capture, another operation is in progress.";
                    break;
                }
                case NEED_CALIBRATION: {
                    str = "Reader is ready for capture, but calibration needs to be performed soon.";
                    break;
                }
                case FAILURE: {
                    str = "Reader cannot capture, reset is needed.";
                    break;
                }
                default: {
                    str = String.format("Unknown reader status", new Object[0]);
                    break;
                }
            }
            return str;
        }
    }

    public enum ImageProcessing {
        IMG_PROC_DEFAULT,
        IMG_PROC_PIV,
        IMG_PROC_ENHANCED,
        IMG_PROC_UNPROCESSED;
    }

    public enum Priority {
        COOPERATIVE,
        EXCLUSIVE;
    }

    public interface CaptureCallback {
        void CaptureResultEvent(final CaptureResult p0);
    }
}