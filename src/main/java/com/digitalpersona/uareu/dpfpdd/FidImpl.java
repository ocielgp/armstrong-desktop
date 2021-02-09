package com.digitalpersona.uareu.dpfpdd;

import com.digitalpersona.uareu.Fid;

public final class FidImpl implements Fid {
    private int cbeff_id;
    private int capture_device_id;
    private int acquisition_level;
    private int scale_units;
    private int scan_resolution;
    private int image_resolution;
    private int bpp;
    private int compression;
    private Format m_format;
    private byte[] m_data;
    private FivImpl[] m_views;

    @Override
    public int getCbeffId() {
        return this.cbeff_id;
    }

    @Override
    public int getCaptureDeviceId() {
        return this.capture_device_id;
    }

    @Override
    public int getAcquisitionLevel() {
        return this.acquisition_level;
    }

    @Override
    public int getFingerCnt() {
        return this.m_views.length;
    }

    @Override
    public int getScaleUnits() {
        return this.scale_units;
    }

    @Override
    public int getScanResolution() {
        return this.scan_resolution;
    }

    @Override
    public int getImageResolution() {
        return this.image_resolution;
    }

    @Override
    public int getBpp() {
        return this.bpp;
    }

    @Override
    public int getCompression() {
        return this.compression;
    }

    @Override
    public Format getFormat() {
        return this.m_format;
    }

    @Override
    public Fiv[] getViews() {
        return this.m_views;
    }

    @Override
    public byte[] getData() {
        return this.m_data;
    }

    public FidImpl(final Format format, final int nViewCnt) {
        this.m_format = format;
        this.m_views = new FivImpl[nViewCnt];
        for (int i = 0; i < nViewCnt; ++i) {
            this.m_views[i] = new FivImpl(this);
        }
    }

    private final class FivImpl implements Fiv {
        private int finger_position;
        private int view_cnt;
        private int view_number;
        private int quality;
        private int impression_type;
        private int height;
        private int width;
        private int m_length;
        private int m_offset;
        private int m_image_offset;
        private FidImpl m_fid;

        @Override
        public int getFingerPosition() {
            return this.finger_position;
        }

        @Override
        public int getViewCnt() {
            return this.view_cnt;
        }

        @Override
        public int getViewNumber() {
            return this.view_number;
        }

        @Override
        public int getQuality() {
            return this.quality;
        }

        @Override
        public int getImpressionType() {
            return this.impression_type;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public byte[] getData() {
            final byte[] data = new byte[this.m_length];
            System.arraycopy(this.m_fid.m_data, this.m_offset, data, 0, this.m_length);
            return data;
        }

        @Override
        public byte[] getImageData() {
            final int nImageLength = this.m_length - (this.m_image_offset - this.m_offset);
            final byte[] data = new byte[nImageLength];
            System.arraycopy(this.m_fid.m_data, this.m_image_offset, data, 0, nImageLength);
            return data;
        }

        protected FivImpl(final FidImpl fid) {
            this.m_fid = fid;
        }
    }
}