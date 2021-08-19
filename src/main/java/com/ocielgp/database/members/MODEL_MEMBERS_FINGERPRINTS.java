package com.ocielgp.database.members;

import javafx.beans.property.SimpleIntegerProperty;

public class MODEL_MEMBERS_FINGERPRINTS {
    private final SimpleIntegerProperty idFingerprint = new SimpleIntegerProperty();
    private byte[] fingerprint;
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();

    public int getIdFingerprint() {
        return idFingerprint.get();
    }

    public SimpleIntegerProperty idFingerprintProperty() {
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

    public SimpleIntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }
}
