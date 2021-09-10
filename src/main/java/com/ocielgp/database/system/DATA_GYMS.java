package com.ocielgp.database.system;

import com.ocielgp.database.DataServer;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DATA_GYMS {
    public static CompletableFuture<MODEL_GYMS> ReadGym(int idGym) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            ResultSet rs;
            MODEL_GYMS modelGyms = null;
            try {
                ps = con.prepareStatement("SELECT idGym, name, address FROM GYMS WHERE idGym = ?");
                ps.setInt(1, idGym);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelGyms = new MODEL_GYMS();
                    modelGyms.setIdGym(rs.getInt("idGym"));
                    modelGyms.setName(rs.getString("name"));
                    modelGyms.setAddress(rs.getString("address"));
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(
                        MethodHandles.lookup().lookupClass().getSimpleName(),
                        Thread.currentThread().getStackTrace()[1],
                        "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                        sqlException
                );
            }
            return modelGyms;
        });
    }

    public static ObservableList<MODEL_GYMS> ReadGyms() {
        Connection con = DataServer.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        ObservableList<MODEL_GYMS> modelGymsList = FXCollections.observableArrayList();
        try {
            ps = con.prepareStatement("SELECT idGym, name, address FROM GYMS WHERE flag = 1");
            rs = ps.executeQuery();
            while (rs.next()) {
                MODEL_GYMS modelGyms = new MODEL_GYMS();
                modelGyms.setIdGym(rs.getInt("idGym"));
                modelGyms.setName(rs.getString("name"));
                modelGyms.setAddress(rs.getString("address"));
                modelGymsList.add(modelGyms);
            }
            if (modelGymsList.isEmpty()) {
                Notifications.warn(MethodHandles.lookup().lookupClass().getSimpleName(), "No hay gimnasios registrados.", 5);
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return modelGymsList;
    }
}
