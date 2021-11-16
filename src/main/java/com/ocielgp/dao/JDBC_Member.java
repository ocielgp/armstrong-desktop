package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.models.Model_Member;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.ParamBuilder;
import com.ocielgp.utilities.Styles;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class JDBC_Member {
    public static int CreateMember(Model_Member modelMember) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO MEMBERS(name, lastName, gender, notes, idGym) VALUE (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, modelMember.getName()); // name
            ps.setString(2, modelMember.getLastName()); // lastName
            ps.setString(3, modelMember.getGender()); // gender
            if (modelMember.getNotes().equals("")) ps.setNull(4, Types.NULL); // notes
            else ps.setString(4, modelMember.getNotes());
            ps.setInt(5, Application.GetCurrentGym().getIdGym()); // idGym
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) { // return new id member
                return rs.getInt(1);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return 0;
    }

    public static CompletableFuture<ObservableList<String>> ReadGenders() {
        return CompletableFuture.supplyAsync(() -> FXCollections.observableArrayList("Hombre", "Mujer"));
    }

    public static String ReadStyle(boolean access, long daysLeft, boolean haveDebts) {
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
            Connection con = DataServer.GetConnection();
            Model_Member modelMember = new Model_Member();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT name, lastName, gender, notes, createdAt, access, idGym FROM MEMBERS WHERE idMember = ? ORDER BY idMember DESC");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelMember.setName(rs.getString("name"));
                    modelMember.setLastName(rs.getString("lastName"));
                    modelMember.setGender(rs.getString("gender"));
                    modelMember.setNotes(rs.getString("notes") == null ? "" : rs.getString("notes"));
                    modelMember.setCreatedAt(DateTime.MySQLToJava(rs.getString("createdAt")));
                    modelMember.setAccess(rs.getBoolean("access"));
                    modelMember.setIdGym(rs.getInt("idGym"));

                    modelMember.setModelMemberPhoto(JDBC_Member_Photo.ReadPhoto(idMember));
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return modelMember;
        });
    }

    public static CompletableFuture<QueryRows> ReadMembers(int maxRows, AtomicInteger page, String query) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement statementLimited, statement;
                ResultSet rs;
                // query initial
                String sqlQuery = "SELECT M.idMember, M.name, M.lastName, M.access, endDateTime, (SELECT COUNT(idDebt) > 0 FROM DEBTS WHERE idMember = M.idMember AND debtStatus = 1 AND flag = 1 ORDER BY dateTime DESC) AS 'haveDebts', PM.flag AS 'flag' FROM MEMBERS M LEFT JOIN PAYMENTS_MEMBERSHIPS PM ON PM.idPaymentMembership = (SELECT idPaymentMembership FROM PAYMENTS_MEMBERSHIPS WHERE idMember = M.idMember AND flag = 1 ORDER BY startDateTime DESC LIMIT 1) WHERE M.idMember NOT IN (SELECT A.idMember FROM ADMINS A WHERE A.flag = 1) AND M.flag = 1 ";

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
                if (!UserPreferences.GetPreferenceBool("FILTER_MEMBER_ALL_GYMS")) {
                    sqlQuery += "AND PM.idGym = " + Application.GetCurrentGym().getIdGym() + " ";
                }
                if (UserPreferences.GetPreferenceBool("FILTER_MEMBER_ACTIVE_MEMBERS")) {
                    sqlQuery += "AND M.access = 1 AND PM.endDateTime >= CURRENT_DATE ";
                }
                if (UserPreferences.GetPreferenceBool("FILTER_MEMBER_DEBTORS")) {
                    sqlQuery += "AND M.idMember IN (SELECT DISTINCT D.idMember FROM DEBTS D WHERE D.debtStatus = 1 AND D.flag = 1) ";
                }

                int genderFilter = UserPreferences.GetPreferenceInt("FILTER_MEMBER_GENDERS");
                if (genderFilter == 1) {
                    sqlQuery += "AND M.gender = 'Hombre' ";
                } else if (genderFilter == 2) {
                    sqlQuery += "AND M.gender = 'Mujer' ";
                }

                if (UserPreferences.GetPreferenceInt("FILTER_MEMBER_ORDER_BY") == 0) {
                    sqlQuery += "ORDER BY M.idMember DESC ";
                } else { // 1
                    sqlQuery += "ORDER BY M.idMember ";
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

                int totalRows = DataServer.CountRows(statement);
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

                        LocalDateTime endDateTime = DateTime.MySQLToJava(rs.getString("endDateTime"));
                        modelMember.setEndDate(DateTime.getDateShort(endDateTime));
                        long daysLeft = DateTime.getDaysLeft(endDateTime);
                        modelMember.setStyle(JDBC_Member.ReadStyle(modelMember.getAccess(), daysLeft, rs.getBoolean("haveDebts")));
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
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return null;
        });
    }

    public static boolean UpdateMember(Model_Member modelMember) {
        ParamBuilder paramBuilder = new ParamBuilder("MEMBERS", "idMember", modelMember.getIdMember());
        paramBuilder.addParam("name", modelMember.getName());
        paramBuilder.addParam("lastName", modelMember.getLastName());
        paramBuilder.addParam("gender", modelMember.getGender());
        paramBuilder.addParam("notes", modelMember.getNotes());
        return paramBuilder.executeUpdate();
    }

    public static CompletableFuture<Boolean> UpdateAccess(int idMember, boolean access) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement ps;
                assert con != null;
                ps = con.prepareStatement("UPDATE MEMBERS SET access = ? WHERE idMember = ?");
                ps.setBoolean(1, !access);
                ps.setInt(2, idMember);
                ps.executeUpdate();
                return true;
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return false;
        });
    }
}
