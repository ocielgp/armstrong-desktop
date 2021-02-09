package com.ocielgp.database;

import com.ocielgp.model.AdministradorModel;
import com.ocielgp.utilities.Hash;
import com.ocielgp.utilities.NotificationHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorData {

    public static AdministradorModel login(String user, String password) {
        String hash = Hash.generateHash(password);
        System.out.println(hash);
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT idRecepcionista, nombres, apellidos, idRol FROM Staff_Recepcionistas s JOIN Usuarios u on s.idUsuario = u.idUsuario WHERE ( BINARY usuario = ? AND BINARY password = ? ) AND u.flag = TRUE LIMIT 1");
            ps.setString(1, user);
            ps.setString(2, hash);
            rs = ps.executeQuery();
            if (rs.next()) {
                AdministradorModel model = new AdministradorModel();
                model.setIdRecepcionista(rs.getInt("idRecepcionista"));
                model.setNombres(rs.getString("nombres"));
                model.setApellidos(rs.getString("apellidos"));
                model.setIdRol(rs.getInt("idRol"));

                NotificationHandler.notify("gmi-check", "Hola", "Bienvenido " + model.getNombres(), 2);
                return model;
            }
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "Error al iniciar sesión en el servidor", 5);
            throwables.printStackTrace();
            return null;
        }
        NotificationHandler.danger("Error", "Usuario / Contraseña incorrectos", 2);
        return null;
    }
}
