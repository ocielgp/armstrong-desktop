package com.ocielgp.database.memeberships;

import com.ocielgp.app.GlobalController;
import com.ocielgp.database.DataServer;
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

public class DATA_MEMBERSHIPS {

    public static CompletableFuture<ObservableList<MODEL_MEMBERSHIPS>> ReadMemberships() {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            ResultSet rs;
            ObservableList<MODEL_MEMBERSHIPS> modelMembershipsList = FXCollections.observableArrayList();
            try {
                ps = con.prepareStatement("SELECT idMembership, days, price, description FROM MEMBERSHIPS WHERE flag = 1");
                rs = ps.executeQuery();
                while (rs.next()) {
                    MODEL_MEMBERSHIPS modelMemberships = new MODEL_MEMBERSHIPS();
                    modelMemberships.setIdMembership(rs.getInt("idMembership"));
                    modelMemberships.setDays(rs.getInt("days"));
                    modelMemberships.setPrice(rs.getDouble("price"));
                    modelMemberships.setDescription(rs.getString("description"));
                    modelMembershipsList.add(modelMemberships);
                }
                if (modelMembershipsList.isEmpty()) {
                    Notifications.danger("Membresías", "No hay membresías registradas.");
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
            return modelMembershipsList;
        });
    }

    /* Update section */
    public static void UpdateMembership(int idMember, MODEL_MEMBERSHIPS modelMemberships) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            try {
                // Clear previous debt if exists
                ps = con.prepareStatement("DELETE FROM DEBTS WHERE DATE(dateTime) = CURDATE() AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC LIMIT 1");
                ps.executeUpdate();

                ps = con.prepareStatement("UPDATE PAYMENTS_MEMBERSHIPS SET payment = ?, endDate = ?, idGym = ?, idStaff = ?, idMembership = ? WHERE startDate = CURRENT_DATE() AND idMember = ?");
                ps.setDouble(1, modelMemberships.getPrice()); // price
                ps.setString(2, String.valueOf(LocalDate.now().plusDays(modelMemberships.getDays()))); // endDate
                ps.setInt(3, GlobalController.getCurrentGym().getIdGym()); // idGym
                ps.setInt(4, GlobalController.getStaffUserModel().getIdMember()); // idStaff
                ps.setInt(5, modelMemberships.getIdMembership()); // idMembership
                ps.setInt(6, idMember); // idMember
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
        });
    }
}
