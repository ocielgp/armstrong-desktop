package com.ocielgp.dao;

import com.ocielgp.models.Model_Gym;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class JDBC_Gym {
    public static CompletableFuture<Model_Gym> ReadGym(int idGym) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            Model_Gym modelGym = null;
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idGym, name, address FROM GYMS WHERE idGym = ?");
                ps.setInt(1, idGym);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelGym = new Model_Gym();
                    modelGym.setIdGym(rs.getInt("idGym"));
                    modelGym.setName(rs.getString("name"));
                    modelGym.setAddress(rs.getString("address"));
                }
            } catch (SQLException sqlException) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelGym;
        });
    }

    public static CompletableFuture<ObservableList<Model_Gym>> ReadGyms() {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            ObservableList<Model_Gym> modelGymsList = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idGym, name, address FROM GYMS WHERE flag = 1");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Gym modelGym = new Model_Gym();
                    modelGym.setIdGym(rs.getInt("idGym"));
                    modelGym.setName(rs.getString("name"));
                    modelGym.setAddress(rs.getString("address"));
                    modelGymsList.add(modelGym);
                }
                if (modelGymsList.isEmpty()) {
                    Notifications.Warn("Gimnasios", "No hay gimnasios registrados", 5);
                }
            } catch (SQLException sqlException) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelGymsList;
        });
    }
}
