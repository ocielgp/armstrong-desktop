package com.digitalpersona.uareu.dpfj;

import com.digitalpersona.uareu.Fmd;

public class FmdImpl implements Fmd {
    private int cbeff_id;
    private int capture_equipment_comp;
    private int capture_equipment_id;
    private int width;
    private int height;
    private int resolution;
    private Format m_format;
    private byte[] m_data;
    private FmvImpl[] m_views;

    @Override
    public int getCbeffId() {
        return this.cbeff_id;
    }

    @Override
    public int getCaptureEquipmentCompliance() {
        return this.capture_equipment_comp;
    }

    @Override
    public int getCaptureEquipmentId() {
        return this.capture_equipment_id;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getResolution() {
        return this.resolution;
    }

    @Override
    public int getViewCnt() {
        return this.m_views.length;
    }

    @Override
    public Format getFormat() {
        return this.m_format;
    }

    @Override
    public Fmv[] getViews() {
        return this.m_views;
    }

    @Override
    public byte[] getData() {
        return this.m_data;
    }

    public FmdImpl(final Format format, final int nViewCnt) {
        this.m_format = format;
        this.m_views = new FmvImpl[nViewCnt];
        for (int i = 0; i < nViewCnt; ++i) {
            this.m_views[i] = new FmvImpl(this);
        }
    }

    private class FmvImpl implements Fmv {
        private int finger_position;
        private int view_number;
        private int impression_type;
        private int quality;
        private int minutia_cnt;
        private int ext_block_length;
        private int m_length;
        private int m_offset;
        private FmdImpl m_fmd;

        @Override
        public int getFingerPosition() {
            return this.finger_position;
        }

        @Override
        public int getViewNumber() {
            return this.view_number;
        }

        @Override
        public int getImpressionType() {
            return this.impression_type;
        }

        @Override
        public int getQuality() {
            return this.quality;
        }

        @Override
        public int getMinutiaCnt() {
            return this.minutia_cnt;
        }

        @Override
        public byte[] getData() {
            final byte[] data = new byte[this.m_length];
            System.arraycopy(this.m_fmd.m_data, this.m_offset, data, 0, this.m_length);
            return data;
        }

        @Override
        public byte[] getExtBlockData() {
            if (0 == this.ext_block_length || -1 == this.ext_block_length) {
                return null;
            }
            final byte[] data = new byte[this.ext_block_length];
            System.arraycopy(this.m_fmd.m_data, this.m_offset + this.m_length - this.ext_block_length, data, 0, this.ext_block_length);
            return data;
        }

        protected FmvImpl(final FmdImpl fmd) {
            this.m_fmd = fmd;
        }
    }
}