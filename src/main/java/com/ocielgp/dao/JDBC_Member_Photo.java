package com.ocielgp.dao;

import com.ocielgp.models.Model_Member_Photo;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class JDBC_Member_Photo {
    public static void CreatePhoto(int idMember, byte[] photoBytes) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO MEMBERS_PHOTOS(photo, idMember) VALUE (?, ?)");
            ps.setBytes(1, photoBytes); // photo
            ps.setInt(2, idMember); // idMember
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
    }

    public static CompletableFuture<Model_Member_Photo> ReadPhoto(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            Model_Member_Photo modelMemberPhoto = null;
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idPhoto, photo FROM MEMBERS_PHOTOS WHERE idMember = ? ORDER BY idMember DESC");
                ps.setInt(1, idMember); // photo
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelMemberPhoto = new Model_Member_Photo();
                    modelMemberPhoto.setPhoto(rs.getBytes("photo"));
                    return modelMemberPhoto;
                }
            } catch (SQLException sqlException) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelMemberPhoto;
        });
    }

    public static boolean UpdatePhoto(int idMember, byte[] photoBytes) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("UPDATE MEMBERS_PHOTOS SET photo = ? WHERE idMember = ?");
            ps.setBytes(1, photoBytes);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            return false;
        } finally {
            DataServer.closeConnection(con);
        }
    }
}
