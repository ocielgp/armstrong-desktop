package com.ocielgp.database;

import com.digitalpersona.uareu.Fmd;
import com.ocielgp.app.AppController;
import com.ocielgp.model.MembersModel;
import com.ocielgp.model.MembershipsModel;
import com.ocielgp.model.PaymentDebtsModel;
import com.ocielgp.utilities.NotificationHandler;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
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
                throw new SQLException("[MembersData][addMember]: Filas afectadas = 0");
            }
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData][addMember]: Error al crear un nuevo miembro.", 5);
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
                    throw new SQLException("[MembersData][uploadPhoto]: Filas afectadas = 0");
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
                throw new SQLException("[MembersData][uploadPhoto]: Filas afectadas = 0");
            }
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData][uploadPhoto]: Error al subir una foto.", 5);
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
            NotificationHandler.danger("Error", "[MembersData][uploadFingerprints]: Error al subir huellas.", 5);
            throwables.printStackTrace();
        }
        return false;
    }

    public static boolean uploadMembership(int idMember, MembershipsModel membership, String notes) {
        Connection con;
        PreparedStatement ps;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("INSERT INTO PAYMENT_MEMBERSHIPS(price, description, days, startDate, endDate, notes, idMember, idGym, idStaffUser, backup) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setDouble(1, membership.getPrice()); // price
            ps.setString(2, membership.getDescription()); // description
            ps.setInt(3, membership.getDays()); // days
            ps.setString(4, String.valueOf(LocalDate.now())); // startDate
            ps.setString(5, String.valueOf(LocalDate.now().plusDays(membership.getDays()))); // endDate
            if (notes.isEmpty()) ps.setNull(6, Types.NULL);
            else ps.setString(6, notes); // notes
            ps.setInt(7, idMember); // idMember
            ps.setInt(8, AppController.getCurrentGym().getIdGym()); // idGym
            ps.setInt(9, AppController.getStaffUserModel().getIdStaffUser()); // idStaffUser

            // TODO: FIX THIS BACKUP USER
            ps.setBoolean(10, false); // backup

            ps.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData][uploadMembership]: Error al registrar membresía.", 5);
            throwables.printStackTrace();
            return false;
        }
    }

    public static boolean createDebt(int idMember, PaymentDebtsModel debt) {
        Connection con;
        PreparedStatement ps;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("INSERT INTO PAYMENT_DEBTS(dateTime, paidOut, owe, notes, idMember, idStaffUser) VALUE (NOW(), ?, ?, ?, ?, ?)");
            ps.setDouble(1, debt.getPaidOut());
            ps.setDouble(2, debt.getOwe());
            ps.setString(3, debt.getNotes());
            ps.setInt(4, idMember);
            ps.setInt(5, AppController.getStaffUserModel().getIdStaffUser());
            ps.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData][uploadMembership]: Error al registrar membresía.", 5);
            throwables.printStackTrace();
            return false;
        }
    }

    public static ObservableList<MembersModel> getMembers(int limit, int page) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT idMember, name, lastName FROM MEMBERS WHERE flag = 1 AND idMember NOT IN ( SELECT idMember FROM STAFF_EMPLOYEES WHERE flag = 1 ) ORDER BY idMember DESC LIMIT ?,?");
            int maxRegisters = limit * page;
            ps.setInt(1, maxRegisters - limit);
            ps.setInt(2, limit);
            rs = ps.executeQuery();

            ObservableList<MembersModel> members = FXCollections.observableArrayList();
            while (rs.next()) {
                MembersModel model = new MembersModel();
                model.setIdMember(rs.getInt("idMember"));
                model.setName(rs.getString("name"));
                model.setLastName(rs.getString("lastName"));
                members.add(model);

            }
            members.addListener((ListChangeListener<MembersModel>) change -> {
                System.out.println("clickoso");
            });
            return members;
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData][getMembers]: Error al obtener socios.", 5);
            throwables.printStackTrace();
        }
        return null;
    }

}
