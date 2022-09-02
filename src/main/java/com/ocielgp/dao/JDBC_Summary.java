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
    public static CompletableFuture<ObservableList<Model_Admin>> ReadHandler(String from, String to, Integer idAdmin, String sql) {
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
                if (idAdmin > 0) {
                    ps.setInt(3, idAdmin);
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Admin modelAdmin = new Model_Admin();
                    modelAdmin.setName(rs.getString("name"));
                    modelAdmin.setMetadata(rs.getString("metadata"));
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

    public static CompletableFuture<ObservableList<Model_Admin>> ReadCheckIn(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT 'name', SUM(count) AS 'metadata' FROM (SELECT COUNT(DISTINCT idMember) AS 'count' FROM CHECK_IN WHERE idMember > 1 AND createdAt BETWEEN ? AND ? GROUP BY DATE(createdAt)) AS count"
                        : "SELECT 'name', SUM(count) AS 'metadata' FROM (SELECT COUNT(DISTINCT idMember) AS 'count' FROM CHECK_IN WHERE createdAt BETWEEN ? AND ? AND createdBy = ? GROUP BY DATE(createdAt)) AS count"

//                "SELECT 'name', COUNT(DISTINCT idMember) AS 'metadata' FROM CHECK_IN WHERE idMember > 1 AND createdAt BETWEEN ? AND ? GROUP BY DATE(createdAt)"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadMembers(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT M.name, COUNT(*) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 0 AND PM.startDateTime BETWEEN ? AND ? JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin ORDER BY metadata DESC"
                        : "SELECT M.name, COUNT(*) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 0 AND PM.startDateTime BETWEEN ? AND ? JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin HAVING A.idAdmin = ? ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadNewMembers(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT M.name, COUNT(*) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 1 AND PM.startDateTime BETWEEN ? AND ? JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin ORDER BY metadata DESC"
                        : "SELECT M.name, COUNT(*) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 1 AND PM.startDateTime BETWEEN ? AND ? JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin HAVING A.idAdmin = ? ORDER BY metadata DESC"

        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadProducts(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT M.name, COUNT(*) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_PRODUCTS PD ON A.idAdmin = PD.createdBy AND PD.flag = 1 AND PD.createdAt BETWEEN ? AND ? JOIN PRODUCTS P ON PD.idProduct = P.idProduct GROUP BY A.idAdmin ORDER BY metadata DESC"
                        : "SELECT M.name, COUNT(*) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_PRODUCTS PD ON A.idAdmin = PD.createdBy AND PD.flag = 1 AND PD.createdAt BETWEEN ? AND ? JOIN PRODUCTS P ON PD.idProduct = P.idProduct GROUP BY A.idAdmin HAVING A.idAdmin = ? ORDER BY metadata DESC"

        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadTotalPaymentsMembershipsFromNewMembers(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT M.name, SUM(PM.price) - CASE WHEN SUM(D.owe) IS NULL THEN 0 ELSE SUM(D.owe) END AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 1 AND startDateTime BETWEEN ? AND ? LEFT JOIN DEBTS D ON D.idMember = PM.idMember AND D.flag = 1 AND D.debtStatus = 1 JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin ORDER BY metadata DESC"
                        : "SELECT M.name, SUM(PM.price) - CASE WHEN SUM(D.owe) IS NULL THEN 0 ELSE SUM(D.owe) END AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 1 AND startDateTime BETWEEN ? AND ? LEFT JOIN DEBTS D ON D.idMember = PM.idMember AND D.flag = 1 AND D.debtStatus = 1 JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin HAVING A.idAdmin = ? ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadTotalPaymentsMembershipsFromMembers(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT M.name, SUM(PM.price) - CASE WHEN SUM(D.owe) IS NULL THEN 0 ELSE SUM(D.owe) END AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 0 AND startDateTime BETWEEN ? AND ? LEFT JOIN DEBTS D ON D.idMember = PM.idMember AND D.flag = 1 AND D.debtStatus = 1 JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin ORDER BY metadata DESC"
                        : "SELECT M.name, SUM(PM.price) - CASE WHEN SUM(D.owe) IS NULL THEN 0 ELSE SUM(D.owe) END AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_MEMBERSHIPS PM ON A.idAdmin = PM.createdBy AND PM.flag = 1 AND PM.firstMembership = 0 AND startDateTime BETWEEN ? AND ? LEFT JOIN DEBTS D ON D.idMember = PM.idMember AND D.flag = 1 AND D.debtStatus = 1 JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership GROUP BY A.idAdmin HAVING A.idAdmin = ? ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadPaymentsVisits(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT M.name, SUM(PV.price) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_VISITS PV ON A.idAdmin = PV.createdBy AND PV.flag = 1 JOIN MEMBERSHIPS MS ON PV.idMembership = MS.idMembership WHERE PV.createdAt BETWEEN ? AND ? GROUP BY A.idAdmin ORDER BY metadata DESC"
                        : "SELECT M.name, SUM(PV.price) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_VISITS PV ON A.idAdmin = PV.createdBy AND PV.flag = 1 JOIN MEMBERSHIPS MS ON PV.idMembership = MS.idMembership WHERE PV.createdAt BETWEEN ? AND ? GROUP BY A.idAdmin HAVING A.idAdmin = ? ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadPaymentsProducts(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                idAdmin,
                (idAdmin == 0)
                        ? "SELECT M.name, SUM(PD.price) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_PRODUCTS PD ON A.idAdmin = PD.createdBy AND PD.flag = 1 JOIN PRODUCTS P ON PD.idProduct = P.idProduct WHERE PD.createdAt BETWEEN ? AND ? GROUP BY A.idAdmin ORDER BY metadata DESC"
                        : "SELECT M.name, SUM(PD.price) AS 'metadata' FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember JOIN PAYMENTS_PRODUCTS PD ON A.idAdmin = PD.createdBy AND PD.flag = 1 JOIN PRODUCTS P ON PD.idProduct = P.idProduct WHERE PD.createdAt BETWEEN ? AND ? GROUP BY A.idAdmin HAVING idAdmin = ? ORDER BY metadata DESC"
        );
    }

    public static CompletableFuture<ObservableList<Model_Admin>> ReadTotalMembersByMembership(String from, String to, Integer idAdmin) {
        return JDBC_Summary.ReadHandler(
                from,
                to,
                0,
                (idAdmin == 0)
                        ? "SELECT MS.name, COUNT(PM.idPaymentMembership) AS 'metadata' FROM MEMBERSHIPS MS JOIN PAYMENTS_MEMBERSHIPS PM ON MS.idMembership = PM.idMembership AND PM.flag = 1 AND PM.startDateTime BETWEEN ? AND ? GROUP BY MS.idMembership ORDER BY metadata DESC"
                        : "SELECT MS.name, COUNT(PM.idPaymentMembership) AS 'metadata' FROM MEMBERSHIPS MS JOIN PAYMENTS_MEMBERSHIPS PM ON MS.idMembership = PM.idMembership AND PM.createdBy = " + idAdmin + " AND PM.flag = 1 AND PM.startDateTime BETWEEN ? AND ? GROUP BY MS.idMembership ORDER BY metadata DESC"
        );
    }
}
