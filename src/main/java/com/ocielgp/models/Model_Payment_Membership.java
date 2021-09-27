package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

public class Model_Payment_Membership {
    private final SimpleIntegerProperty idPaymentMembership = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> payment = new SimpleObjectProperty<>();
    private final SimpleStringProperty startDate = new SimpleStringProperty();
    private final SimpleStringProperty endDate = new SimpleStringProperty();
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idGym = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idStaff = new SimpleIntegerProperty();
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

    public BigDecimal getPayment() {
        return payment.get();
    }

    public void setPayment(BigDecimal payment) {
        this.payment.set(payment);
    }

    public ObjectProperty<BigDecimal> paymentProperty() {
        return payment;
    }

    public String getStartDate() {
        return startDate.get();
    }

    public SimpleStringProperty startDateProperty() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate.set(startDate);
    }

    public String getEndDate() {
        return endDate.get();
    }

    public SimpleStringProperty endDateProperty() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate.set(endDate);
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
