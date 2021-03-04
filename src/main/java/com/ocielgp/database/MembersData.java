package com.ocielgp.database;

import com.digitalpersona.uareu.Fmd;
import com.ocielgp.model.MembersModel;
import com.ocielgp.utilities.NotificationHandler;

import java.sql.*;
import java.util.ListIterator;

public class MembersData {
    public static int addMember(MembersModel memberModel) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("INSERT INTO MEMBERS(name, lastName, gender, phone, email, notes, registrationDate) VALUE (?, ?, ?, ?, ?, ?, CURDATE());", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, memberModel.getName());
            ps.setString(2, memberModel.getLastName());
            ps.setString(3, memberModel.getGender());
            if (memberModel.getPhone().equals("")) ps.setNull(4, Types.NULL);
            else ps.setString(4, memberModel.getPhone());
            if (memberModel.getEmail().equals("")) ps.setNull(5, Types.NULL);
            else ps.setString(5, memberModel.getEmail());
            if (memberModel.getNotes().equals("")) ps.setNull(6, Types.NULL);
            else ps.setString(6, memberModel.getNotes());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                rs.next();
                return rs.getInt(1); // Return new id member
            } else {
                throw new SQLException("[MembersData]: Filas afectadas = 0");
            }
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData]: Error al crear un nuevo miembro.", 5);
            throwables.printStackTrace();
        }
        return 0;
    }

    public static boolean uploadPhoto(int idMember, byte[] photoBytes) {
        if (photoBytes == null) {
            return true; // Skip photo
        }
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            // Check if member already have a photo
            ps = con.prepareStatement("SELECT idPhoto FROM MEMBER_PHOTOS WHERE idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                int idPhoto = rs.getInt("idPhoto");
                ps = con.prepareStatement("UPDATE MEMBER_PHOTOS SET photo = ? WHERE idPhoto = ?");
                ps.setBytes(1, photoBytes);
                ps.setInt(2, idPhoto);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    return true;
                } else {
                    throw new SQLException("[MembersData]: Filas afectadas = 0");
                }
            }
            ps = con.prepareStatement("INSERT INTO MEMBER_PHOTOS(photo, idMember) VALUE (?, ?)");
            ps.setBytes(1, photoBytes);
            ps.setInt(2, idMember);
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("subida");
                return true;
            } else {
                throw new SQLException("[MembersData]: Filas afectadas = 0");
            }
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData]: Error al subir una foto.", 5);
            throwables.printStackTrace();
        }
        return false;
    }

    public static boolean uploadFingerprints(int idMember, ListIterator<Fmd> fingerprints) {
        if (fingerprints == null) {
            return true; // Skip fingerprints
        }
        Connection con;
        PreparedStatement ps;
        try {
            con = DataServer.getConnection();
            // Remove all fingerprints if member have
            ps = con.prepareStatement("DELETE FROM MEMBER_FINGERPRINTS WHERE idMember = ?");
            ps.setInt(1, idMember);
            ps.executeUpdate();

            while (fingerprints.hasNext()) {
                Fmd fingerprint = fingerprints.next();
                ps = con.prepareStatement("INSERT INTO MEMBER_FINGERPRINTS(fingerprint, idMember) VALUE (?, ?)");
                ps.setBytes(1, fingerprint.getData());
                ps.setInt(2, idMember);
                ps.executeUpdate();
            }
            return true;
        } catch (Exception throwables) {
            NotificationHandler.danger("Error", "[MembersData]: Error al subir huellas.", 5);
            throwables.printStackTrace();
        }
        return false;
    }
}
