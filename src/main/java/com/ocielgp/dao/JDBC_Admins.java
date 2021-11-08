package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBC_Admins {
    public static Boolean ReadLogin(String username, String password) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            String hash = Hash.generateHash(password);
            System.out.println("hash " + hash);

            assert con != null;
            ps = con.prepareStatement("SELECT A.idRole, M.idMember, M.name, M.lastName, M.access FROM ADMINS A JOIN MEMBERS M ON A.idMember = M.idMember WHERE (A.flag = 1 AND M.flag = 1) AND (BINARY username = ? AND BINARY password = ?)");
            ps.setString(1, username);
            ps.setString(2, hash);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("access") == 0) {
                    return null;
                } else {
                    Model_Admin modelAdmin = new Model_Admin();
                    modelAdmin.setPassword(hash);
                    modelAdmin.setIdRole(rs.getShort("idRole"));
                    modelAdmin.setIdMember(rs.getInt("idMember"));
                    modelAdmin.setName(rs.getString("name"));
                    modelAdmin.setLastName(rs.getString("lastName"));

                    Application.setModelAdmin(modelAdmin);
                    return true;
                }
            }
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
        return false;
    }
}
