package com.ocielgp.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Model_Payment_Visit {
    private final IntegerProperty idPaymentVisit = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>(new BigDecimal(0));
    private final ObjectProperty<Short> idGym = new SimpleObjectProperty<>();
    private final IntegerProperty idAdmin = new SimpleIntegerProperty();
    private final IntegerProperty idVisit = new SimpleIntegerProperty();

    public int getIdPaymentVisit() {
        return idPaymentVisit.get();
    }

    public IntegerProperty idPaymentVisitProperty() {
        return idPaymentVisit;
    }

    public void setIdPaymentVisit(int idPaymentVisit) {
        this.idPaymentVisit.set(idPaymentVisit);
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

    public BigDecimal getPrice() {
        return price.get();
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public Short getIdGym() {
        return idGym.get();
    }

    public ObjectProperty<Short> idGymProperty() {
        return idGym;
    }

    public void setIdGym(Short idGym) {
        this.idGym.set(idGym);
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

    public int getIdVisit() {
        return idVisit.get();
    }

    public IntegerProperty idVisitProperty() {
        return idVisit;
    }

    public void setIdVisit(int idVisit) {
        this.idVisit.set(idVisit);
    }
}
