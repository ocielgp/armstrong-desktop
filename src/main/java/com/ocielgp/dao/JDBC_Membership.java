package com.ocielgp.dao;

import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class JDBC_Membership {

    public static CompletableFuture<ObservableList<Model_Membership>> ReadMemberships() {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            ObservableList<Model_Membership> modelMembershipsList = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idMembership, price, description FROM MEMBERSHIPS WHERE flag = 1");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Membership modelMembership = new Model_Membership();
                    modelMembership.setIdMembership(rs.getInt("idMembership"));
                    modelMembership.setPrice(rs.getBigDecimal("price"));
                    modelMembership.setDescription(rs.getString("description"));
                    modelMembershipsList.add(modelMembership);
                }
                if (modelMembershipsList.isEmpty()) {
                    Notifications.Danger("Membresías", "No hay membresías registradas");
                }
                con.close();
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelMembershipsList;
        });
    }
}
