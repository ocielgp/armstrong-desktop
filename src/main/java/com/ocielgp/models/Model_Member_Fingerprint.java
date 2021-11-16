package com.ocielgp.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Model_Member_Fingerprint {
    private final IntegerProperty idFingerprint = new SimpleIntegerProperty();
    private byte[] fingerprint;
    private final IntegerProperty idMember = new SimpleIntegerProperty();

    public int getIdFingerprint() {
        return idFingerprint.get();
    }

    public IntegerProperty idFingerprintProperty() {
        return idFingerprint;
    }

    public void setIdFingerprint(int idFingerprint) {
        this.idFingerprint.set(idFingerprint);
    }

    public byte[] getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(byte[] fingerprint) {
        this.fingerprint = fingerprint;
    }

    public int getIdMember() {
        return idMember.get();
    }

    public IntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }
}
