package com.ocielgp.database.payments;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MODEL_PAYMENTS_MEMBERSHIPS {
    private final SimpleIntegerProperty idPaymentMembership = new SimpleIntegerProperty();
    private final SimpleDoubleProperty payment = new SimpleDoubleProperty();
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

    public double getPayment() {
        return payment.get();
    }

    public SimpleDoubleProperty paymentProperty() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment.set(payment);
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
