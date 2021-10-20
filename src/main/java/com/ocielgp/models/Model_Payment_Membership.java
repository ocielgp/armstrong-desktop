package com.ocielgp.models;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Model_Payment_Membership {
    private final SimpleIntegerProperty idPaymentMembership = new SimpleIntegerProperty();
    private final LongProperty days = new SimpleLongProperty();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> startDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endDateTime = new SimpleObjectProperty<>();
    private final SimpleIntegerProperty idGym = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idStaff = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idMembership = new SimpleIntegerProperty();

    public int getIdPaymentMembership() {
        return idPaymentMembership.get();
    }

    public SimpleIntegerProperty idPaymentMembershipProperty() {
        return idPaymentMembership;
    }

    public void setIdPaymentMembership(int idPaymentMembership) {
        this.idPaymentMembership.set(idPaymentMembership);
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

    public BigDecimal getPrice() {
        return price.get();
    }

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

    public int getIdGym() {
        return idGym.get();
    }

    public SimpleIntegerProperty idGymProperty() {
        return idGym;
    }

    public void setIdGym(int idGym) {
        this.idGym.set(idGym);
    }

    public int getIdStaff() {
        return idStaff.get();
    }

    public SimpleIntegerProperty idStaffProperty() {
        return idStaff;
    }

    public void setIdStaff(int idStaff) {
        this.idStaff.set(idStaff);
    }

    public int getIdMember() {
        return idMember.get();
    }

    public SimpleIntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }

    public int getIdMembership() {
        return idMembership.get();
    }

    public SimpleIntegerProperty idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(int idMembership) {
        this.idMembership.set(idMembership);
    }
}
