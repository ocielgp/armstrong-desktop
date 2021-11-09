package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class JDBC_Check_In {
    synchronized public static void CreateCheckIn(boolean isStaff, int idMember, int openedBy) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                if (isStaff) {
                    ps = con.prepareStatement("SELECT M.name, M.lastName, MP.photo, A.username, G.name AS 'gymName' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember JOIN ADMINS A on M.idMember = A.idMember JOIN ADMINS_ROLES AR on AR.idRole = A.idRole JOIN GYMS G on M.idGym = G.idGym WHERE M.idMember = ?");
                    ps.setInt(1, idMember);
                    rs = ps.executeQuery();

                    byte[] photo = rs.getBytes("photo");
                    String name = rs.getString("name") + " " + rs.getString("lastName");
                    String gym = rs.getString("gymName");
                    String membership = rs.getString("name");

                    if (rs.next()) {
                        Application.showUserInfo(
                                Styles.EPIC,
                                photo,
                                idMember,
                                name,
                                gym,
                                membership
                        );
                    }
                } else {
                    ps = con.prepareStatement("SELECT MP.photo, M.name, M.lastName, M.access, MS.description, M.idGym AS 'idGymMember', DATE(PM.endDateTime) AS 'endDateTime', PM.idGym AS 'idGymPayment', (SELECT COUNT(idDebt) > 0 FROM DEBTS WHERE idMember = M.idMember AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC) AS 'haveDebts' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember LEFT JOIN PAYMENTS_MEMBERSHIPS PM ON PM.idPaymentMembership = (SELECT idPaymentMembership FROM PAYMENTS_MEMBERSHIPS WHERE idMember = M.idMember AND flag = 1 ORDER BY PM.startDateTime DESC LIMIT 1) LEFT JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership WHERE M.idMember = ? AND M.flag = 1");
                    ps.setInt(1, idMember);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        byte[] photo = rs.getBytes("photo");
                        String name = rs.getString("name") + " " + rs.getString("lastName");
                        String membership = rs.getString("description");

                        if (rs.getString("description") == null) { // no payment found
                            JDBC_Gym.ReadGym(rs.getInt("idGymMember")).thenAccept(model_gyms -> {
                                Application.showUserInfo(
                                        Styles.DANGER,
                                        photo,
                                        idMember,
                                        name,
                                        model_gyms.getName(),
                                        "N / A"
                                );
                            });
                        } else { // payment found
                            boolean access = rs.getBoolean("access");
                            long daysLeft = DateTime.getDaysLeft(rs.getString("endDateTime"));
                            boolean haveDebts = rs.getBoolean("haveDebts");
                            JDBC_Gym.ReadGym(rs.getInt("idGymPayment")).thenAccept(model_gyms -> {
                                Application.showUserInfo(
                                        JDBC_Member.ReadStyle(
                                                access,
                                                daysLeft,
                                                haveDebts
                                        ),
                                        photo,
                                        idMember,
                                        name,
                                        model_gyms.getName(),
                                        membership
                                );
                            });
                        }
                    }
                }

                ps = con.prepareStatement("INSERT INTO CHECK_IN(dateTime, idMember, idGym, openedBy) VALUE (NOW(), ?, ?, ?)");
                ps.setInt(1, idMember); // idMember
                ps.setInt(2, Application.getCurrentGym().getIdGym()); // idGym
                ps.setInt(3, openedBy); // openedBy
                ps.executeUpdate();
                Loading.close();
                System.out.println("Libre");
            } catch (SQLException sqlException) {
                if (sqlException.getErrorCode() != 1062) {
                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
                }
                Loading.close();
            } finally {
                DataServer.closeConnection(con);
                JDBC_Member_Fingerprint.isReaderAvailable = true;
            }
        });
    }
}
