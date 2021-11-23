package com.ocielgp.dao;

import com.ocielgp.models.Model_Admin;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class JDBC_Summary {
    public static CompletableFuture<ObservableList<Model_Admin>> ReadHandler(boolean isMoney, String from, String to, String sql) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            ObservableList<Model_Admin> membersObservableList = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement(sql);
                ps.setString(1, from);
                ps.setString(2, to);
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Admin modelAdmin = new Model_Admin();
                    modelAdmin.setName(rs.getString("name"));
                    modelAdmin.setMetadata((isMoney) ? "$ " + rs.getString("metadata") : rs.getString("metadata"));
                    membersObservableList.add(modelAdmin);
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return membersObservableList;
        });
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadCheckIn(String from, String to) {
        return JDBC_Summary.ReadHandler(
                false,
                from,
                to,
                "SELECT 'name', COUNT(*) AS 'metadata' FROM CHECK_IN WHERE createdAt BETWEEN ? AND ?"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadTotalMembers(String from, String to) {
        return JDBC_Summary.ReadHandler(
                false,
                from,
                to,
                "SELECT M.name, COUNT(*) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy JOIN MEMBERSHIPS MS ON PM.idPaymentMembership = MS.idMembership WHERE M.flag = 1 AND PM.flag = 1 AND startDateTime BETWEEN ? AND ? GROUP BY A.idAdmin ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadTotalPaymentsMemberships(String from, String to) {
        return JDBC_Summary.ReadHandler(
                true,
                from,
                to,
                "SELECT M.name, SUM(PM.price) - CASE WHEN SUM(D.owe) IS NULL THEN 0 ELSE SUM(D.owe) END AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy LEFT JOIN DEBTS D ON D.idMember = PM.idMember AND D.flag = 1 AND D.debtStatus = 1 JOIN MEMBERSHIPS MS ON PM.idPaymentMembership = MS.idMembership WHERE M.flag = 1 AND PM.flag = 1 AND startDateTime BETWEEN ? AND ? GROUP BY A.idAdmin ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadPaymentsVisits(String from, String to) {
        return JDBC_Summary.ReadHandler(
                true,
                from,
                to,
                "SELECT M.name, SUM(MS.price) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember AND M.flag = 1 JOIN PAYMENTS_VISITS PV ON A.idAdmin = PV.createdBy AND PV.flag = 1 JOIN MEMBERSHIPS MS ON PV.idMembership = MS.idMembership WHERE A.createdAt BETWEEN ? AND ? GROUP BY A.idAdmin ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadTotalMembersByMembership(String from, String to) {
        return JDBC_Summary.ReadHandler(
                false,
                from,
                to,
                "SELECT MS.name, COUNT(PM.idPaymentMembership) AS 'metadata' FROM PAYMENTS_MEMBERSHIPS PM JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership WHERE PM.flag = 1 AND PM.startDateTime BETWEEN ? AND ? GROUP BY MS.idMembership ORDER BY metadata DESC"
        );
    }
}
