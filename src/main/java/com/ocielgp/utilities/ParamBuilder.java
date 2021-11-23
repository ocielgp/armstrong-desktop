package com.ocielgp.utilities;

import com.ocielgp.app.Application;
import com.ocielgp.dao.DataServer;
import javafx.util.Pair;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class ParamBuilder {
    // attributes
    private final StringBuilder sqlBuilder;
    private final Pair<String, Object> where;
    private final HashMap<Integer, Object> queryParams = new HashMap<>();

    public ParamBuilder(String table, String whereColumn, Object whereValue) {
        this.sqlBuilder = new StringBuilder("UPDATE ").append(table).append(" SET ");
        addParam("updatedBy", Application.GetModelAdmin().getIdMember());
        this.where = new Pair<>(whereColumn, whereValue);
    }

    public void addParam(String column, Object value) {
        if (value != null) {
            this.queryParams.put(this.queryParams.size() + 1, value);
            if (this.queryParams.size() > 1) {
                this.sqlBuilder.append(", ").append(column).append(" = ?");
            } else {
                this.sqlBuilder.append(column).append(" = ?");
            }
        }
    }

    public boolean executeUpdate() {
        if (this.queryParams.size() == 1) return true;
        if (this.queryParams.size() > 1) { // skip updated by
            Connection con = DataServer.GetConnection();
            try {
                this.sqlBuilder.append(" WHERE ").append(this.where.getKey()).append(" = ?");
                assert con != null;
                System.out.println("paramBuilder: " + this.sqlBuilder);
                PreparedStatement preparedStatement = con.prepareStatement(this.sqlBuilder.toString());
                this.queryParams.forEach((position, param) -> {
                    try {
                        preparedStatement.setObject(position, param);
                    } catch (SQLException sqlException) {
                        Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
                    }
                });
                preparedStatement.setObject(this.queryParams.size() + 1, this.where.getValue());
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
                return false;
            } finally {
                DataServer.CloseConnection(con);
            }
        }
        return true;
    }
}
