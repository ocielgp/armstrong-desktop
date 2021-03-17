package com.ocielgp.database;

import com.ocielgp.model.GymsModel;
import com.ocielgp.utilities.NotificationHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GymsData {
    public static ObservableList<GymsModel> getGyms() {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT idGym, name, address FROM GYMS WHERE flag = 1");
            rs = ps.executeQuery();
            ObservableList<GymsModel> gyms = FXCollections.observableArrayList();
            while (rs.next()) {
                GymsModel gym = new GymsModel();
                gym.setIdGym(rs.getInt("idGym"));
                gym.setName(rs.getString("name"));
                gym.setAddress(rs.getString("address"));

                gyms.add(gym);
            }
            if (gyms.isEmpty()) {
                NotificationHandler.warn("Gyms", "No hay gimnasios registradas.", 2);
                return null;
            } else {
                return gyms;
            }
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[GymsData][getGyms]: Error al obtener Gyms.", 5);
            throwables.printStackTrace();
            return null;
        }
    }
}
