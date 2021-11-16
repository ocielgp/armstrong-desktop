package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Model_Membership {
    // types
    public static final byte VISIT = 0;
    public static final byte MONTHLY = 1;
    public static final byte ALL = 2;

    // Max length
    public static final byte MAX_PRICE = 10;
    public static final byte MAX_NAME = 60;

    private final ObjectProperty<Integer> idMembership = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final ObjectProperty<Boolean> monthly = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> createdBy = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> updatedBy = new SimpleObjectProperty<>();

    public Integer getIdMembership() {
        return idMembership.get();
    }

    public ObjectProperty<Integer> idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(Integer idMembership) {
        this.idMembership.set(idMembership);
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

    public BigDecimal getPrice() {
        return price.get();
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public Boolean getMonthly() {
        return monthly.get();
    }

    public ObjectProperty<Boolean> monthlyProperty() {
        return monthly;
    }

    public void setMonthly(Boolean monthly) {
        this.monthly.set(monthly);
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
        if (getMonthly()) {
            return "[M][$" + getPrice() + "]: " + getName();
        } else {
            return "[D][$" + getPrice() + "]: " + getName();
        }
    }
}
