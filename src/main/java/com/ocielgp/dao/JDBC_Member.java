package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.database.DataServer;
import com.ocielgp.database.QueryRows;
import com.ocielgp.database.Utilities;
import com.ocielgp.models.Model_Member;
import com.ocielgp.utilities.DateFormatter;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class JDBC_Member {
    public static CompletableFuture<Integer> CreateMember(Model_Member modelMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("INSERT INTO MEMBERS(name, lastName, gender, phone, email, notes, registrationDate, idGym) VALUE (?, ?, ?, ?, ?, ?, CURDATE(), ?);", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, modelMember.getName()); // name
                ps.setString(2, modelMember.getLastName()); // lastName
                ps.setString(3, modelMember.getGender()); // gender
                if (modelMember.getPhone().equals("")) ps.setNull(4, Types.NULL); // phone
                else ps.setString(4, modelMember.getPhone());
                if (modelMember.getEmail().equals("")) ps.setNull(5, Types.NULL); // email
                else ps.setString(5, modelMember.getEmail());
                if (modelMember.getNotes().equals("")) ps.setNull(6, Types.NULL); // notes
                else ps.setString(6, modelMember.getNotes());
                ps.setInt(7, modelMember.getIdGym()); // idGym
                ps.executeUpdate();

                rs = ps.getGeneratedKeys();
                if (rs.next()) { // return new id member
                    return rs.getInt(1);
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return 0;
        });
    }

    public static CompletableFuture<ObservableList<String>> ReadGenders() {
        return CompletableFuture.supplyAsync(() -> FXCollections.observableArrayList("Hombre", "Mujer"));
    }

    public static Styles ReadStyle(int idMember) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("SELECT M.access, PM.endDate - CURRENT_DATE AS 'daysLeft', (SELECT COUNT(idDebt) > 0 FROM DEBTS WHERE idMember = M.idMember AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC) AS 'haveDebts' FROM MEMBERS M JOIN PAYMENTS_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE (M.flag = 1) AND M.idMember = ? ORDER BY M.idMember DESC");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            if (rs.next()) {
                boolean access = rs.getBoolean("access");
                long daysLeft = rs.getInt("daysLeft");
                boolean haveDebts = rs.getBoolean("haveDebts");
                return JDBC_Member.ReadStyle(access, daysLeft, haveDebts);
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
        return Styles.DANGER;
    }

    public static Styles ReadStyle(boolean access, long daysLeft, boolean haveDebts) {
        /* DATES
         *  - 0 DAYS = DANGER
         * 1-3 DAYS = WARN
         * + 3 DAYS = SUCCESS
         */
        if (haveDebts) {
            return Styles.CREATIVE;
        } else if (!access) {
            return Styles.DANGER;
        } else {
            if (daysLeft >= 0 && daysLeft <= 3) {
                return Styles.WARN;
            } else if (daysLeft > 3) {
                return Styles.SUCCESS;
            } else {
                return Styles.DANGER;
            }
        }
    }

    public static CompletableFuture<Model_Member> ReadMember(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            Model_Member modelMember = new Model_Member();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT name, lastName, gender, phone, email, notes, registrationDate, access, idGym FROM MEMBERS WHERE idMember = ? ORDER BY idMember DESC");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelMember.setName(rs.getString("name"));
                    modelMember.setLastName(rs.getString("lastName"));
                    modelMember.setGender(rs.getString("gender"));
                    modelMember.setPhone(rs.getString("phone") == null ? "" : rs.getString("phone"));
                    modelMember.setEmail(rs.getString("email") == null ? "" : rs.getString("email"));
                    modelMember.setNotes(rs.getString("notes") == null ? "" : rs.getString("notes"));
                    modelMember.setRegistrationDate(rs.getString("registrationDate"));
                    modelMember.setAccess(rs.getBoolean("access"));
                    modelMember.setIdGym(rs.getInt("idGym"));
                    try {
                        JDBC_Member_Photo.ReadPhoto(idMember).thenAccept(modelMember::setModelMemberPhoto).get();
                    } catch (Exception exception) {
                        Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception.getMessage(), exception);
                    }
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return modelMember;
        });
    }

    public static CompletableFuture<QueryRows> ReadMembers(int maxRows, AtomicInteger page, String query) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement statementLimited, statement;
                ResultSet rs;
                // query initial
                String sqlQuery = "SELECT M.idMember, M.name, M.lastName, M.access, PM.endDate, (SELECT COUNT(idDebt) > 0 FROM DEBTS WHERE idMember = M.idMember AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC) AS 'haveDebts', PM.flag AS 'flag' FROM MEMBERS M LEFT JOIN PAYMENTS_MEMBERSHIPS PM ON PM.idPaymentMembership = (SELECT idPaymentMembership FROM PAYMENTS_MEMBERSHIPS WHERE idMember = M.idMember AND flag = 1 ORDER BY startDate DESC LIMIT 1) WHERE M.idMember NOT IN (SELECT SM.idMember FROM STAFF_MEMBERS SM WHERE SM.flag = 1) AND M.flag = 1 ";

                // fieldSearchContent
                if (query.length() > 0) {
                    try {
                        Integer.parseInt(query);
                        sqlQuery += "AND M.idMember = ? ";
                    } catch (NumberFormatException exception) {
                        sqlQuery += "AND (M.name LIKE ? OR M.lastName LIKE ?) ";
                    }
                }

                // filters
                if (!UserPreferences.getPreferenceBool("FILTER_MEMBER_ALL_GYMS")) {
                    sqlQuery += "AND PM.idGym = " + Application.getCurrentGym().getIdGym() + " ";
                }
                if (UserPreferences.getPreferenceBool("FILTER_MEMBER_ACTIVE_MEMBERS")) {
                    sqlQuery += "AND PM.endDate >= CURRENT_DATE ";
                }
                if (UserPreferences.getPreferenceBool("FILTER_MEMBER_DEBTORS")) {
                    sqlQuery += "AND M.idMember IN (SELECT DISTINCT D.idMember FROM DEBTS D WHERE D.debtStatus = 1 AND D.flag = 1) ";
                }

                int genderFilter = UserPreferences.getPreferenceInt("FILTER_MEMBER_GENDERS");
                if (genderFilter == 1) {
                    sqlQuery += "AND M.gender = 'Hombre' ";
                } else if (genderFilter == 2) {
                    sqlQuery += "AND M.gender = 'Mujer' ";
                }

                if (UserPreferences.getPreferenceInt("FILTER_MEMBER_ORDER_BY") == 0) {
                    sqlQuery += "ORDER BY M.idMember DESC ";
                } else { // 1
                    sqlQuery += "ORDER BY M.registrationDate ";
                }

                assert con != null;
                statement = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                // limit query ( pagination purposes )
                sqlQuery += "LIMIT ?,?";
                statementLimited = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                ParameterMetaData parameters = statementLimited.getParameterMetaData();
                if (parameters != null) {
                    int maxRegisters = maxRows * page.get();
                    if (parameters.getParameterCount() == 2) {
                        statementLimited.setInt(1, maxRegisters - maxRows); // limit ?
                        statementLimited.setInt(2, maxRows); // limit ?,?
                    } else if (parameters.getParameterCount() == 3) {
                        statementLimited.setInt(1, Integer.parseInt(query)); // idMember
                        statementLimited.setInt(2, maxRegisters - maxRows); // limit ?
                        statementLimited.setInt(3, maxRows); // limit ?,?

                        statement.setInt(1, Integer.parseInt(query)); // idMember
                    } else if (parameters.getParameterCount() == 4) {
                        statementLimited.setString(1, "%" + query + "%"); // name
                        statementLimited.setString(2, "%" + query + "%"); // lastName
                        statementLimited.setInt(3, maxRegisters - maxRows); // limit ?
                        statementLimited.setInt(4, maxRows); // limit ?,?

                        statement.setString(1, "%" + query + "%"); // name
                        statement.setString(2, "%" + query + "%"); // lastName
                    }
                }

                int totalRows = Utilities.countRows(statement);
                int totalPages = (int) Math.ceil((double) totalRows / maxRows);
                rs = statementLimited.executeQuery();
                ObservableList<Model_Member> members = FXCollections.observableArrayList();
                while (rs.next()) {
                    Model_Member modelMember = new Model_Member();
                    if (rs.getBoolean("flag")) {
                        modelMember.setIdMember(rs.getInt("idMember"));
                        modelMember.setName(rs.getString("name"));
                        modelMember.setLastName(rs.getString("lastName"));
                        modelMember.setAccess(rs.getBoolean("access"));

                        LocalDate endDate = LocalDate.parse(rs.getString("endDate"));
                        modelMember.setEndDate(DateFormatter.getDayMonthYearShort(endDate));
                        long daysLeft = DateFormatter.daysDifferenceToday(endDate);
                        modelMember.setStyle(JDBC_Member.ReadStyle(modelMember.isAccess(), daysLeft, rs.getBoolean("haveDebts")));
                    } else {
                        modelMember.setIdMember(rs.getInt("idMember"));
                        modelMember.setName(rs.getString("name"));
                        modelMember.setLastName(rs.getString("lastName"));
                        modelMember.setEndDate("-");
                        modelMember.setStyle(Styles.DANGER);
                    }
                    members.add(modelMember);
                }
                return new QueryRows(members, totalRows, totalPages);
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return null;
        });
    }

    public static void UpdateName(int idMember, String newName) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET name = ? WHERE idMember = ?");
                ps.setString(1, newName);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }

    public static void UpdateLastName(int idMember, String newLastName) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET lastName = ? WHERE idMember = ?");
                ps.setString(1, newLastName);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }

    public static void UpdateGender(int idMember, String newGender) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET gender = ? WHERE idMember = ?");
                ps.setString(1, newGender);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }

    public static void UpdatePhone(int idMember, String newPhone) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET phone = ? WHERE idMember = ?");
                ps.setString(1, newPhone);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }

    public static void UpdateEmail(int idMember, String newEmail) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET email = ? WHERE idMember = ?");
                ps.setString(1, newEmail);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }

    public static void UpdateNotes(int idMember, String newNotes) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET notes = ? WHERE idMember = ?");
                ps.setString(1, newNotes);
                ps.setInt(2, idMember);
                ps.executeUpdate();
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
        });
    }

    public static CompletableFuture<Boolean> UpdateAccess(int idMember, boolean access) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET access = ? WHERE idMember = ?");
                ps.setBoolean(1, !access);
                ps.setInt(2, idMember);
                ps.executeUpdate();
                return true;
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return false;
        });
    }
}
