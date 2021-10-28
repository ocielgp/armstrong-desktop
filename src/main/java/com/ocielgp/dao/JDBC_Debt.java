package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Debt;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JDBC_Debt {
    public static boolean CreateDebt(int idMember, Model_Debt modelDebt) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO DEBTS(dateTime, owe, paidOut, amount, description, idAdmin, idMember, isMembership) VALUE (NOW(), ?, ?, ?, ?, ?, ?, ?)");
            ps.setBigDecimal(1, modelDebt.getOwe()); // owe
            ps.setBigDecimal(2, modelDebt.getPaidOut()); // paidOut
            ps.setInt(3, modelDebt.getAmount()); // amount
            ps.setString(4, modelDebt.getDescription()); // description
            ps.setInt(5, Application.getModelAdmin().getIdMember()); // idAdmin
            ps.setInt(6, idMember); // idMember
            ps.setBoolean(7, modelDebt.isMembership()); // idMember
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            return false;
        } finally {
            DataServer.closeConnection(con);
        }
    }

    public static CompletableFuture<List<Model_Debt>> ReadDebts(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            List<Model_Debt> debtList = new ArrayList<>();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT dateTime, owe, paidOut, amount, description FROM DEBTS WHERE idMember = ? AND debtStatus = 1");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Debt modelDebt = new Model_Debt();
                    modelDebt.setDateTime(DateTime.MySQLToJava(rs.getString("dateTime")));
                    modelDebt.setOwe(rs.getBigDecimal("owe"));
                    modelDebt.setPaidOut(rs.getBigDecimal("paidOut"));
                    modelDebt.setAmount(rs.getShort("amount"));
                    modelDebt.setDescription(rs.getString("description"));
                    debtList.add(modelDebt);
                }
                con.close();
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return debtList;
        });
    }

    public static CompletableFuture<BigDecimal> ReadTotalOwe(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT SUM(owe) 'totalOwe' FROM DEBTS WHERE idMember = ? AND debtStatus = 1");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getBigDecimal("totalOwe");
                }
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return new BigDecimal("0.0");
        });
    }
}
