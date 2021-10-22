package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Debt;
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
    public static void CreateDebt(Model_Debt modelDebt, int idMember, int debtType) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("INSERT INTO DEBTS(dateTime, owe, paidOut, amount, description, idStaff, idMember, idDebtType) VALUE (NOW(), ?, ?, ?, ?, ?, ?, ?)");
                ps.setBigDecimal(1, modelDebt.getOwe()); // owe
                ps.setBigDecimal(2, modelDebt.getPaidOut()); // paidOut
                ps.setInt(3, modelDebt.getAmount()); // amount
                ps.setString(4, modelDebt.getDescription()); // description
                ps.setInt(5, Application.getModelAdmin().getIdMember()); // idStaff
                ps.setInt(6, idMember); // idMember
                ps.setInt(7, debtType); // idDebtType
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
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
                    if (debtList.size() == 0) {
                        JDBC_Debt.ReadTotalOwe(idMember).thenAccept(modelDebt::setTotalOwe);
                    }
                    modelDebt.setDateTime(rs.getString("dateTime"));
                    modelDebt.setOwe(rs.getBigDecimal("owe"));
                    modelDebt.setPaidOut(rs.getBigDecimal("paidOut"));
                    modelDebt.setAmount(rs.getInt("amount"));
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
