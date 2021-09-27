package com.ocielgp.models;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class Model_Debt {
    // Max length
    public static final byte oweLength = 10;
    public static final byte notesLength = 80;

    private final SimpleIntegerProperty idDebt = new SimpleIntegerProperty();
    private final SimpleStringProperty dateTime = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> paidOut = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> owe = new SimpleObjectProperty<>();
    private final IntegerProperty amount = new SimpleIntegerProperty();
    private final SimpleStringProperty description = new SimpleStringProperty();
    private final BooleanProperty debtStatus = new SimpleBooleanProperty();
    private final SimpleIntegerProperty idStaff = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idMember = new SimpleIntegerProperty();
    private final SimpleIntegerProperty idDebtType = new SimpleIntegerProperty();

    private final ObjectProperty<BigDecimal> totalOwe = new SimpleObjectProperty<>();

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

    public BigDecimal getPaidOut() {
        return paidOut.get();
    }

    public void setPaidOut(BigDecimal paidOut) {
        this.paidOut.set(paidOut);
    }

    public ObjectProperty<BigDecimal> paidOutProperty() {
        return paidOut;
    }

    public BigDecimal getOwe() {
        return owe.get();
    }

    public void setOwe(BigDecimal owe) {
        this.owe.set(owe);
    }

    public ObjectProperty<BigDecimal> oweProperty() {
        return owe;
    }

    public int getAmount() {
        return amount.get();
    }

    public IntegerProperty amountProperty() {
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

    public BigDecimal getTotalOwe() {
        return totalOwe.get();
    }

    public void setTotalOwe(BigDecimal totalOwe) {
        this.totalOwe.set(totalOwe);
    }

    public ObjectProperty<BigDecimal> totalOweProperty() {
        return totalOwe;
    }
}
