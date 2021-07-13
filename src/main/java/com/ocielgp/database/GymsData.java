package com.ocielgp.database;

import com.ocielgp.database.models.GymsModel;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
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
                Notifications.warn(MethodHandles.lookup().lookupClass().getSimpleName(), "No hay gimnasios registrados.", 5);
            } else {
                return gyms;
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return null;
    }
}
