package com.ocielgp.model;

import javafx.beans.property.*;

public class AdministradorModel {
    private final IntegerProperty idRecepcionista = new SimpleIntegerProperty();
    private final StringProperty nombres = new SimpleStringProperty();
    private final StringProperty apellidos = new SimpleStringProperty();
    private final IntegerProperty idRol = new SimpleIntegerProperty();

    public int getIdRecepcionista() {
        return idRecepcionista.get();
    }

    public IntegerProperty idRecepcionistaProperty() {
        return idRecepcionista;
    }

    public void setIdRecepcionista(int idRecepcionista) {
        this.idRecepcionista.set(idRecepcionista);
    }

    public String getNombres() {
        return nombres.get();
    }

    public StringProperty nombresProperty() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres.set(nombres);
    }

    public String getApellidos() {
        return apellidos.get();
    }

    public StringProperty apellidosProperty() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos.set(apellidos);
    }

    public int getIdRol() {
        return idRol.get();
    }

    public IntegerProperty idRolProperty() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol.set(idRol);
    }
}
