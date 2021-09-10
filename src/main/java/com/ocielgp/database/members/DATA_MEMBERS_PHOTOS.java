package com.ocielgp.database.members;

import com.ocielgp.database.DataServer;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DATA_MEMBERS_PHOTOS {
    public static void CreatePhoto(int idMember, byte[] photoBytes) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            try {
                ps = con.prepareStatement("INSERT INTO MEMBERS_PHOTOS(photo, idMember) VALUE (?, ?)");
                ps.setBytes(1, photoBytes); // photo
                ps.setInt(2, idMember); // idMember
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
        });
    }

    public static CompletableFuture<MODEL_MEMBERS_PHOTOS> ReadPhoto(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            ResultSet rs;
            MODEL_MEMBERS_PHOTOS modelMembersPhotos = null;
            try {
                ps = con.prepareStatement("SELECT idPhoto, photo FROM MEMBERS_PHOTOS WHERE idMember = ? ORDER BY idMember DESC");
                ps.setInt(1, idMember); // photo
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelMembersPhotos = new MODEL_MEMBERS_PHOTOS();
                    modelMembersPhotos.setPhoto(rs.getBytes("photo"));
                    return modelMembersPhotos;
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
            return modelMembersPhotos;
        });
    }

    public static void UpdatePhoto(int idMember, byte[] photoBytes) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            try {
                ps = con.prepareStatement("UPDATE MEMBERS_PHOTOS SET photo = ? WHERE idMember = ?");
                ps.setBytes(1, photoBytes);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
        });
    }
}
