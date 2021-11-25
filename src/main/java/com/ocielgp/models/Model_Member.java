package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

public class Model_Member {
    // Max length
    public static final byte nameLength = 30;
    public static final byte lastNameLength = 30;
    public static final byte notesLength = 80;

    private final ObjectProperty<Integer> idMember = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty gender = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final ObjectProperty<Boolean> access = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> idGym = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> createdBy = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> updatedBy = new SimpleObjectProperty<>();

    private final StringProperty endDate = new SimpleStringProperty();
    private String style;

    // members
    private Model_Member_Photo modelMemberPhoto;
    private Model_Member_Fingerprint modelMemberFingerprint;

    // gym
    private Model_Gym modelGym;

    // payments
    private Model_Payment_Membership modelPaymentMembership;

    // debts
    private Model_Debt modelDebt;

    public Integer getIdMember() {
        return idMember.get();
    }

    public ObjectProperty<Integer> idMemberProperty() {
        return idMember;
    }

    public void setIdMember(Integer idMember) {
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

    public Boolean getAccess() {
        return access.get();
    }

    public ObjectProperty<Boolean> accessProperty() {
        return access;
    }

    public void setAccess(Boolean access) {
        this.access.set(access);
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

    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    public Integer getCreatedBy() {
        return createdBy.get();
    }

    public ObjectProperty<Integer> createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy.set(createdBy);
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public Integer getUpdatedBy() {
        return updatedBy.get();
    }

    public ObjectProperty<Integer> updatedByProperty() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy.set(updatedBy);
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

    public Model_Gym getModelGym() {
        return modelGym;
    }

    public void setModelGym(Model_Gym modelGym) {
        this.modelGym = modelGym;
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
