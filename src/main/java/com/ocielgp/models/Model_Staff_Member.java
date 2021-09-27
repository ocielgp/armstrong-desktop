package com.ocielgp.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Model_Staff_Member {
    private final IntegerProperty idStaffMember = new SimpleIntegerProperty();
    private final SimpleStringProperty username = new SimpleStringProperty();
    private final SimpleStringProperty password = new SimpleStringProperty();
    private final IntegerProperty idMember = new SimpleIntegerProperty();
    private final IntegerProperty addedByIdMember = new SimpleIntegerProperty();
    private final IntegerProperty idRole = new SimpleIntegerProperty();

    public int getIdStaffMember() {
        return idStaffMember.get();
    }

    public IntegerProperty idStaffMemberProperty() {
        return idStaffMember;
    }

    public void setIdStaffMember(int idStaffMember) {
        this.idStaffMember.set(idStaffMember);
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
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

    public int getAddedByIdMember() {
        return addedByIdMember.get();
    }

    public IntegerProperty addedByIdMemberProperty() {
        return addedByIdMember;
    }

    public void setAddedByIdMember(int addedByIdMember) {
        this.addedByIdMember.set(addedByIdMember);
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
