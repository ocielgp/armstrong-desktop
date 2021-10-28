package com.ocielgp.models;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class Model_Membership {
    // Max length
    public static final byte MAX_PRICE = 10;
    public static final byte MAX_NAME = 60;

    private final IntegerProperty idMembership = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();

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

    @Override
    public String toString() {
        return "[$" + getPrice() + "]: " + getName();
    }
}
