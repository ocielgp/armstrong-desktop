package com.ocielgp.dao;

import com.ocielgp.models.Model_Member_Photo;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBC_Member_Photo {
    public static void CreatePhoto(int idMember, byte[] photoBytes) {
        if (photoBytes == null) return;
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO MEMBERS_PHOTOS(photo, idMember) VALUE (?, ?)");
            ps.setBytes(1, photoBytes); // photo
            ps.setInt(2, idMember); // idMember
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
    }

    public static Model_Member_Photo ReadPhoto(int idMember) {
        Connection con = DataServer.GetConnection();
        Model_Member_Photo modelMemberPhoto = new Model_Member_Photo();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT idPhoto, photo FROM MEMBERS_PHOTOS WHERE idMember = ? ORDER BY idMember DESC");
            ps.setInt(1, idMember); // photo
            rs = ps.executeQuery();
            if (rs.next()) {
                modelMemberPhoto.setPhoto(rs.getBytes("photo"));
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return modelMemberPhoto;
    }

    public static boolean UpdatePhoto(int idMember, byte[] photoBytes) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT idPhoto FROM MEMBERS_PHOTOS WHERE idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            if (rs.next()) {
                ps = con.prepareStatement("UPDATE MEMBERS_PHOTOS SET photo = ? WHERE idMember = ?");
                ps.setBytes(1, photoBytes);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } else {
                JDBC_Member_Photo.CreatePhoto(idMember, photoBytes);
            }
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            return false;
        } finally {
            DataServer.CloseConnection(con);
        }
    }
}
