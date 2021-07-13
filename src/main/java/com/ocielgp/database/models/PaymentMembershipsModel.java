package com.ocielgp.database.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PaymentMembershipsModel {
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final SimpleStringProperty days = new SimpleStringProperty();
    private final SimpleStringProperty startDate = new SimpleStringProperty();
    private final SimpleStringProperty endDate = new SimpleStringProperty();
    private final SimpleStringProperty paymentDate = new SimpleStringProperty();
    private final SimpleStringProperty notes = new SimpleStringProperty();
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idGym = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idStaffUser = new SimpleIntegerProperty();
    private final SimpleBooleanProperty backup = new SimpleBooleanProperty();

    public double getPrice() {
        return price.get();
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getDays() {
        return days.get();
    }

    public SimpleStringProperty daysProperty() {
        return days;
    }

    public void setDays(String days) {
        this.days.set(days);
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

    public String getPaymentDate() {
        return paymentDate.get();
    }

    public SimpleStringProperty paymentDateProperty() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate.set(paymentDate);
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

    public int getIdGym() {
        return idGym.get();
    }

    public SimpleIntegerProperty idGymProperty() {
        return idGym;
    }

    public void setIdGym(int idGym) {
        this.idGym.set(idGym);
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

    public boolean isBackup() {
        return backup.get();
    }

    public SimpleBooleanProperty backupProperty() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup.set(backup);
    }
}
