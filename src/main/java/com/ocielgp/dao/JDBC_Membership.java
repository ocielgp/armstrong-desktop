package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
                ps = con.prepareStatement("SELECT idMembership, days, price, description FROM MEMBERSHIPS WHERE flag = 1");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Membership modelMembership = new Model_Membership();
                    modelMembership.setIdMembership(rs.getInt("idMembership"));
                    modelMembership.setDays(rs.getInt("days"));
                    modelMembership.setPrice(rs.getBigDecimal("price"));
                    modelMembership.setDescription(rs.getString("description"));
                    modelMembershipsList.add(modelMembership);
                }
                if (modelMembershipsList.isEmpty()) {
                    Notifications.danger("Membresías", "No hay membresías registradas");
                }
                con.close();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelMembershipsList;
        });
    }

    /* Update section */
    public static void UpdateMembership(int idMember, Model_Membership modelMembership) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                // Clear previous debt if exists
                ps = con.prepareStatement("DELETE FROM DEBTS WHERE DATE(dateTime) = CURDATE() AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC LIMIT 1");
                ps.executeUpdate();

                ps = con.prepareStatement("UPDATE PAYMENTS_MEMBERSHIPS SET payment = ?, endDate = ?, idGym = ?, idStaff = ?, idMembership = ? WHERE startDate = CURRENT_DATE() AND idMember = ?");
                ps.setBigDecimal(1, modelMembership.getPrice()); // price
                ps.setString(2, String.valueOf(LocalDate.now().plusDays(modelMembership.getDays()))); // endDate
                ps.setInt(3, Application.getCurrentGym().getIdGym()); // idGym
                ps.setInt(4, Application.getStaffUserModel().getIdMember()); // idStaff
                ps.setInt(5, modelMembership.getIdMembership()); // idMembership
                ps.setInt(6, idMember); // idMember
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }
}
