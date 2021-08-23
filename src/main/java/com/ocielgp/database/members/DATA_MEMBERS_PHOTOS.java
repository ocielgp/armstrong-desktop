package com.ocielgp.database.members;

import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DATA_MEMBERS_PHOTOS {
    public static boolean CreatePhoto(int idMember, byte[] photoBytes) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("INSERT INTO MEMBERS_PHOTOS(photo, idMember) VALUE (?, ?)");
            ps.setBytes(1, photoBytes); // photo
            ps.setInt(2, idMember); // idMember
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static MODEL_MEMBERS_PHOTOS ReadPhoto(int idMember) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT idPhoto, photo FROM MEMBERS_PHOTOS WHERE idMember = ? ORDER BY idMember DESC");
            ps.setInt(1, idMember); // photo
            rs = ps.executeQuery();
            if (rs.next()) {
                MODEL_MEMBERS_PHOTOS modelMembersPhotos = new MODEL_MEMBERS_PHOTOS();
                modelMembersPhotos.setPhoto(rs.getBytes("photo"));
                return modelMembersPhotos;
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return null;
    }

    public static boolean UpdatePhoto(int idMember, byte[] photoBytes) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS_PHOTOS SET photo = ? WHERE idMember = ?");
            ps.setBytes(1, photoBytes);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }
}
