package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Member;
import com.ocielgp.models.Model_Staff_Member;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBC_Staff_Member {
    public static Boolean ReadLogin(String username, String password) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            String hash = Hash.generateHash(password);
            System.out.println("hash " + hash);

            assert con != null;
            ps = con.prepareStatement("SELECT SM.idRole, M.idMember, M.name, M.lastName, M.access FROM STAFF_MEMBERS SM JOIN MEMBERS M ON SM.idMember = M.idMember WHERE (SM.flag = 1 AND M.flag = 1) AND (BINARY username = ? AND BINARY password = ?)");
            ps.setString(1, username);
            ps.setString(2, hash);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("access") == 0) {
                    return null;
                } else {
                    Model_Staff_Member modelStaffMember = new Model_Staff_Member();
                    modelStaffMember.setPassword(hash);
                    modelStaffMember.setIdRole(rs.getInt("idRole"));

                    Model_Member modelMember = new Model_Member();
                    modelMember.setIdMember(rs.getInt("idMember"));
                    modelMember.setName(rs.getString("name"));
                    modelMember.setLastName(rs.getString("lastName"));
                    modelMember.setModelStaffMember(modelStaffMember);

                    Application.setStaffUserModel(modelMember);
                    return true;
                }
            }
        } catch (SQLException sqlException) {
            Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
        return false;
    }
}
