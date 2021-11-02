package com.ocielgp.models;

import javafx.beans.property.*;

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

    private final IntegerProperty idMembership = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Short> type = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private final IntegerProperty idAdmin = new SimpleIntegerProperty();

    public int getIdMembership() {
        return idMembership.get();
    }

    public IntegerProperty idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(int idMembership) {
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

    public Short getType() {
        return type.get();
    }

    public ObjectProperty<Short> typeProperty() {
        return type;
    }

    public void setType(Short type) {
        this.type.set(type);
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

    public int getIdAdmin() {
        return idAdmin.get();
    }

    public IntegerProperty idAdminProperty() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin.set(idAdmin);
    }

    @Override
    public String toString() {
        if (getType() == 1) {
            return "[MEN][$" + getPrice() + "]: " + getName();
        } else {
            return "[VIS][$" + getPrice() + "]: " + getName();
        }
    }
}
