package com.ocielgp.models;

import javafx.beans.property.*;

public class Model_Admin extends Model_Member {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final IntegerProperty idMember = new SimpleIntegerProperty();
    private final IntegerProperty addedByIdMember = new SimpleIntegerProperty();
    private final ObjectProperty<Short> idRole = new SimpleObjectProperty<>();

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    @Override
    public int getIdMember() {
        return idMember.get();
    }

    @Override
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

    public Short getIdRole() {
        return idRole.get();
    }

    public ObjectProperty<Short> idRoleProperty() {
        return idRole;
    }

    public void setIdRole(Short idRole) {
        this.idRole.set(idRole);
    }
}
