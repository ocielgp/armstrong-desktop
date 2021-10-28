package com.ocielgp.models;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Model_Debt {
    // max length
    public static final byte MAX_OWE = 9;
    public static final byte MAX_PAID_OUT = 9;
    public static final short MAX_AMOUNT = 255;
    public static final byte MAX_DESCRIPTION = 80;

    private final IntegerProperty idDebt = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> dateTime = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> owe = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> paidOut = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> amount = new SimpleObjectProperty<>(Short.valueOf("0"));
    private final StringProperty description = new SimpleStringProperty();
    private final BooleanProperty isMembership = new SimpleBooleanProperty();
    private final BooleanProperty debtStatus = new SimpleBooleanProperty();
    private final IntegerProperty idAdmin = new SimpleIntegerProperty();
    private final IntegerProperty idMember = new SimpleIntegerProperty();

    public int getIdDebt() {
        return idDebt.get();
    }

    public IntegerProperty idDebtProperty() {
        return idDebt;
    }

    public void setIdDebt(int idDebt) {
        this.idDebt.set(idDebt);
    }

    public LocalDateTime getDateTime() {
        return dateTime.get();
    }

    public ObjectProperty<LocalDateTime> dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime.set(dateTime);
    }

    public BigDecimal getOwe() {
        return owe.get();
    }

    public ObjectProperty<BigDecimal> oweProperty() {
        return owe;
    }

    public void setOwe(BigDecimal owe) {
        this.owe.set(owe);
    }

    public BigDecimal getPaidOut() {
        return paidOut.get();
    }

    public ObjectProperty<BigDecimal> paidOutProperty() {
        return paidOut;
    }

    public void setPaidOut(BigDecimal paidOut) {
        this.paidOut.set(paidOut);
    }

    public Short getAmount() {
        return amount.get();
    }

    public ObjectProperty<Short> amountProperty() {
        return amount;
    }

    public void setAmount(Short amount) {
        this.amount.set(amount);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public boolean isMembership() {
        return isMembership.get();
    }

    public BooleanProperty isMembershipProperty() {
        return isMembership;
    }

    public void setIsMembership(boolean isMembership) {
        this.isMembership.set(isMembership);
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

    public int getIdAdmin() {
        return idAdmin.get();
    }

    public IntegerProperty idAdminProperty() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin.set(idAdmin);
    }

    public int getIdMember() {
        return idMember.get();
    }

    public IntegerProperty idMemberProperty() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember.set(idMember);
    }
}
