package com.ocielgp.database;

import com.ocielgp.model.AdministradorModel;
import com.ocielgp.model.SociosPlanesModel;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.NotificationHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SocioData {

    public static ObservableList<SociosPlanesModel> getSociosPlanes() {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT * FROM Socios_Planes WHERE flag = TRUE");
            rs = ps.executeQuery();
            ObservableList<SociosPlanesModel> subscriptions = FXCollections.observableArrayList();
            while (rs.next()) {
                SociosPlanesModel subscription = new SociosPlanesModel();
                subscription.setIdPlan(rs.getInt("idPlan"));
                subscription.setPrecio(rs.getDouble("precio"));
                subscription.setDescripcion(rs.getString("descripcion"));
                subscription.setDias(rs.getInt("dias"));

                subscriptions.add(subscription);
            }
            if (subscriptions.isEmpty()) {
                NotificationHandler.warn("Planes", "No hay planes registrados", 2);
            } else {
                return subscriptions;
            }
            return subscriptions;
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[SocioData]Error al obtener Socios Planes", 5);
            throwables.printStackTrace();
            return null;
        }
    }
}
