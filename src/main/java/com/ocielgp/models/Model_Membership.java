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
    private final ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idAdmin = new SimpleObjectProperty<>();

    public Integer getIdMembership() {
        return idMembership.get();
    }

    public ObjectProperty<Integer> idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(Integer idMembership) {
        this.idMembership.set(idMembership);
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
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

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
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

    @Override
    public String toString() {
        if (getMonthly()) {
            return "[MEN][$" + getPrice() + "]: " + getName();
        } else {
            return "[VIS][$" + getPrice() + "]: " + getName();
        }
    }
}
