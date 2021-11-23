package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.ParamBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class JDBC_Membership {
    public static int CreateMembership(Model_Membership modelMembership) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO MEMBERSHIPS(name, price, monthly, createdBy) VALUE (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, modelMembership.getName()); // name
            ps.setBigDecimal(2, modelMembership.getPrice()); // price
            ps.setBoolean(3, modelMembership.getMonthly()); // monthly
            ps.setInt(4, Application.GetModelAdmin().getIdMember()); // idAdmin
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) { // return new id membership
                return rs.getInt(1);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
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
            Connection con = DataServer.GetConnection();
            ObservableList<Model_Membership> modelMembershipsList = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                String sql = "SELECT idMembership, name, price, monthly, createdAt, createdBy, updatedAt, updatedBy FROM MEMBERSHIPS WHERE flag = 1";
                if (monthly == 0 || monthly == 1) {
                    sql += " AND monthly = ?";
                    ps = con.prepareStatement(sql + " ORDER BY monthly, price");
                    ps.setByte(1, monthly);

                } else ps = con.prepareStatement(sql + " ORDER BY monthly DESC, price");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Membership modelMembership = new Model_Membership();
                    modelMembership.setIdMembership(rs.getInt("idMembership"));
                    modelMembership.setName(rs.getString("name"));
                    modelMembership.setPrice(rs.getBigDecimal("price"));
                    modelMembership.setMonthly(rs.getBoolean("monthly"));
                    modelMembership.setCreatedAt(DateTime.MySQLToJava(rs.getString("createdAt")));
                    modelMembership.setCreatedBy(rs.getInt("createdBy"));
                    modelMembership.setUpdatedAt(DateTime.MySQLToJava(rs.getString("updatedAt")));
                    modelMembership.setUpdatedBy(rs.getInt("updatedBy"));
                    modelMembershipsList.add(modelMembership);
                }
                if (modelMembershipsList.isEmpty()) {
                    Notifications.Danger("Membresías", "No hay membresías registradas");
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return modelMembershipsList;
        });
    }

    public static boolean UpdateMembership(Model_Membership modelMembership) {
        ParamBuilder paramBuilder = new ParamBuilder("MEMBERSHIPS", "idMembership", modelMembership.getIdMembership());
        paramBuilder.addParam("name", modelMembership.getName());
        paramBuilder.addParam("price", modelMembership.getPrice());
        return paramBuilder.executeUpdate();
    }

    public static boolean DeleteMembership(int idMembership) {
        ParamBuilder paramBuilder = new ParamBuilder("MEMBERSHIPS", "idMembership", idMembership);
        paramBuilder.addParam("updatedBy", Application.GetModelAdmin().getIdMember());
        paramBuilder.addParam("flag", 0);
        return paramBuilder.executeUpdate();
    }
}
