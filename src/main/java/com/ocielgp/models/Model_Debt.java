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
    private final ObjectProperty<BigDecimal> owe = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> paidOut = new SimpleObjectProperty<>();
    private final BooleanProperty debtStatus = new SimpleBooleanProperty();
    private final IntegerProperty idMember = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final IntegerProperty updatedBy = new SimpleIntegerProperty();

    public int getIdDebt() {
        return idDebt.get();
    }

    public IntegerProperty idDebtProperty() {
        return idDebt;
    }

    public void setIdDebt(int idDebt) {
        this.idDebt.set(idDebt);
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

    public boolean isDebtStatus() {
        return debtStatus.get();
    }

    public BooleanProperty debtStatusProperty() {
        return debtStatus;
    }

    public void setDebtStatus(boolean debtStatus) {
        this.debtStatus.set(debtStatus);
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    public int getUpdatedBy() {
        return updatedBy.get();
    }

    public IntegerProperty updatedByProperty() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy.set(updatedBy);
    }
}
