package com.ocielgp.dao;

import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class JDBC_Membership {

    /**
     * @param type 0 = visit
     *             1 = monthly
     *             2 = all
     */
    public static CompletableFuture<ObservableList<Model_Membership>> ReadMemberships(byte type) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            ObservableList<Model_Membership> modelMembershipsList = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                String sql = "SELECT idMembership, price, name, type, dateTime, idAdmin FROM MEMBERSHIPS WHERE flag = 1";
                if (type == 0 || type == 1) {
                    sql += " AND type = ?";
                    ps = con.prepareStatement(sql + " ORDER BY type, price DESC");
                    ps.setByte(1, type);

                } else ps = con.prepareStatement(sql + " ORDER BY type DESC, price");
                rs = ps.executeQuery();

                while (rs.next()) {
                    Model_Membership modelMembership = new Model_Membership();
                    modelMembership.setIdMembership(rs.getInt("idMembership"));
                    modelMembership.setPrice(rs.getBigDecimal("price"));
                    modelMembership.setName(rs.getString("name"));
                    modelMembership.setType(rs.getShort("type"));
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
