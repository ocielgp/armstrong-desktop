package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.database.DataServer;
import com.ocielgp.utilities.DateFormatter;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class JDBC_Check_In {
    public static void CreateCheckIn(boolean isStaff, int idMember, int openedBy) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                if (isStaff) {
                    assert con != null;
                    ps = con.prepareStatement("SELECT M.name, M.lastName, MP.photo, SR.name, G.name AS 'gymName' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember JOIN STAFF_MEMBERS SM on M.idMember = SM.idMember JOIN STAFF_ROLE SR on SM.idRole = SR.idRole JOIN GYMS G on M.idGym = G.idGym WHERE M.idMember = ?");
                    ps.setInt(1, idMember);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        Application.showUserInfo(
                                Styles.EPIC,
                                rs.getBytes("photo"),
                                idMember,
                                rs.getString("name") + " " + rs.getString("lastName"),
                                rs.getString("gymName"),
                                rs.getString("name")
                        );
                    }
                } else {
                    assert con != null;
                    ps = con.prepareStatement("SELECT MP.photo, M.name, M.lastName, M.access, MS.description, PM.endDate, (SELECT COUNT(idDebt) > 0 FROM DEBTS WHERE idMember = M.idMember AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC) AS 'haveDebts', M.idGym AS 'idGymMember', PM.idGym AS 'idGymPayment' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember LEFT JOIN PAYMENTS_MEMBERSHIPS PM ON PM.idPaymentMembership = (SELECT idPaymentMembership FROM PAYMENTS_MEMBERSHIPS WHERE idMember = M.idMember AND flag = 1 ORDER BY startDate DESC LIMIT 1) LEFT JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership WHERE M.idMember = ? AND M.flag = 1");
                    ps.setInt(1, idMember);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getString("description") == null) { // no payment found
                            JDBC_Gym.ReadGym(rs.getInt("idGymMember")).thenAccept(model_gyms -> {
                                try {
                                    Application.showUserInfo(
                                            Styles.DANGER,
                                            rs.getBytes("photo"),
                                            idMember,
                                            rs.getString("name") + " " + rs.getString("lastName"),
                                            model_gyms.getName(),
                                            "N / A"
                                    );
                                } catch (SQLException sqlException) {
                                    Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
                                }
                            });
                        } else { // payment found
                            JDBC_Gym.ReadGym(rs.getInt("idGymPayment")).thenAccept(model_gyms -> {
                                try {
                                    Application.showUserInfo(
                                            JDBC_Member.ReadStyle(
                                                    rs.getBoolean("access"),
                                                    DateFormatter.daysDifferenceToday(LocalDate.parse(rs.getString("endDate"))),
                                                    rs.getBoolean("haveDebts")
                                            ),
                                            rs.getBytes("photo"),
                                            idMember,
                                            rs.getString("name") + " " + rs.getString("lastName"),
                                            model_gyms.getName(),
                                            rs.getString("description") + " (" + DateFormatter.getDateWithDayName(LocalDate.parse(rs.getString("endDate"))) + ")"
                                    );
                                } catch (SQLException sqlException) {
                                    Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
                                }
                            });
                        }
                    }
                }
                Loading.close();
                ps = con.prepareStatement("INSERT INTO CHECK_IN(dateTime, idMember, idGym, openedBy) VALUE (NOW(), ?, ?, ?)");
                ps.setInt(1, idMember); // idMember
                ps.setInt(2, Application.getCurrentGym().getIdGym()); // idGym
                ps.setInt(3, openedBy); // openedBy
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            JDBC_Member_Fingerprint.SCANNING = false;
        });
    }
}
