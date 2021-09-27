package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.database.DataServer;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.models.Model_Payment_Membership;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class JDBC_Payment_Membership {
    public static CompletableFuture<Integer> CreatePaymentMembership(int idMember, Model_Membership modelMembership) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("INSERT INTO PAYMENTS_MEMBERSHIPS(payment, startDate, endDate, idGym, idStaff, idMember, idMembership) VALUE (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setBigDecimal(1, modelMembership.getPrice()); // payment
                ps.setString(2, String.valueOf(LocalDate.now())); // startDate
                ps.setString(3, String.valueOf(LocalDate.now().plusDays(modelMembership.getDays()))); // endDate
                ps.setInt(4, Application.getCurrentGym().getIdGym()); // idGym
                ps.setInt(5, Application.getStaffUserModel().getIdMember()); // idStaff
                ps.setInt(6, idMember); // idMember
                ps.setInt(7, modelMembership.getIdMembership()); // idMembership
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) { // Return new idPaymentMembership
                    return rs.getInt(1);
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return 0;
        });
    }

    public static CompletableFuture<Model_Payment_Membership> ReadLastPayment(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Model_Payment_Membership modelPaymentMembership = null;
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idPaymentMembership, startDate, endDate, idGym, idStaff, idMembership FROM PAYMENTS_MEMBERSHIPS WHERE flag = 1 AND idMember = ? ORDER BY startDate DESC LIMIT 1");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelPaymentMembership = new Model_Payment_Membership();
                    modelPaymentMembership.setIdPaymentMembership(rs.getInt("idPaymentMembership"));
                    modelPaymentMembership.setStartDate(rs.getString("startDate"));
                    modelPaymentMembership.setEndDate(rs.getString("endDate"));
                    modelPaymentMembership.setIdGym(rs.getInt("idGym"));
                    modelPaymentMembership.setIdStaff(rs.getInt("idStaff"));
                    modelPaymentMembership.setIdMembership(rs.getInt("idMembership"));
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelPaymentMembership;
        });
    }
}
