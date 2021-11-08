package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDateTime;

public class Model_Payment_Membership extends Model_Membership {
    private final ObjectProperty<Integer> idPaymentMembership = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> months = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> startDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> idGym = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idAdmin = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idMember = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idMembership = new SimpleObjectProperty<>();

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

    public Integer getIdAdmin() {
        return idAdmin.get();
    }

    public ObjectProperty<Integer> idAdminProperty() {
        return idAdmin;
    }

    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin.set(idAdmin);
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
}
