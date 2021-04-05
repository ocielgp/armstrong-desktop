package com.ocielgp.database;

import com.ocielgp.model.StaffUsersModel;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.NotificationHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffUsersData {

    public static StaffUsersModel login(String user, String password) {
        String hash = Hash.generateHash(password);
        System.out.println(hash);
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT SU.idStaffUser, SU.locked, M.name, M.lastName, SE.idRole FROM MEMBERS M JOIN STAFF_EMPLOYEES SE on M.idMember = SE.idMember JOIN STAFF_USERS SU on SE.idEmployee = SU.idEmployee WHERE ( M.flag = 1 AND SE.flag = 1 ) AND ( BINARY SU.user = ? AND BINARY SU.password = ? )");
            ps.setString(1, user);
            ps.setString(2, hash);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("locked") == 1) {
                    NotificationHandler.warn("Bloqueado", "Esta cuenta se encuentra suspendida.", 3);
                    return null;
                } else {
                    StaffUsersModel model = new StaffUsersModel();
                    model.setIdStaffUser(rs.getInt("idStaffUser"));
                    model.setName(rs.getString("name"));
                    model.setLastName(rs.getString("lastName"));
                    model.setIdRole(rs.getInt("idRole"));

                    return model;
                }
            }
        } catch (SQLException sqlException) {
            System.out.println("hola");
//            NotificationHandler.catchError(
//                    MethodHandles.lookup().lookupClass().getSimpleName(),
//                    Thread.currentThread().getStackTrace()[1],
//                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
//                    sqlException
//            );
//            NotificationHandler.danger("Error", "[RecepcionistData]: Error al iniciar sesión en el servidor", 5);
//            sqlException.printStackTrace();
        }
        NotificationHandler.danger("Error", "Usuario / Contraseña incorrectos", 2);
        return null;
    }
}
