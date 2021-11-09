package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.utilities.*;
import javafx.scene.image.Image;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class JDBC_Check_In {
    synchronized public static void CreateCheckIn(int idMember, int openedBy) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("INSERT INTO CHECK_IN(dateTime, idMember, idGym, openedBy) VALUE (NOW(), ?, ?, ?)");
                ps.setInt(1, idMember); // idMember
                ps.setInt(2, Application.getCurrentGym().getIdGym()); // idGym
                ps.setInt(3, openedBy); // openedBy
                ps.executeUpdate();
                System.out.println("Libre");
            } catch (SQLException sqlException) {
                if (sqlException.getErrorCode() != 1062) { // ignore duplicate rows
                    Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
                }
            } finally {
                DataServer.closeConnection(con);
                JDBC_Member_Fingerprint.isReaderAvailable = true;
            }
        });
    }

    public static void checkInSystem(int idMember) {
        Loading.show();
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT M.idMember, A.idMember AS 'idAdmin' FROM MEMBERS M LEFT JOIN ADMINS A on M.idMember = A.idMember WHERE M.idMember = ? ORDER BY M.createdAt");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            JDBC_Check_In.CreateCheckIn(idMember, Application.getModelAdmin().getIdMember());
            if (rs.next()) {
                if (rs.getBoolean("idAdmin")) showAdminInfo(idMember);
                else showMemberInfo(idMember);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
    }

    public static void showMemberInfo(int idMember) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT MP.photo, M.name, M.lastName, M.access, MS.name AS 'membershipName', M.idGym AS 'idGymMember', PM.months, PM.endDateTime, PM.idGym AS 'idGymPayment', (SELECT COUNT(idDebt) > 0 FROM DEBTS WHERE idMember = M.idMember AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC) AS 'haveDebts' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember LEFT JOIN PAYMENTS_MEMBERSHIPS PM ON PM.idPaymentMembership = (SELECT idPaymentMembership FROM PAYMENTS_MEMBERSHIPS WHERE idMember = M.idMember AND flag = 1 ORDER BY PM.startDateTime DESC LIMIT 1) LEFT JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership WHERE M.idMember = ? AND M.flag = 1");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                byte[] photoBytes = rs.getBytes("photo");
                Image photo = (photoBytes == null) ? FileLoader.getDefaultImage() : FileLoader.loadImage(photoBytes);
                String name = rs.getString("name") + " " + rs.getString("lastName");
                String membershipName = rs.getString("membershipName");

                if (membershipName != null) { // payment found
                    boolean access = rs.getBoolean("access");
                    LocalDateTime endDateTime = DateTime.MySQLToJava(rs.getString("endDateTime"));
                    long daysLeft = DateTime.getDaysLeft(endDateTime);
                    boolean haveDebts = rs.getBoolean("haveDebts");
                    String months = rs.getString("months");

                    StringBuilder membershipDescription = new StringBuilder();
                    membershipDescription.append("(").append(months).append(") ").append(membershipName);
                    membershipDescription.append(", termina el ").append(DateTime.getDateShort(endDateTime));
                    membershipDescription.append(" (").append(daysLeft).append((daysLeft == 1 ? "día" : " días")).append(")");
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
                                membershipDescription.toString()
                        );
                    });
                } else { // no payment found
                    JDBC_Gym.ReadGym(rs.getInt("idGymMember")).thenAccept(model_gyms -> Application.showUserInfo(
                            Styles.DANGER,
                            photo,
                            idMember,
                            name,
                            model_gyms.getName(),
                            "N / A"
                    ));
                }
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.closeConnection(con);
            Loading.closeNow();
        }
    }

    public static void showAdminInfo(int idMember) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT M.name, M.lastName, MP.photo, A.username, G.name AS 'gymName' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember JOIN ADMINS A on M.idMember = A.idMember JOIN ADMINS_ROLES AR on AR.idRole = A.idRole JOIN GYMS G on M.idGym = G.idGym WHERE M.idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            byte[] photoBytes = rs.getBytes("photo");
            Image photo = (photoBytes == null) ? FileLoader.getDefaultImage() : FileLoader.loadImage(photoBytes);
            String name = rs.getString("name") + " " + rs.getString("lastName");
            String gym = rs.getString("gymName");

            if (rs.next()) {
                Application.showUserInfo(
                        Styles.EPIC,
                        photo,
                        idMember,
                        name,
                        gym,
                        "Empleado"
                );
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.closeConnection(con);
            Loading.closeNow();
        }
    }
}
