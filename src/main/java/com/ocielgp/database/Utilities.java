package com.ocielgp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilities {
    public static int countRows(PreparedStatement ps) {
        Connection con = DataServer.getConnection();
        try {
            ResultSet rs = ps.executeQuery();
            if (rs.last()) {
                return rs.getRow();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }
}
