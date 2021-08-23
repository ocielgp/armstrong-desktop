package com.ocielgp.database.staff;

import com.ocielgp.app.GlobalController;
import com.ocielgp.database.members.MODEL_MEMBERS;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DATA_STAFF_MEMBERS {
    public static Boolean Login(String username, String password) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            String hash = Hash.generateHash(password);
            System.out.println("hash " + hash);

            ps = con.prepareStatement("SELECT SM.idRole, M.idMember, M.name, M.lastName, M.access FROM STAFF_MEMBERS SM JOIN MEMBERS M ON SM.idMember = M.idMember WHERE (SM.flag = 1 AND M.flag = 1) AND (BINARY username = ? AND BINARY password = ?)");
            ps.setString(1, username);
            ps.setString(2, hash);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("access") == 0) {
                    return null;
                } else {
                    MODEL_STAFF_MEMBERS modelStaffMembers = new MODEL_STAFF_MEMBERS();
                    modelStaffMembers.setPassword(hash);
                    modelStaffMembers.setIdRole(rs.getInt("idRole"));

                    MODEL_MEMBERS modelMembers = new MODEL_MEMBERS();
                    modelMembers.setIdMember(rs.getInt("idMember"));
                    modelMembers.setName(rs.getString("name"));
                    modelMembers.setLastName(rs.getString("lastName"));
                    modelMembers.setModelStaffMembers(modelStaffMembers);

                    GlobalController.setStaffUserModel(modelMembers);
                    return true;
                }
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }
}
