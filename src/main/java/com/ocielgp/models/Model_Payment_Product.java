package com.ocielgp.models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Model_Payment_Product extends Model_Product {
    private final ObjectProperty<Integer> idPaymentProduct = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> idProduct = new SimpleObjectProperty<>();
    private final ObjectProperty<Short> idGym = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> createdBy = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> updatedBy = new SimpleObjectProperty<>();

    public Integer getIdPaymentProduct() {
        return idPaymentProduct.get();
    }

    public ObjectProperty<Integer> idPaymentProductProperty() {
        return idPaymentProduct;
    }

    public void setIdPaymentProduct(Integer idPaymentProduct) {
        this.idPaymentProduct.set(idPaymentProduct);
    }

    @Override
    public BigDecimal getPrice() {
        return price.get();
    }

    @Override
    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    @Override
    public Integer getIdProduct() {
        return idProduct.get();
    }

    @Override
    public ObjectProperty<Integer> idProductProperty() {
        return idProduct;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct.set(idProduct);
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

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt.get();
    }

    @Override
    public ObjectProperty<LocalDateTime> createdAtProperty() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt.set(createdAt);
    }

    @Override
    public Integer getCreatedBy() {
        return createdBy.get();
    }

    @Override
    public ObjectProperty<Integer> createdByProperty() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy.set(createdBy);
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    @Override
    public ObjectProperty<LocalDateTime> updatedAtProperty() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt.set(updatedAt);
    }

    @Override
    public Integer getUpdatedBy() {
        return updatedBy.get();
    }

    @Override
    public ObjectProperty<Integer> updatedByProperty() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy.set(updatedBy);
    }
}
