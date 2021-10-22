package com.ocielgp.models;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Model_Payment_Membership extends Model_Membership {
    private final SimpleIntegerProperty idPaymentMembership = new SimpleIntegerProperty();
    private final LongProperty months = new SimpleLongProperty();
    private final ObjectProperty<LocalDateTime> startDateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> endDateTime = new SimpleObjectProperty<>();
    private final SimpleIntegerProperty idGym = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idAdmin = new SimpleIntegerProperty();
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

    public long getMonths() {
        return months.get();
    }

    public LongProperty monthsProperty() {
        return months;
    }

    public void setMonths(long months) {
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

    public int getIdGym() {
        return idGym.get();
    }

    public SimpleIntegerProperty idGymProperty() {
        return idGym;
    }

    public void setIdGym(int idGym) {
        this.idGym.set(idGym);
    }

    public int getIdAdmin() {
        return idAdmin.get();
    }

    public SimpleIntegerProperty idAdminProperty() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin.set(idAdmin);
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
