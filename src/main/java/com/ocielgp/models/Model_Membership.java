package com.ocielgp.models;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class Model_Membership {
    // Max length
    public static final byte priceLength = 10;
    public static final byte descriptionLength = 60;

    private final IntegerProperty idMembership = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final LongProperty days = new SimpleLongProperty();

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

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
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

    public long getDays() {
        return days.get();
    }

    public LongProperty daysProperty() {
        return days;
    }

    public void setDays(long days) {
        this.days.set(days);
    }

    @Override
    public String toString() {
        return "[$" + getPrice() + "]: " + getDescription() + " (" + getDays() + ") dias";
    }
}
