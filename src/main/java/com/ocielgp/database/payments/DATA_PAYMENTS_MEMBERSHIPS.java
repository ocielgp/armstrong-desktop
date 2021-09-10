package com.ocielgp.database.payments;

import com.ocielgp.app.GlobalController;
import com.ocielgp.database.DataServer;
import com.ocielgp.database.memeberships.MODEL_MEMBERSHIPS;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class DATA_PAYMENTS_MEMBERSHIPS {
    public static CompletableFuture<Integer> CreatePaymentMembership(int idMember, MODEL_MEMBERSHIPS modelMemberships) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            ResultSet rs;
            try {
                ps = con.prepareStatement("INSERT INTO PAYMENTS_MEMBERSHIPS(payment, startDate, endDate, idGym, idStaff, idMember, idMembership) VALUE (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setDouble(1, modelMemberships.getPrice()); // payment
                ps.setString(2, String.valueOf(LocalDate.now())); // startDate
                ps.setString(3, String.valueOf(LocalDate.now().plusDays(modelMemberships.getDays()))); // endDate
                ps.setInt(4, GlobalController.getCurrentGym().getIdGym()); // idGym
                ps.setInt(5, GlobalController.getStaffUserModel().getIdMember()); // idStaff
                ps.setInt(6, idMember); // idMember
                ps.setInt(7, modelMemberships.getIdMembership()); // idMembership
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) { // Return new idPaymentMembership
                    return rs.getInt(1);
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
            return 0;
        });
    }

    public static CompletableFuture<MODEL_PAYMENTS_MEMBERSHIPS> getLastPayment(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            ResultSet rs;
            MODEL_PAYMENTS_MEMBERSHIPS modelPaymentsMemberships = null;
            try {
                ps = con.prepareStatement("SELECT idPaymentMembership, startDate, endDate, idGym, idStaff, idMembership FROM PAYMENTS_MEMBERSHIPS WHERE flag = 1 AND idMember = ? ORDER BY startDate DESC LIMIT 1");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelPaymentsMemberships = new MODEL_PAYMENTS_MEMBERSHIPS();
                    modelPaymentsMemberships.setIdPaymentMembership(rs.getInt("idPaymentMembership"));
                    modelPaymentsMemberships.setStartDate(rs.getString("startDate"));
                    modelPaymentsMemberships.setEndDate(rs.getString("endDate"));
                    modelPaymentsMemberships.setIdGym(rs.getInt("idGym"));
                    modelPaymentsMemberships.setIdStaff(rs.getInt("idStaff"));
                    modelPaymentsMemberships.setIdMembership(rs.getInt("idMembership"));
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
            return modelPaymentsMemberships;
        });
    }
}
