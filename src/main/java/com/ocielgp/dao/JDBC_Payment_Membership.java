package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.models.Model_Payment_Membership;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class JDBC_Payment_Membership {
    public static int CreatePaymentMembership(int idMember, Model_Membership modelMembership, short months) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            LocalDateTime now = LocalDateTime.now();
            ps = con.prepareStatement("INSERT INTO PAYMENTS_MEMBERSHIPS(months, price, startDateTime, endDateTime, idGym, idAdmin, idMember, idMembership)" +
                            "VALUE (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            System.out.println("endatetime: " + DateTime.getEndDateToMySQL(now, months));
            ps.setShort(1, months); // months
            ps.setBigDecimal(2, modelMembership.getPrice()); // price
            ps.setString(3, DateTime.JavaToMySQLDateTime(now)); // startDateTime
            ps.setString(4, DateTime.getEndDateToMySQL(now, months)); // endDateTime
            ps.setInt(5, Application.getCurrentGym().getIdGym()); // idGym
            ps.setInt(6, Application.getModelAdmin().getIdMember()); // idAdmin
            ps.setInt(7, idMember); // idMember
            ps.setInt(8, modelMembership.getIdMembership()); // idMembership
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) { // return new idPaymentMembership
                return rs.getInt(1);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
        return 0;
    }

    public static CompletableFuture<Model_Payment_Membership> ReadLastPayment(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Model_Payment_Membership modelPaymentMembership = null;
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idPaymentMembership, price, startDateTime, endDateTime, idGym, idAdmin, idMembership FROM PAYMENTS_MEMBERSHIPS WHERE flag = 1 AND idMember = ? ORDER BY startDateTime DESC LIMIT 1");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelPaymentMembership = new Model_Payment_Membership();
                    modelPaymentMembership.setIdPaymentMembership(rs.getInt("idPaymentMembership"));
                    modelPaymentMembership.setPrice(rs.getBigDecimal("price"));
                    modelPaymentMembership.setStartDateTime(DateTime.MySQLToJava(rs.getString("startDateTime")));
                    modelPaymentMembership.setEndDateTime(DateTime.MySQLToJava(rs.getString("endDateTime")));
                    modelPaymentMembership.setIdGym(rs.getShort("idGym"));
                    modelPaymentMembership.setIdAdmin(rs.getInt("idAdmin"));
                    modelPaymentMembership.setIdMember(idMember);
                    modelPaymentMembership.setIdMembership(rs.getInt("idMembership"));
                }
            } catch (SQLException sqlException) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelPaymentMembership;
        });
    }

    public static boolean UpdatePaymentMembership(Model_Membership modelMembership, Model_Payment_Membership modelPaymentMembership) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            // disable previous debt if exists
            assert con != null;
            ps = con.prepareStatement("UPDATE DEBTS SET flag = 0 WHERE DATE(dateTime) = ? AND idMember = ? AND isMembership = 1 ORDER BY dateTime DESC");
            ps.setString(1, DateTime.JavaToMySQLDate(modelPaymentMembership.getStartDateTime()));
            ps.setInt(2, modelPaymentMembership.getIdMember());
            ps.executeUpdate();

            ps = con.prepareStatement("UPDATE PAYMENTS_MEMBERSHIPS SET price = ?, startDateTime = NOW(), idGym = ?, idAdmin = ?, idMembership = ? WHERE idPaymentMembership = ?");
            ps.setBigDecimal(1, modelMembership.getPrice()); // price
            ps.setInt(2, Application.getCurrentGym().getIdGym()); // idGym
            ps.setInt(3, Application.getModelAdmin().getIdMember()); // idAdmin
            ps.setInt(4, modelMembership.getIdMembership()); // idMembership
            ps.setInt(5, modelPaymentMembership.getIdPaymentMembership());
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            return false;
        } finally {
            DataServer.closeConnection(con);
        }
    }

    public static boolean DeletePaymentMembership(Model_Payment_Membership modelPaymentMembership) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            // disable previous debt if exists
            ps = con.prepareStatement("UPDATE DEBTS SET flag = 0 WHERE DATE(dateTime) = ? AND idMember = ? AND isMembership = 1 ORDER BY dateTime DESC");
            ps.setString(1, DateTime.JavaToMySQLDate(modelPaymentMembership.getStartDateTime()));
            ps.setInt(2, modelPaymentMembership.getIdMember());
            ps.executeUpdate();

            ps = con.prepareStatement("UPDATE PAYMENTS_MEMBERSHIPS SET flag = 0 WHERE idPaymentMembership = ?");
            ps.setInt(1, modelPaymentMembership.getIdPaymentMembership());
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            return false;
        } finally {
            DataServer.closeConnection(con);
        }
    }
}
