package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Admin_Role;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class JDBC_Admins_Role {
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

    public static CompletableFuture<ObservableList<Model_Admin_Role>> ReadRoles() {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            ObservableList<Model_Admin_Role> modelAdminsRoles = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idRole, name, createdAt, createdBy, updatedAt, updatedBy FROM ADMINS_ROLES WHERE flag = 1 AND idRole > ?");
                ps.setShort(1, Application.GetModelAdmin().getIdRole());
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Admin_Role modelAdminsRole = new Model_Admin_Role();
                    modelAdminsRole.setIdRole(rs.getShort("idRole"));
                    modelAdminsRole.setName(rs.getString("name"));
                    modelAdminsRole.setCreatedAt(DateTime.MySQLToJava(rs.getString("createdAt")));
                    modelAdminsRole.setCreatedBy(rs.getInt("createdBy"));
                    modelAdminsRole.setUpdatedAt(DateTime.MySQLToJava(rs.getString("updatedAt")));
                    modelAdminsRole.setUpdatedBy(rs.getInt("updatedBy"));
                    modelAdminsRoles.add(modelAdminsRole);
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return modelAdminsRoles;
        });
    }
}
