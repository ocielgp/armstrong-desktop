package com.ocielgp.database.system;

import com.ocielgp.app.GlobalController;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DATA_CHECK_IN {
    public static boolean CreateCheckIn(int idMember, int openedBy) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("INSERT INTO CHECK_IN(dateTime, idMember, idGym, openedBy) VALUE (NOW(), ?, ?, ?)");
            ps.setInt(1, idMember); // idMember
            ps.setInt(2, GlobalController.getCurrentGym().getIdGym()); // idGym
            ps.setInt(3, openedBy); // openedBy
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }
}
