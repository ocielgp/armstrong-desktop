package com.digitalpersona.uareu;

public class UareUException extends Exception {
    public static final int URU_E_SUCCESS = 0;
    public static final int URU_E_NOT_IMPLEMENTED = 96075786;
    public static final int URU_E_FAILURE = 96075787;
    public static final int URU_E_NO_DATA = 96075788;
    public static final int URU_E_MORE_DATA = 96075789;
    public static final int URU_E_INVALID_PARAMETER = 96075796;
    public static final int URU_E_INVALID_DEVICE = 96075797;
    public static final int URU_E_DEVICE_BUSY = 96075806;
    public static final int URU_E_DEVICE_FAILURE = 96075807;
    public static final int URU_E_INVALID_FID = 96075877;
    public static final int URU_E_TOO_SMALL_AREA = 96075878;
    public static final int URU_E_INVALID_FMD = 96075977;
    public static final int URU_E_ENROLLMENT_IN_PROGRESS = 96076077;
    public static final int URU_E_ENROLLMENT_NOT_STARTED = 96076078;
    public static final int URU_E_ENROLLMENT_NOT_READY = 96076079;
    public static final int DPFJ_E_ENROLLMENT_INVALID_SET = 96076080;
    public static final int URU_E_ENROLLMENT_INVALID_SET = 96076080;
    public static final int URU_E_COMPRESSION_IN_PROGRESS = 96076097;
    public static final int URU_E_COMPRESSION_NOT_STARTED = 96076098;
    public static final int URU_E_COMPRESSION_INVALID_WSQ_PARAMETER = 96076106;
    public static final int URU_E_COMPRESSION_WSQ_FAILURE = 96076107;
    public static final int URU_E_COMPRESSION_WSQ_LIB_NOT_FOUND = 96076108;
    public static final int URU_E_QUALITY_NO_IMAGE = 96076126;
    public static final int URU_E_QUALITY_TOO_FEW_MINUTIA = 96076127;
    public static final int URU_E_QUALITY_FAILURE = 96076128;
    public static final int URU_E_QUALITY_LIB_NOT_FOUND = 96076129;
    private static final long serialVersionUID = 4311262755669951497L;
    private final String m_str;
    private final int m_code;

    public UareUException(final int n) {
        switch (n) {
            case 0: {
                this.m_code = n;
                this.m_str = "API call succeeded.";
                break;
            }
            case 96075786: {
                this.m_code = n;
                this.m_str = "API call is not implemented.";
                break;
            }
            case 96075787: {
                this.m_code = n;
                this.m_str = "Reason for the failure is unknown or cannot be specified.";
                break;
            }
            case 96075788: {
                this.m_code = n;
                this.m_str = "No data is available.";
                break;
            }
            case 96075789: {
                this.m_code = n;
                this.m_str = "The memory allocated by the application is not big enough for the data which is expected.";
                break;
            }
            case 96075796: {
                this.m_code = n;
                this.m_str = "One or more parameters passed to the API call are invalid.";
                break;
            }
            case 96075797: {
                this.m_code = n;
                this.m_str = "Reader handle is not valid.";
                break;
            }
            case 96075806: {
                this.m_code = n;
                this.m_str = "The API call cannot be completed because another call is in progress.";
                break;
            }
            case 96075807: {
                this.m_code = n;
                this.m_str = "The reader is not working properly.";
                break;
            }
            case 96075877: {
                this.m_code = n;
                this.m_str = "FID is invalid.";
                break;
            }
            case 96075878: {
                this.m_code = n;
                this.m_str = "Image is too small.";
                break;
            }
            case 96075977: {
                this.m_code = n;
                this.m_str = "FMD is invalid.";
                break;
            }
            case 96076077: {
                this.m_code = n;
                this.m_str = "Enrollment operation is in progress.";
                break;
            }
            case 96076078: {
                this.m_code = n;
                this.m_str = "Enrollment operation has not begun.";
                break;
            }
            case 96076079: {
                this.m_code = n;
                this.m_str = "Not enough in the pool of FMDs to create enrollment FMD.";
                break;
            }
            case 96076080: {
                this.m_code = n;
                this.m_str = "Unable to create enrollment FMD with the collected set of FMDs.";
                break;
            }
            case 96076097: {
                this.m_code = n;
                this.m_str = "Compression or decompression operation is in progress.";
                break;
            }
            case 96076098: {
                this.m_code = n;
                this.m_str = "Compression or decompression operation was not started.";
                break;
            }
            case 96076106: {
                this.m_code = n;
                this.m_str = "One or more parameters passed for WSQ compression are invalid.";
                break;
            }
            case 96076107: {
                this.m_code = n;
                this.m_str = "Unspecified error during WSQ compression or decompression.";
                break;
            }
            case 96076108: {
                this.m_code = n;
                this.m_str = "Library for WSQ compression is not found or not built-in.";
                break;
            }
            case 96076126: {
                this.m_code = n;
                this.m_str = "Image is invalid or absent.";
                break;
            }
            case 96076127: {
                this.m_code = n;
                this.m_str = "Too few minutia detected in the fingerprint image.";
                break;
            }
            case 96076128: {
                this.m_code = n;
                this.m_str = "Unspecified error during execution.";
                break;
            }
            case 96076129: {
                this.m_code = n;
                this.m_str = "Library for image quality is not found or not built-in.";
                break;
            }
            default: {
                this.m_code = -1;
                this.m_str = String.format("Unknown error, code: 0x%x", n);
                break;
            }
        }
    }

    public int getCode() {
        return this.m_code;
    }

    @Override
    public String toString() {
        return this.m_str;
    }
}