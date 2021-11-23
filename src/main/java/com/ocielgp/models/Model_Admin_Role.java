package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Model_Admin_Role {
    private final ObjectProperty<Short> idRole = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> createdBy = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> updatedBy = new SimpleObjectProperty<>();

    public Short getIdRole() {
        return idRole.get();
    }

    public ObjectProperty<Short> idRoleProperty() {
        return idRole;
    }

    public void setIdRole(Short idRole) {
        this.idRole.set(idRole);
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

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public Integer getCreatedBy() {
        return createdBy.get();
    }

    public ObjectProperty<Integer> createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy.set(createdBy);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public Integer getUpdatedBy() {
        return updatedBy.get();
    }

    public ObjectProperty<Integer> updatedByProperty() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy.set(updatedBy);
    }

    @Override
    public String toString() {
        return getName();
    }
}
