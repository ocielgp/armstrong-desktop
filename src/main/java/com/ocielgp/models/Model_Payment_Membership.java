package com.ocielgp.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;

public class Model_Payment_Membership extends Model_Membership {
    private final IntegerProperty idPaymentMembership = new SimpleIntegerProperty();
    private final ObjectProperty<Short> months = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> startDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> idGym = new SimpleObjectProperty<>();
    private final IntegerProperty idAdmin = new SimpleIntegerProperty();
    private final IntegerProperty idMember = new SimpleIntegerProperty();
    private final IntegerProperty idMembership = new SimpleIntegerProperty();

    public int getIdPaymentMembership() {
        return idPaymentMembership.get();
    }

    public IntegerProperty idPaymentMembershipProperty() {
        return idPaymentMembership;
    }

    public void setIdPaymentMembership(int idPaymentMembership) {
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

    public int getIdMember() {
        return idMember.get();
    }

    public IntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }

    @Override
    public int getIdMembership() {
        return idMembership.get();
    }

    @Override
    public IntegerProperty idMembershipProperty() {
        return idMembership;
    }

    public void setIdMembership(int idMembership) {
        this.idMembership.set(idMembership);
    }
}
