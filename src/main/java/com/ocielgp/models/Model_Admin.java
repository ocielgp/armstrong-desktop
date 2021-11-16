package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Model_Admin extends Model_Member {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final ObjectProperty<Integer> idMember = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> addedByIdMember = new SimpleObjectProperty<>();
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
    public Integer getIdMember() {
        return idMember.get();
    }

    @Override
    public ObjectProperty<Integer> idMemberProperty() {
        return idMember;
    }

    public void setIdMember(Integer idMember) {
        this.idMember.set(idMember);
    }

    public Integer getAddedByIdMember() {
        return addedByIdMember.get();
    }

    public ObjectProperty<Integer> addedByIdMemberProperty() {
        return addedByIdMember;
    }

    public void setAddedByIdMember(Integer addedByIdMember) {
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
