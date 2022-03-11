package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Model_Payment_Visit {
    private final ObjectProperty<Integer> idPaymentVisit = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>(new BigDecimal(0));
    private final ObjectProperty<Short> idGym = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idMembership = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> createdBy = new SimpleObjectProperty<>();

    public Integer getIdPaymentVisit() {
        return idPaymentVisit.get();
    }

    public ObjectProperty<Integer> idPaymentVisitProperty() {
        return idPaymentVisit;
    }

    public void setIdPaymentVisit(Integer idPaymentVisit) {
        this.idPaymentVisit.set(idPaymentVisit);
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

    public Integer getIdMembership() {
        return idMembership.get();
    }

    public ObjectProperty<Integer> idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(Integer idMembership) {
        this.idMembership.set(idMembership);
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
}
