package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.controller.Controller_Door;
import com.ocielgp.models.Model_Check_In;
import com.ocielgp.utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class JDBC_Check_In {
    public static CompletableFuture<Boolean> CreateCheckIn(int idMember, int createdBy) {
        return CompletableFuture.supplyAsync(() -> {
            if (JDBC_Member_Fingerprint.lastMemberIdFingerprint == idMember) { // same member fingerprint
                return true;
            } else {
                Connection con = DataServer.GetConnection();
                try {
                    PreparedStatement ps;
                    assert con != null;
                    ps = con.prepareStatement("INSERT INTO CHECK_IN(createdBy, idMember, idGym) VALUE (?, ?, ?)");
                    ps.setInt(1, createdBy); // createdBy
                    ps.setInt(2, idMember); // idMember
                    ps.setInt(3, Application.GetCurrentGym().getIdGym()); // idGym
                    ps.executeUpdate();
                    JDBC_Member_Fingerprint.lastMemberIdFingerprint = idMember;
                    return true;
                } catch (SQLException sqlException) {
                    if (sqlException.getErrorCode() != 1062) { // ignore duplicate rows
                        Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
                    }
                } finally {
//                Fingerprint_Log.generateLog("[Fingerprint]: Process finalized");
                    DataServer.CloseConnection(con);
                }
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
            ps = con.prepareStatement("SELECT M.idMember, A.idAdmin FROM MEMBERS M LEFT JOIN ADMINS A on M.idMember = A.idAdmin WHERE M.idMember = ? ORDER BY M.createdAt");
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
                    BigDecimal price = rs.getBigDecimal("payment");
                    short months = rs.getShort("months");

                    String membershipDescription = "(" + months + "x" + price.divide(new BigDecimal(months)) + ") " + membershipName +
                            ", termina el " + DateTime.getDateShort(endDateTime) +
                            " (" + daysLeft + (daysLeft == 1 ? "día" : " días") + ")";
                    JDBC_Gym.ReadGym(rs.getInt("idGymPayment")).thenAccept(model_gyms -> {
                        String style = JDBC_Member.ReadStyle(
                                access,
                                daysLeft,
                                haveDebts
                        );
                        if (haveDebts) {
                            Controller_Door.PURPLE();
                            Notifications.Danger("Deudor", "Este socio tiene adeudos pendientes");
                        } else if (!access) {
                            Controller_Door.RED();
                            Notifications.Danger("Bloqueado", "Este socio no tiene acceso al los gimnasios");
                        } else if (style.equals(Styles.SUCCESS) || style.equals(Styles.WARN)) {
                            if (price.equals("0.00")) {
                                style = Styles.EPIC;
                            }

                            if (daysLeft > 3) {
                                Controller_Door.GREEN();
                            } else if (daysLeft >= 0) {
                                Controller_Door.YELLOW();
                            }
                            JDBC_Check_In.CreateCheckIn(idMember, openedBy).thenAccept(isOk -> {
                                if (isOk) {
                                    Notifications.Success("Entrada", "Entrada de " + name + " registrada", 2);
                                }
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
                    JDBC_Gym.ReadGym(rs.getInt("idGymMember")).thenAccept(model_gyms -> {
                        Controller_Door.RED();
                        Application.ShowUserInfo(
                                Styles.DANGER,
                                photo,
                                idMember,
                                name,
                                model_gyms.getName(),
                                "N / A"
                        );
                    });
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
            ps = con.prepareStatement("SELECT M.name, M.lastName, MP.photo, AR.name AS 'roleName', M.access, G.name AS 'gymName' FROM MEMBERS M LEFT JOIN MEMBERS_PHOTOS MP ON M.idMember = MP.idMember JOIN ADMINS A on M.idMember = A.idAdmin JOIN ADMINS_ROLES AR on AR.idRole = A.idRole JOIN GYMS G on M.idGym = G.idGym WHERE M.idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                byte[] photoBytes = rs.getBytes("photo");
                Image photo = (photoBytes == null) ? FileLoader.getDefaultImage() : FileLoader.loadImage(photoBytes);
                String name = rs.getString("name");
                boolean access = rs.getBoolean("access");
                String gym = rs.getString("gymName");
                String roleName = rs.getString("roleName");

                if (!access) {
                    Application.ShowUserInfo(
                            Styles.DANGER,
                            photo,
                            idMember,
                            name,
                            gym,
                            roleName
                    );
                    Notifications.Danger("Bloqueado", "Este administrador no tiene acceso al los gimnasios");
                } else {
                    JDBC_Check_In.CreateCheckIn(idMember, openedBy).thenAccept(isOk -> {
                        if (isOk) {
                            Controller_Door.GREEN();
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
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
            Loading.closeNow();
        }
    }

    public static CompletableFuture<QueryRows> ReadAllCheckIn(int maxRows, AtomicInteger page, String query, String from, String to) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement statementLimited, statement;
                ResultSet rs;
                // query initial
                String sqlQuery = "SELECT CI.createdAt, CONCAT(M.name, ' ', M.lastName) AS 'memberName', A.username, G.name AS 'gymName', CI.createdBy = 1 'openedBySystem' FROM CHECK_IN CI JOIN MEMBERS M ON CI.idMember = M.idMember JOIN ADMINS A ON CI.createdBy = A.idAdmin JOIN GYMS G ON CI.idGym = G.idGym WHERE M.idMember > 1 AND CI.createdAt BETWEEN ? AND ? ";

                // fieldSearchContent
                if (query.length() > 0) {
                    sqlQuery += "AND (CONCAT(M.name, ' ', M.lastName) LIKE ? OR G.name LIKE ? OR A.username LIKE ?) ";
                }

                assert con != null;
                statement = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                // limit query ( pagination purposes )
                sqlQuery += "ORDER BY CI.createdAt DESC LIMIT ?,?";
                statementLimited = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                ParameterMetaData parameters = statementLimited.getParameterMetaData();
                int maxRegisters = maxRows * page.get();
                if (parameters.getParameterCount() == 4) {
                    statementLimited.setString(1, from);
                    statementLimited.setString(2, to);
                    statementLimited.setInt(3, maxRegisters - maxRows); // limit ?
                    statementLimited.setInt(4, maxRows); // limit ?,?

                    statement.setString(1, from);
                    statement.setString(2, to);
                } else {
                    statementLimited.setString(1, from);
                    statementLimited.setString(2, to);
                    statementLimited.setString(3, "%" + query + "%"); // firstName and lastName
                    statementLimited.setString(4, "%" + query + "%"); // gymName
                    statementLimited.setString(5, "%" + query + "%"); // username
                    statementLimited.setInt(6, maxRegisters - maxRows); // limit ?
                    statementLimited.setInt(7, maxRows); // limit ?,?

                    statement.setString(1, from);
                    statement.setString(2, to);
                    statement.setString(3, "%" + query + "%"); // firstName and lastName
                    statement.setString(4, "%" + query + "%"); // gymName
                    statement.setString(5, "%" + query + "%"); // username
                }

                int totalRows = DataServer.CountRows(statement);
                int totalPages = (int) Math.ceil((double) totalRows / maxRows);
                rs = statementLimited.executeQuery();
                ObservableList<Model_Check_In> modelCheckIns = FXCollections.observableArrayList();
                while (rs.next()) {
                    Model_Check_In modelCheckIn = new Model_Check_In();
                    modelCheckIn.setDateTime(DateTime.MySQLToJavaMX(rs.getString("createdAt")));
                    modelCheckIn.setAdminName(InputProperties.capitalizeFirstLetter(rs.getString("username")));
                    modelCheckIn.setMemberName(rs.getString("memberName"));
                    modelCheckIn.setGymName(rs.getString("gymName"));
                    modelCheckIn.setOpenedBySystem(rs.getBoolean("openedBySystem"));

                    modelCheckIns.add(modelCheckIn);
                }
                return new QueryRows(modelCheckIns, totalRows, totalPages);
            } catch (Exception sqlException) {
//                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return null;
        });
    }
}
