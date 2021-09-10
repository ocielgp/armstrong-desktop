package com.ocielgp.database.members;

import com.ocielgp.database.payments.MODEL_DEBTS;
import com.ocielgp.database.payments.MODEL_PAYMENTS_MEMBERSHIPS;
import com.ocielgp.database.staff.MODEL_STAFF_MEMBERS;
import com.ocielgp.database.system.MODEL_GYMS;
import com.ocielgp.utilities.Styles;
import javafx.beans.property.*;

public class MODEL_MEMBERS {
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
    private final BooleanProperty access = new SimpleBooleanProperty();
    private final IntegerProperty idGym = new SimpleIntegerProperty();

    private final StringProperty endDate = new SimpleStringProperty();
    private Styles style;

    // members
    private MODEL_MEMBERS_PHOTOS modelMembersPhotos;
    private MODEL_MEMBERS_FINGERPRINTS modelMembersFingerprints;

    // gym
    private MODEL_GYMS modelGyms;

    // staff
    private MODEL_STAFF_MEMBERS modelStaffMembers;

    // payments
    private MODEL_PAYMENTS_MEMBERSHIPS modelPaymentsMemberships;

    // debts
    private MODEL_DEBTS modelDebtsMemberships;

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

    public Styles getStyle() {
        return style;
    }

    public void setStyle(Styles style) {
        this.style = style;
    }

    public MODEL_MEMBERS_PHOTOS getModelMembersPhotos() {
        return modelMembersPhotos;
    }

    public void setModelMembersPhotos(MODEL_MEMBERS_PHOTOS modelMembersPhotos) {
        this.modelMembersPhotos = modelMembersPhotos;
    }

    public MODEL_MEMBERS_FINGERPRINTS getModelMembersFingerprints() {
        return modelMembersFingerprints;
    }

    public void setModelMembersFingerprints(MODEL_MEMBERS_FINGERPRINTS modelMembersFingerprints) {
        this.modelMembersFingerprints = modelMembersFingerprints;
    }

    public MODEL_GYMS getModelGyms() {
        return modelGyms;
    }

    public void setModelGyms(MODEL_GYMS modelGyms) {
        this.modelGyms = modelGyms;
    }

    public MODEL_STAFF_MEMBERS getModelStaffMembers() {
        return modelStaffMembers;
    }

    public void setModelStaffMembers(MODEL_STAFF_MEMBERS modelStaffMembers) {
        this.modelStaffMembers = modelStaffMembers;
    }

    public MODEL_PAYMENTS_MEMBERSHIPS getModelPaymentsMemberships() {
        return modelPaymentsMemberships;
    }

    public void setModelPaymentsMemberships(MODEL_PAYMENTS_MEMBERSHIPS modelPaymentsMemberships) {
        this.modelPaymentsMemberships = modelPaymentsMemberships;
    }

    public MODEL_DEBTS getModelDebtsMemberships() {
        return modelDebtsMemberships;
    }

    public void setModelDebtsMemberships(MODEL_DEBTS modelDebtsMemberships) {
        this.modelDebtsMemberships = modelDebtsMemberships;
    }
}
