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
    synchronized public static CompletableFuture<Boolean> CreateCheckIn(int idMember, int openedBy) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("INSERT INTO CHECK_IN(idMember, idGym, openedBy) VALUE (?, ?, ?)");
                ps.setInt(1, idMember); // idMember
                ps.setInt(2, Application.GetCurrentGym().getIdGym()); // idGym
                ps.setInt(3, openedBy); // openedBy
                ps.executeUpdate();
                return true;
            } catch (SQLException sqlException) {
                if (sqlException.getErrorCode() != 1062) { // ignore duplicate rows
                    Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
                }
            } finally {
                DataServer.CloseConnection(con);
                JDBC_Member_Fingerprint.isReaderAvailable = true;
            }
            return false;
        });
    }

    public static void CheckInByAdmin(int idMember) {
        Loading.show();
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT M.idMember, A.idAdmin AS 'idAdmin' FROM MEMBERS M LEFT JOIN ADMINS A on M.idMember = A.idAdmin WHERE M.idMember = ? ORDER BY M.createdAt");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getBoolean("idAdmin"))
                    JDBC_Check_In.ShowAdminInfo(idMember, Application.GetModelAdmin().getIdMember());
                else JDBC_Check_In.ShowMemberInfo(idMember, Application.GetModelAdmin().getIdMember());
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
    }

    public static void ShowMemberInfo(int idMember, int openedBy) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT MP.photo, M.name, M.lastName, M.access, MS.name AS 'membershipName', M.idGym AS 'idGymMember', PM.price AS 'payment', PM.months, PM.endDateTime, PM.idGym AS 'idGymPayment', (SELECT COUNT(idDebt) > 0 FROM DEBTS WHERE idMember = M.idMember AND debtStatus = 1 AND flag = 1 ORDER BY M.createdAt DESC) AS 'haveDebts' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember LEFT JOIN PAYMENTS_MEMBERSHIPS PM ON PM.idPaymentMembership = (SELECT idPaymentMembership FROM PAYMENTS_MEMBERSHIPS WHERE idMember = M.idMember AND flag = 1 ORDER BY startDateTime DESC LIMIT 1) LEFT JOIN MEMBERSHIPS MS ON PM.idMembership = MS.idMembership WHERE M.idMember = ? AND M.flag = 1");
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
                    String price = rs.getString("payment");
                    String months = rs.getString("months");

                    String membershipDescription = "(" + months + "x" + price + ") " + membershipName +
                            ", termina el " + DateTime.getDateShort(endDateTime) +
                            " (" + daysLeft + (daysLeft == 1 ? "día" : " días") + ")";
                    JDBC_Gym.ReadGym(rs.getInt("idGymPayment")).thenAccept(model_gyms -> {
                        String style = JDBC_Member.ReadStyle(
                                access,
                                daysLeft,
                                haveDebts
                        );
                        if (haveDebts) {
                            Notifications.Danger("Deudor", "El socio tiene adeudos pendientes");
                        } else if (!access) {
                            Notifications.Danger("Bloqueado", "El socio no tiene acceso al los gimnasios");
                        } else if (style.equals(Styles.SUCCESS) || style.equals(Styles.WARN)) {
                            JDBC_Check_In.CreateCheckIn(idMember, openedBy).thenAccept(isOk -> {
                                if (isOk)
                                    Notifications.Success("Entrada", "Entrada de " + name + " registrada", 2);
                            });
                        }
                        Application.ShowUserInfo(
                                style,
                                photo,
                                idMember,
                                name,
                                model_gyms.getName(),
                                membershipDescription
                        );
                    });
                } else { // no payment found
                    JDBC_Gym.ReadGym(rs.getInt("idGymMember")).thenAccept(model_gyms -> Application.ShowUserInfo(
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
            DataServer.CloseConnection(con);
            Loading.closeNow();
        }
    }

    public static void ShowAdminInfo(int idMember, int openedBy) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT M.name, M.lastName, MP.photo, AR.name AS 'roleName', G.name AS 'gymName' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember JOIN ADMINS A on M.idMember = A.idAdmin JOIN ADMINS_ROLES AR on AR.idRole = A.idRole JOIN GYMS G on M.idGym = G.idGym WHERE M.idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                byte[] photoBytes = rs.getBytes("photo");
                Image photo = (photoBytes == null) ? FileLoader.getDefaultImage() : FileLoader.loadImage(photoBytes);
                String name = rs.getString("name");
                String gym = rs.getString("gymName");
                String roleName = rs.getString("roleName");

                JDBC_Check_In.CreateCheckIn(idMember, openedBy).thenAccept(isOk -> {
                    if (isOk) {
                        Application.ShowUserInfo(
                                Styles.EPIC,
                                photo,
                                idMember,
                                name,
                                gym,
                                roleName
                        );
                        Notifications.Success("Entrada", "Entrada de " + name + " registrada", 2);
                    }
                });
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
            Loading.closeNow();
        }
    }
}
