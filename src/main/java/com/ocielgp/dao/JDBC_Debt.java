package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Debt;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.ParamBuilder;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBC_Debt {
    public static boolean CreateDebt(int idMember, Model_Debt modelDebt) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO DEBTS(owe, paidOut, idMember, createdBy) VALUE (?, ?, ?, ?)");
            ps.setBigDecimal(1, modelDebt.getOwe()); // owe
            ps.setBigDecimal(2, modelDebt.getPaidOut()); // paidOut
            ps.setInt(3, idMember); // idMember
            ps.setInt(4, Application.GetModelAdmin().getIdMember()); // idAdmin
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            return false;
        } finally {
            DataServer.CloseConnection(con);
        }
    }

    public static Model_Debt ReadDebt(int idMember) {
        Connection con = DataServer.GetConnection();
        Model_Debt modelDebt = new Model_Debt();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT idDebt, updatedAt, owe, paidOut FROM DEBTS WHERE idMember = ? AND flag = 1 AND debtStatus = 1 ORDER BY updatedAt DESC LIMIT 1");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            if (rs.next()) {
                modelDebt.setIdDebt(rs.getInt("idDebt"));
                modelDebt.setUpdatedAt(DateTime.MySQLToJava(rs.getString("updatedAt")));
                modelDebt.setOwe(rs.getBigDecimal("owe"));
                modelDebt.setPaidOut(rs.getBigDecimal("paidOut"));
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return modelDebt;
    }

    public static boolean UpdateDebt(Model_Debt modelDebt) {
        ParamBuilder paramBuilder = new ParamBuilder("DEBTS", "idDebt", modelDebt.getIdDebt());
        paramBuilder.addParam("owe", modelDebt.getOwe());
        paramBuilder.addParam("paidOut", modelDebt.getPaidOut());
        return paramBuilder.executeUpdate();
    }

    public static boolean DeleteDebt(int idMember) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            // disable previous debt if exists
            ps = con.prepareStatement("UPDATE DEBTS SET flag = 0, updatedBy = ? WHERE idMember = ? AND debtStatus = 1 ORDER BY updatedAt DESC");
            ps.setInt(1, Application.GetModelAdmin().getIdMember());
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return false;
    }
}
