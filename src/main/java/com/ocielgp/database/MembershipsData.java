package com.ocielgp.database;

import com.ocielgp.database.models.MembershipsModel;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MembershipsData {

    public static ObservableList<MembershipsModel> getMemberships() {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT idMembership, price, description, days FROM MEMBERSHIPS WHERE flag = 1");
            rs = ps.executeQuery();
            ObservableList<MembershipsModel> subscriptions = FXCollections.observableArrayList();
            while (rs.next()) {
                MembershipsModel subscription = new MembershipsModel();
                subscription.setIdMembership(rs.getInt("idMembership"));
                subscription.setPrice(rs.getDouble("price"));
                subscription.setDescription(rs.getString("description"));
                subscription.setDays(rs.getInt("days"));

                subscriptions.add(subscription);
            }
            if (subscriptions.isEmpty()) {
                Notifications.warn("Membresías", "No hay membresías registradas.", 2);
            } else {
                return subscriptions;
            }
            return subscriptions;
        } catch (SQLException throwables) {
            Notifications.danger("Error", "[MembershipsData]: Error al obtener Memberships.", 5);
            throwables.printStackTrace();
            return null;
        }
    }
}
