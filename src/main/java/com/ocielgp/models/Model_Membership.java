package com.ocielgp.models;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class Model_Membership {
    // Max length
    public static final byte priceLength = 10;
    public static final byte descriptionLength = 60;

    private final IntegerProperty idMembership = new SimpleIntegerProperty();
    private final LongProperty months = new SimpleLongProperty();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();

    public int getIdMembership() {
        return idMembership.get();
    }

    public IntegerProperty idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(int idMembership) {
        this.idMembership.set(idMembership);
    }

    public long getMonths() {
        return months.get();
    }

    public LongProperty monthsProperty() {
        return months;
    }

    public void setMonths(long months) {
        this.months.set(months);
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

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Override
    public String toString() {
        return "[$" + getPrice() + "]: " + getDescription();
    }
}
