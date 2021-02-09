package com.ocielgp.model;

import javafx.beans.property.*;

public class SociosPlanesModel {
    private final IntegerProperty idPlan = new SimpleIntegerProperty();
    private final SimpleDoubleProperty precio = new SimpleDoubleProperty();
    private final StringProperty descripcion = new SimpleStringProperty();
    private final IntegerProperty dias = new SimpleIntegerProperty();

    public int getIdPlan() {
        return idPlan.get();
    }

    public IntegerProperty idPlanProperty() {
        return idPlan;
    }

    public void setIdPlan(int idPlan) {
        this.idPlan.set(idPlan);
    }

    public double getPrecio() {
        return precio.get();
    }

    public SimpleDoubleProperty precioProperty() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio.set(precio);
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public int getDias() {
        return dias.get();
    }

    public IntegerProperty diasProperty() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias.set(dias);
    }

    @Override
    public String toString() {
        return "[$" + getPrecio() + "]: " + getDescripcion() + " (" + getDias() + ") dias";
    }
}
