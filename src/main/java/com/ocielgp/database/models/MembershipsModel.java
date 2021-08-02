package com.ocielgp.database.models;

import javafx.beans.property.*;

public class MembershipsModel {
    // Max length
    public static final byte priceLength = 10;
    public static final byte descriptionLength = 60;

    private final IntegerProperty idMembership = new SimpleIntegerProperty();
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();
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

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
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

    public long getDays() {
        return days.get();
    }

    public void setDays(long days) {
        this.days.set(days);
    }

    public LongProperty daysProperty() {
        return days;
    }

    @Override
    public String toString() {
        return "[$" + getPrice() + "]: " + getDescription() + " (" + getDays() + ") dias";
    }
}
