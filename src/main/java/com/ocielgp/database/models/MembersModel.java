package com.ocielgp.database.models;

import javafx.beans.property.*;

public class MembersModel {
    // Max length
    public static final byte nameLength = 30;
    public static final byte lastNameLength = 30;
    public static final byte phoneLength = 10;
    public static final short emailLength = 254;
    public static final byte notesLength = 80;

    private final IntegerProperty idMember = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty gender = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final StringProperty registrationDate = new SimpleStringProperty();
    private byte[] photo;

    private final LongProperty daysLeft = new SimpleLongProperty();
    private final StringProperty endDate = new SimpleStringProperty();
    private final IntegerProperty debtCount = new SimpleIntegerProperty();

    private PaymentMembershipsModel paymentMembership;
    private PaymentDebtsModel pendingPayment;

    public int getIdMember() {
        return idMember.get();
    }

    public IntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getGender() {
        return gender.get();
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getNotes() {
        return notes.get();
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public String getRegistrationDate() {
        return registrationDate.get();
    }

    public StringProperty registrationDateProperty() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate.set(registrationDate);
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public long getDaysLeft() {
        return daysLeft.get();
    }

    public LongProperty daysLeftProperty() {
        return daysLeft;
    }

    public void setDaysLeft(long daysLeft) {
        this.daysLeft.set(daysLeft);
    }

    public String getEndDate() {
        return endDate.get();
    }

    public StringProperty endDateProperty() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate.set(endDate);
    }

    public int getDebtCount() {
        return debtCount.get();
    }

    public void setDebtCount(int debtCount) {
        this.debtCount.set(debtCount);
    }

    public IntegerProperty debtCountProperty() {
        return debtCount;
    }

    public PaymentMembershipsModel getPaymentMembership() {
        return paymentMembership;
    }

    public void setPaymentMembership(PaymentMembershipsModel paymentMembership) {
        this.paymentMembership = paymentMembership;
    }

    public PaymentDebtsModel getPendingPayment() {
        return pendingPayment;
    }

    public void setPendingPayment(PaymentDebtsModel pendingPayment) {
        this.pendingPayment = pendingPayment;
    }
}
