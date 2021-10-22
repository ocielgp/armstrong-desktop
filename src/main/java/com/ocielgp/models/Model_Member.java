package com.ocielgp.models;

import javafx.beans.property.*;

import java.time.LocalDateTime;

public class Model_Member {
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
    private final StringProperty notes = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> registrationDateTime = new SimpleObjectProperty<>();
    private final BooleanProperty access = new SimpleBooleanProperty();
    private final IntegerProperty idGym = new SimpleIntegerProperty();

    private final StringProperty endDate = new SimpleStringProperty();
    private String style;

    // members
    private Model_Member_Photo modelMemberPhoto;
    private Model_Member_Fingerprint modelMemberFingerprint;

    // gym
    private Model_Gym modelGyms;

    // staff
    private Model_Admin modelStaffMember;

    // payments
    private Model_Payment_Membership modelPaymentMembership;

    // debts
    private Model_Debt modelDebt;

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

    public String getNotes() {
        return notes.get();
    }

    public StringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime.get();
    }

    public ObjectProperty<LocalDateTime> registrationDateTimeProperty() {
        return registrationDateTime;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime.set(registrationDateTime);
    }

    public boolean isAccess() {
        return access.get();
    }

    public BooleanProperty accessProperty() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access.set(access);
    }

    public int getIdGym() {
        return idGym.get();
    }

    public IntegerProperty idGymProperty() {
        return idGym;
    }

    public void setIdGym(int idGym) {
        this.idGym.set(idGym);
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Model_Member_Photo getModelMemberPhoto() {
        return modelMemberPhoto;
    }

    public void setModelMemberPhoto(Model_Member_Photo modelMemberPhoto) {
        this.modelMemberPhoto = modelMemberPhoto;
    }

    public Model_Member_Fingerprint getModelMemberFingerprint() {
        return modelMemberFingerprint;
    }

    public void setModelMemberFingerprint(Model_Member_Fingerprint modelMemberFingerprint) {
        this.modelMemberFingerprint = modelMemberFingerprint;
    }

    public Model_Gym getModelGyms() {
        return modelGyms;
    }

    public void setModelGyms(Model_Gym modelGyms) {
        this.modelGyms = modelGyms;
    }

    public Model_Admin getModelStaffMember() {
        return modelStaffMember;
    }

    public void setModelStaffMember(Model_Admin modelStaffMember) {
        this.modelStaffMember = modelStaffMember;
    }

    public Model_Payment_Membership getModelPaymentMembership() {
        return modelPaymentMembership;
    }

    public void setModelPaymentMembership(Model_Payment_Membership modelPaymentMembership) {
        this.modelPaymentMembership = modelPaymentMembership;
    }

    public Model_Debt getModelDebt() {
        return modelDebt;
    }

    public void setModelDebt(Model_Debt modelDebt) {
        this.modelDebt = modelDebt;
    }
}
