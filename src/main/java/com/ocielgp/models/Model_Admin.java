package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Model_Admin extends Model_Member {
    // Max length
    public static final byte usernameLength = 45;

    private final ObjectProperty<Integer> idAdmin = new SimpleObjectProperty<>();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final ObjectProperty<Short> idRole = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> createdBy = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> updatedBy = new SimpleObjectProperty<>();

    private final StringProperty roleName = new SimpleStringProperty();

    private final StringProperty metadata = new SimpleStringProperty();

    public String getMetadata() {
        return metadata.get();
    }

    public StringProperty metadataProperty() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata.set(metadata);
    }

    public Integer getIdAdmin() {
        return idAdmin.get();
    }

    public ObjectProperty<Integer> idAdminProperty() {
        return idAdmin;
    }

    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin.set(idAdmin);
    }

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

    public Short getIdRole() {
        return idRole.get();
    }

    public ObjectProperty<Short> idRoleProperty() {
        return idRole;
    }

    public void setIdRole(Short idRole) {
        this.idRole.set(idRole);
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    @Override
    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    @Override
    public Integer getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public ObjectProperty<Integer> createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy.set(createdBy);
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    @Override
    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    @Override
    public Integer getUpdatedBy() {
        return updatedBy.get();
    }

    @Override
    public ObjectProperty<Integer> updatedByProperty() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy.set(updatedBy);
    }

    public String getRoleName() {
        return roleName.get();
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
