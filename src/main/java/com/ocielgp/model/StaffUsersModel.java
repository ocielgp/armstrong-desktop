package com.ocielgp.model;

import javafx.beans.property.*;

public class StaffUsersModel {
    private final IntegerProperty idStaffUser = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final IntegerProperty idRole = new SimpleIntegerProperty();

    public int getIdStaffUser() {
        return idStaffUser.get();
    }

    public IntegerProperty idStaffUserProperty() {
        return idStaffUser;
    }

    public void setIdStaffUser(int idStaffUser) {
        this.idStaffUser.set(idStaffUser);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public int getIdRole() {
        return idRole.get();
    }

    public IntegerProperty idRoleProperty() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole.set(idRole);
    }
}
