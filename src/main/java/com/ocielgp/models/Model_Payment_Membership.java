package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Model_Payment_Membership extends Model_Membership {
    private final ObjectProperty<Integer> idPaymentMembership = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> months = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> startDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<Boolean> firstMembership = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> idGym = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idMember = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idMembership = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> createdBy = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> updatedBy = new SimpleObjectProperty<>();

    public Integer getIdPaymentMembership() {
        return idPaymentMembership.get();
    }

    public ObjectProperty<Integer> idPaymentMembershipProperty() {
        return idPaymentMembership;
    }

    public void setIdPaymentMembership(Integer idPaymentMembership) {
        this.idPaymentMembership.set(idPaymentMembership);
    }

    public Short getMonths() {
        return months.get();
    }

    public ObjectProperty<Short> monthsProperty() {
        return months;
    }

    public void setMonths(Short months) {
        this.months.set(months);
    }

    @Override
    public BigDecimal getPrice() {
        return price.get();
    }

    @Override
    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime.get();
    }

    public ObjectProperty<LocalDateTime> startDateTimeProperty() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime.set(startDateTime);
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime.get();
    }

    public ObjectProperty<LocalDateTime> endDateTimeProperty() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime.set(endDateTime);
    }

    public Boolean getFirstMembership() {
        return firstMembership.get();
    }

    public ObjectProperty<Boolean> firstMembershipProperty() {
        return firstMembership;
    }

    public void setFirstMembership(Boolean firstMembership) {
        this.firstMembership.set(firstMembership);
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

    public Integer getIdMember() {
        return idMember.get();
    }

    public ObjectProperty<Integer> idMemberProperty() {
        return idMember;
    }

    public void setIdMember(Integer idMember) {
        this.idMember.set(idMember);
    }

    @Override
    public Integer getIdMembership() {
        return idMembership.get();
    }

    @Override
    public ObjectProperty<Integer> idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(Integer idMembership) {
        this.idMembership.set(idMembership);
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    @Override
    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    @Override
    public Integer getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public ObjectProperty<Integer> createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy.set(createdBy);
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    @Override
    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    @Override
    public Integer getUpdatedBy() {
        return updatedBy.get();
    }

    @Override
    public ObjectProperty<Integer> updatedByProperty() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy.set(updatedBy);
    }
}
