package com.ocielgp.database.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class GymsModel {
    private final SimpleIntegerProperty idGym = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty address = new SimpleStringProperty();

    public int getIdGym() {
        return idGym.get();
    }

    public SimpleIntegerProperty idGymProperty() {
        return idGym;
    }

    public void setIdGym(int idGym) {
        this.idGym.set(idGym);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAddress() {
        return address.get();
    }

    public SimpleStringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    @Override
    public String toString() {
        return getName();
    }
}
