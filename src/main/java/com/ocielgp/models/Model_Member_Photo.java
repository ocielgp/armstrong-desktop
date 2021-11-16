package com.ocielgp.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Model_Member_Photo {
    private final IntegerProperty idPhoto = new SimpleIntegerProperty();
    private byte[] photo;
    private final IntegerProperty idMember = new SimpleIntegerProperty();

    public int getIdPhoto() {
        return idPhoto.get();
    }

    public IntegerProperty idPhotoProperty() {
        return idPhoto;
    }

    public void setIdPhoto(int idPhoto) {
        this.idPhoto.set(idPhoto);
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
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
