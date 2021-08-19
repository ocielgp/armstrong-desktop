package com.ocielgp.database.payments;

import com.ocielgp.app.GlobalController;
import com.ocielgp.database.DataServer;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DATA_DEBTS {
    public static boolean CreateDebt(MODEL_DEBTS modelDebts, int idMember, int debtType) {
        Connection con = DataServer.getConnection();
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("INSERT INTO DEBTS(dateTime, owe, paidOut, amount, description, idStaff, idMember, idDebtType) VALUE (NOW(), ?, ?, ?, ?, ?, ?, ?)");
            ps.setDouble(1, modelDebts.getOwe()); // owe
            ps.setDouble(2, modelDebts.getPaidOut()); // paidOut
            ps.setInt(3, modelDebts.getAmount()); // amount
            ps.setString(4, modelDebts.getDescription()); // description
            ps.setInt(5, GlobalController.getStaffUserModel().getIdMember()); // idStaff
            ps.setInt(6, idMember); // idMember
            ps.setInt(7, debtType); // idDebtType
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static boolean ReadHaveDebts(int idMember) {
        Connection con = DataServer.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT COUNT(idDebt) > 0 AS 'haveDebts' FROM DEBTS WHERE idMember = ? AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("haveDebts");
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static ArrayList<MODEL_DEBTS> ReadDebts(int idMember) {
        Connection con = DataServer.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        ArrayList<MODEL_DEBTS> debtsList = new ArrayList<>();
        try {
            ps = con.prepareStatement("SELECT dateTime, owe, paidOut, amount, description FROM DEBTS WHERE idMember = ? AND debtStatus = 1");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            while (rs.next()) {
                MODEL_DEBTS modelDebts = new MODEL_DEBTS();
                if (debtsList.size() == 0) {
                    modelDebts.setTotalOwe(ReadTotalOwe(idMember));
                }
                modelDebts.setDateTime(rs.getString("dateTime"));
                modelDebts.setOwe(rs.getDouble("owe"));
                modelDebts.setPaidOut(rs.getDouble("paidOut"));
                modelDebts.setAmount(rs.getInt("amount"));
                modelDebts.setDescription(rs.getString("description"));
                debtsList.add(modelDebts);
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return debtsList;
    }

    public static double ReadTotalOwe(int idMember) {
        Connection con = DataServer.getConnection();
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT SUM(owe) 'totalOwe' FROM DEBTS WHERE idMember = ? AND debtStatus = 1");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalOwe");
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return 0;
    }
}
