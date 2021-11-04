package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class JDBC_Membership {
    public static int CreateMembership(Model_Membership modelMembership) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO MEMBERSHIPS(price, name, monthly, idAdmin)" +
                            "VALUE (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setBigDecimal(1, modelMembership.getPrice()); // price
            ps.setString(2, modelMembership.getName()); // name
            ps.setBoolean(3, modelMembership.isMonthly()); // monthly
            ps.setInt(4, Application.getModelAdmin().getIdMember()); // idAdmin
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) { // return new Membership
                return rs.getInt(1);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
        return 0;
    }

    /**
     * @param monthly 0 = visit
     *                1 = monthly
     *                2 = all
     */
    public static CompletableFuture<ObservableList<Model_Membership>> ReadMemberships(byte monthly) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            ObservableList<Model_Membership> modelMembershipsList = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                String sql = "SELECT idMembership, price, name, monthly, dateTime, idAdmin FROM MEMBERSHIPS WHERE flag = 1";
                if (monthly == 0 || monthly == 1) {
                    sql += " AND monthly = ?";
                    ps = con.prepareStatement(sql + " ORDER BY monthly, price DESC");
                    ps.setByte(1, monthly);

                } else ps = con.prepareStatement(sql + " ORDER BY monthly DESC, price");
                rs = ps.executeQuery();

                while (rs.next()) {
                    Model_Membership modelMembership = new Model_Membership();
                    modelMembership.setIdMembership(rs.getInt("idMembership"));
                    modelMembership.setPrice(rs.getBigDecimal("price"));
                    modelMembership.setName(rs.getString("name"));
                    modelMembership.setMonthly(rs.getBoolean("monthly"));
                    modelMembership.setDateTime(DateTime.MySQLToJava(rs.getString("dateTime")));
                    modelMembership.setIdAdmin(rs.getInt("idAdmin"));
                    modelMembershipsList.add(modelMembership);
                }
                if (modelMembershipsList.isEmpty()) {
                    Notifications.Danger("Membresías", "No hay membresías registradas");
                }
                con.close();
            } catch (SQLException sqlException) {
                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelMembershipsList;
        });
    }
}
