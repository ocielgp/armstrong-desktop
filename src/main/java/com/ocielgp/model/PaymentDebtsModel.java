package com.ocielgp.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PaymentDebtsModel {
    // Max length
    public static final byte oweLength = 10;
    public static final byte notesLength = 80;

    private final SimpleIntegerProperty idPendingPayment = new SimpleIntegerProperty();
    private final SimpleStringProperty dateTime = new SimpleStringProperty();
    private final SimpleDoubleProperty paidOut = new SimpleDoubleProperty();
    private final SimpleDoubleProperty owe = new SimpleDoubleProperty();
    private final SimpleStringProperty notes = new SimpleStringProperty();
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idStaffUser = new SimpleIntegerProperty();

    public int getIdPendingPayment() {
        return idPendingPayment.get();
    }

    public SimpleIntegerProperty idPendingPaymentProperty() {
        return idPendingPayment;
    }

    public void setIdPendingPayment(int idPendingPayment) {
        this.idPendingPayment.set(idPendingPayment);
    }

    public String getDateTime() {
        return dateTime.get();
    }

    public SimpleStringProperty dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime.set(dateTime);
    }

    public double getPaidOut() {
        return paidOut.get();
    }

    public SimpleDoubleProperty paidOutProperty() {
        return paidOut;
    }

    public void setPaidOut(double paidOut) {
        this.paidOut.set(paidOut);
    }

    public double getOwe() {
        return owe.get();
    }

    public SimpleDoubleProperty oweProperty() {
        return owe;
    }

    public void setOwe(double owe) {
        this.owe.set(owe);
    }

    public String getNotes() {
        return notes.get();
    }

    public SimpleStringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
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

    public int getIdStaffUser() {
        return idStaffUser.get();
    }

    public SimpleIntegerProperty idStaffUserProperty() {
        return idStaffUser;
    }

    public void setIdStaffUser(int idStaffUser) {
        this.idStaffUser.set(idStaffUser);
    }
}
