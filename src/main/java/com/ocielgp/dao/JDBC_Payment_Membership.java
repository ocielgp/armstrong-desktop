package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.models.Model_Payment_Membership;
import com.ocielgp.utilities.DateFormatter;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class JDBC_Payment_Membership {
    public static CompletableFuture<Integer> CreatePaymentMembership(int idMember, Model_Membership modelMembership) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("INSERT INTO PAYMENTS_MEMBERSHIPS(days, price, idGym, idStaff, idMember, idMembership)" +
                                "VALUE (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, modelMembership.getDays()); // days
                ps.setBigDecimal(2, modelMembership.getPrice()); // payment
                ps.setInt(3, Application.getCurrentGym().getIdGym()); // idGym
                ps.setInt(4, Application.getStaffUserModel().getIdMember()); // idStaff
                ps.setInt(5, idMember); // idMember
                ps.setInt(6, modelMembership.getIdMembership()); // idMembership
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) { // Return new idPaymentMembership
                    return rs.getInt(1);
                }
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
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
                ps = con.prepareStatement("SELECT idPaymentMembership, startDateTime, endDateTime, idGym, idStaff, idMembership FROM PAYMENTS_MEMBERSHIPS WHERE flag = 1 AND idMember = ? ORDER BY startDateTime DESC LIMIT 1");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelPaymentMembership = new Model_Payment_Membership();
                    modelPaymentMembership.setIdPaymentMembership(rs.getInt("idPaymentMembership"));
                    modelPaymentMembership.setStartDateTime(DateTime.MySQLToJava(rs.getString("startDateTime")));
                    modelPaymentMembership.setEndDateTime(DateTime.MySQLToJava(rs.getString("endDateTime")));
                    modelPaymentMembership.setIdGym(rs.getInt("idGym"));
                    modelPaymentMembership.setIdStaff(rs.getInt("idStaff"));
                    modelPaymentMembership.setIdMembership(rs.getInt("idMembership"));
                }
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelPaymentMembership;
        });
    }

    public static void UpdatePaymentMembership(int idMember, Model_Membership modelMembership) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                // clear previous debt if exists
                assert con != null;
                ps = con.prepareStatement("DELETE FROM DEBTS WHERE DATE(dateTime) = CURRENT_DATE AND idMember = ? AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC LIMIT 1");
                ps.setInt(1, idMember);
                ps.executeUpdate();

                ps = con.prepareStatement("UPDATE PAYMENTS_MEMBERSHIPS SET payment = ?, endDateTime = ?, idGym = ?, idStaff = ?, idMembership = ? WHERE DATE(startDateTime) = CURRENT_DATE AND idMember = ?");
                ps.setBigDecimal(1, modelMembership.getPrice()); // payment
                System.out.println("a" + DateFormatter.mysqlDateTime(LocalDateTime.now().plusDays(modelMembership.getDays())));
                ps.setString(2, DateFormatter.mysqlDateTime(LocalDateTime.now().plusDays(modelMembership.getDays()))); // endDateTime
                ps.setInt(3, Application.getCurrentGym().getIdGym()); // idGym
                ps.setInt(4, Application.getStaffUserModel().getIdMember()); // idStaff
                ps.setInt(5, modelMembership.getIdMembership()); // idMembership
                ps.setInt(6, idMember); // idMember
                ps.executeUpdate();
                System.out.println("actualizar");
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }
}
