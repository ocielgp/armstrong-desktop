package com.ocielgp.database.members;

import javafx.beans.property.SimpleIntegerProperty;

public class MODEL_MEMBERS_PHOTOS {
    private final SimpleIntegerProperty idPhoto = new SimpleIntegerProperty();
    private byte photo[];
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();

    public int getIdPhoto() {
        return idPhoto.get();
    }

    public SimpleIntegerProperty idPhotoProperty() {
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

    public SimpleIntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }
}
