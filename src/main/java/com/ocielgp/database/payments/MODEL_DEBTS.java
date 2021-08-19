package com.ocielgp.database.payments;

import javafx.beans.property.*;

public class MODEL_DEBTS {
    // Max length
    public static final byte oweLength = 10;
    public static final byte notesLength = 80;

    private final SimpleIntegerProperty idDebt = new SimpleIntegerProperty();
    private final SimpleStringProperty dateTime = new SimpleStringProperty();
    private final SimpleDoubleProperty paidOut = new SimpleDoubleProperty();
    private final SimpleDoubleProperty owe = new SimpleDoubleProperty();
    private final SimpleIntegerProperty amount = new SimpleIntegerProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final BooleanProperty debtStatus = new SimpleBooleanProperty();
    private final SimpleIntegerProperty idStaff = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idDebtType = new SimpleIntegerProperty();

    private final SimpleDoubleProperty totalOwe = new SimpleDoubleProperty();

    public int getIdDebt() {
        return idDebt.get();
    }

    public SimpleIntegerProperty idDebtProperty() {
        return idDebt;
    }

    public void setIdDebt(int idDebt) {
        this.idDebt.set(idDebt);
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

    public int getAmount() {
        return amount.get();
    }

    public SimpleIntegerProperty amountProperty() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
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

    public boolean isDebtStatus() {
        return debtStatus.get();
    }

    public BooleanProperty debtStatusProperty() {
        return debtStatus;
    }

    public void setDebtStatus(boolean debtStatus) {
        this.debtStatus.set(debtStatus);
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

    public int getIdMember() {
        return idMember.get();
    }

    public SimpleIntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }

    public int getIdDebtType() {
        return idDebtType.get();
    }

    public SimpleIntegerProperty idDebtTypeProperty() {
        return idDebtType;
    }

    public void setIdDebtType(int idDebtType) {
        this.idDebtType.set(idDebtType);
    }

    public double getTotalOwe() {
        return totalOwe.get();
    }

    public SimpleDoubleProperty totalOweProperty() {
        return totalOwe;
    }

    public void setTotalOwe(double totalOwe) {
        this.totalOwe.set(totalOwe);
    }
}
