package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.models.Model_Payment_Membership;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.ParamBuilder;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class JDBC_Payment_Membership {
    public static int CreatePaymentMembership(int idMember, Model_Membership modelMembership, short months, boolean isFirstMembership) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            LocalDateTime now = LocalDateTime.now();
            ps = con.prepareStatement("INSERT INTO PAYMENTS_MEMBERSHIPS(months, price, startDateTime, endDateTime, firstMembership, idGym, createdBy, idMember, idMembership)" +
                            "VALUE (?, ?, ?, ?,?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setShort(1, months); // months
            ps.setBigDecimal(2, modelMembership.getPrice()); // price
            ps.setString(3, DateTime.JavaToMySQLDateTime(now)); // startDateTime
            ps.setString(4, DateTime.getEndDateToMySQL(now, months)); // endDateTime
            ps.setBoolean(5, isFirstMembership); // firstMembership
            ps.setInt(6, Application.GetCurrentGym().getIdGym()); // idGym
            ps.setInt(7, Application.GetModelAdmin().getIdMember()); // idAdmin
            ps.setInt(8, idMember); // idMember
            ps.setInt(9, modelMembership.getIdMembership()); // idMembership
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) { // return new idPaymentMembership
                return rs.getInt(1);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return 0;
    }

    public static CompletableFuture<Model_Payment_Membership> ReadLastPayment(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Model_Payment_Membership modelPaymentMembership = null;
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idPaymentMembership, months, price, startDateTime, endDateTime, firstMembership, idGym, idMembership, createdAt, createdBy, updatedAt, updatedBy FROM PAYMENTS_MEMBERSHIPS WHERE flag = 1 AND idMember = ? ORDER BY createdAt DESC LIMIT 1");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelPaymentMembership = new Model_Payment_Membership();
                    modelPaymentMembership.setIdPaymentMembership(rs.getInt("idPaymentMembership"));
                    modelPaymentMembership.setMonths(rs.getShort("months"));
                    modelPaymentMembership.setPrice(rs.getBigDecimal("price"));
                    modelPaymentMembership.setStartDateTime(DateTime.MySQLToJava(rs.getString("startDateTime")));
                    modelPaymentMembership.setEndDateTime(DateTime.MySQLToJava(rs.getString("endDateTime")));
                    modelPaymentMembership.setFirstMembership(rs.getBoolean("firstMembership"));
                    modelPaymentMembership.setIdGym(rs.getShort("idGym"));
                    modelPaymentMembership.setIdMembership(rs.getInt("idMembership"));
                    modelPaymentMembership.setCreatedAt(DateTime.MySQLToJava(rs.getString("createdAt")));
                    modelPaymentMembership.setCreatedBy(rs.getInt("createdBy"));
                    modelPaymentMembership.setUpdatedAt(DateTime.MySQLToJava(rs.getString("updatedAt")));
                    modelPaymentMembership.setUpdatedBy(rs.getInt("updatedBy"));
                    modelPaymentMembership.setIdMember(idMember);
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return modelPaymentMembership;
        });
    }

    public static boolean UpdatePaymentMembership(Model_Membership modelMembership, Model_Payment_Membership modelPaymentMembership) {
        // disable previous debt if exists
        boolean isOk = JDBC_Debt.DeleteDebt(modelPaymentMembership.getIdMember());
        if (isOk) {
            LocalDateTime now = LocalDateTime.now();
            ParamBuilder paramBuilder = new ParamBuilder("PAYMENTS_MEMBERSHIPS", "idPaymentMembership", modelPaymentMembership.getIdPaymentMembership());
            paramBuilder.addParam("months", modelPaymentMembership.getMonths());
            paramBuilder.addParam("price", modelMembership.getPrice());
            paramBuilder.addParam("startDateTime", DateTime.JavaToMySQLDateTime(now));
            paramBuilder.addParam("endDateTime", DateTime.getEndDateToMySQL(now, modelPaymentMembership.getMonths()));
            paramBuilder.addParam("firstMembership", modelPaymentMembership.getFirstMembership());
            paramBuilder.addParam("idGym", Application.GetCurrentGym().getIdGym());
            paramBuilder.addParam("idMembership", modelMembership.getIdMembership());
            isOk = paramBuilder.executeUpdate();
        }
        return isOk;
    }

    public static boolean DeletePaymentMembership(Model_Payment_Membership modelPaymentMembership) {
        // disable previous debt if exists
        boolean isOk = JDBC_Debt.DeleteDebt(modelPaymentMembership.getIdMember());
        if (isOk) {
            ParamBuilder paramBuilder = new ParamBuilder("PAYMENTS_MEMBERSHIPS", "idPaymentMembership", modelPaymentMembership.getIdPaymentMembership());
            paramBuilder.addParam("flag", 0);
            isOk = paramBuilder.executeUpdate();
        }
        return isOk;
    }
}
