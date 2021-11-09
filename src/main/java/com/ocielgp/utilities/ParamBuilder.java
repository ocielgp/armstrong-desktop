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
        this.where = new Pair<>(whereColumn, whereValue);
        addParam("updatedBy", Application.getModelAdmin().getIdMember());
    }

    public void addParam(String tableColumn, Object param) {
        if (param != null) {
            this.queryParams.put(this.queryParams.size() + 1, param);
            if (this.queryParams.size() > 1) {
                this.sqlBuilder.append(", ").append(tableColumn).append(" = ?");
            } else {
                this.sqlBuilder.append(tableColumn).append(" = ?");
            }
        }
    }

    public boolean executeUpdate() {
        if (this.queryParams.size() == 1) return true;
        if (this.queryParams.size() > 1) { // skip updated by
            Connection con = DataServer.getConnection();
            try {
                this.sqlBuilder.append(" WHERE ").append(this.where.getKey()).append(" = ?");
                assert con != null;
                System.out.println("sql: " + this.sqlBuilder);
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
                return true;
            } finally {
                DataServer.closeConnection(con);
            }
        }
        return false;
    }
}
