package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.models.Model_Admin;
import com.ocielgp.utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class JDBC_Admin {
    public static int CreateAdmin(Model_Admin modelAdmin) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO ADMINS(idAdmin, username, password, idRole, createdBy) VALUE (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, modelAdmin.getIdMember()); // idMember
            ps.setString(2, modelAdmin.getUsername()); // username
            ps.setString(3, Hash.generateHash(modelAdmin.getPassword())); // password
            ps.setShort(4, modelAdmin.getIdRole()); // idRole
            ps.setShort(5, Application.GetModelAdmin().getIdRole()); // createdBy
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) { // return new id admin
                return rs.getInt(1);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return 0;
    }

    public static CompletableFuture<Object> ReadLogin(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                String hash = Hash.generateHash(password);
//                System.out.println("hash " + hash);

                assert con != null;
                ps = con.prepareStatement("SELECT A.idAdmin, M.access FROM ADMINS A JOIN MEMBERS M ON A.idAdmin = M.idMember WHERE (A.flag = 1 AND M.flag = 1) AND (BINARY username = ? AND BINARY password = ?)");
                ps.setString(1, username);
                ps.setString(2, hash);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (!rs.getBoolean("access")) {
                        return null;
                    } else {
                        Application.SetModelAdmin(JDBC_Admin.ReadAdmin(rs.getInt("idAdmin")).get());
                        return true;
                    }
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } catch (ExecutionException | InterruptedException exception) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception);
            } finally {
                DataServer.CloseConnection(con);
            }
            return false;
        });
    }

    public static Boolean ReadAdminAvailable(String username) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;

            assert con != null;
            ps = con.prepareStatement("SELECT username FROM ADMINS WHERE username LIKE ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            return !rs.next();
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return false;
    }

    public static CompletableFuture<Model_Admin> ReadAdmin(int idAdmin) {
        return JDBC_Member.ReadMember(idAdmin).thenApply(model_member -> {
            Connection con = DataServer.GetConnection();
            Model_Admin modelAdmin = new Model_Admin();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT idAdmin, username, password, idRole, createdAt, createdBy, updatedAt, updatedBy FROM ADMINS WHERE idAdmin = ? ORDER BY idAdmin DESC");
                ps.setInt(1, idAdmin);
                rs = ps.executeQuery();
                if (rs.next()) {
                    modelAdmin.setIdMember(rs.getInt("idAdmin"));
                    modelAdmin.setName(model_member.getName());
                    modelAdmin.setLastName(model_member.getLastName());
                    modelAdmin.setGender(model_member.getGender());
                    modelAdmin.setNotes(model_member.getNotes());
                    modelAdmin.setAccess(model_member.getAccess());
                    modelAdmin.setIdGym(model_member.getIdGym());
                    modelAdmin.setModelMemberPhoto(model_member.getModelMemberPhoto());

                    modelAdmin.setIdAdmin(rs.getInt("idAdmin"));
                    modelAdmin.setUsername(rs.getString("username"));
                    modelAdmin.setPassword(rs.getString("password"));
                    modelAdmin.setIdRole(rs.getShort("idRole"));
                    modelAdmin.setCreatedAt(DateTime.MySQLToJava(rs.getString("createdAt")));
                    modelAdmin.setCreatedBy(rs.getInt("createdBy"));
                    modelAdmin.setUpdatedAt(DateTime.MySQLToJava(rs.getString("updatedAt")));
                    modelAdmin.setUpdatedBy(rs.getInt("updatedBy"));
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return modelAdmin;
        });
    }

    public static CompletableFuture<QueryRows> ReadAdmins(int maxRows, AtomicInteger page, String query) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement statementLimited, statement;
                ResultSet rs;
                // query initial
                String sqlQuery = "SELECT M.idMember, M.name, M.access, A.username, AR.name AS 'roleName' FROM MEMBERS M JOIN ADMINS A ON M.idMember = A.idAdmin JOIN ADMINS_ROLES AR ON A.idRole = AR.idRole WHERE M.flag = 1 AND A.flag = 1 ";

                // fieldSearchContent
                if (query.length() > 0) {
                    try {
                        Integer.parseInt(query);
                        sqlQuery += "AND M.idMember = ? ";
                    } catch (NumberFormatException ignored) {
                        sqlQuery += "AND (A.username LIKE ? OR M.name LIKE ? OR M.lastName LIKE ?) ";
                    }
                }

                // filters
                if (UserPreferences.GetPreferenceBool("FILTER_MEMBER_ACTIVE_MEMBERS")) {
                    sqlQuery += "AND M.access = 1 ";
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
                    } else if (parameters.getParameterCount() == 5) {
                        statementLimited.setString(1, "%" + query + "%"); // username
                        statementLimited.setString(2, "%" + query + "%"); // name
                        statementLimited.setString(3, "%" + query + "%"); // lastName
                        statementLimited.setInt(4, maxRegisters - maxRows); // limit ?
                        statementLimited.setInt(5, maxRows); // limit ?,?

                        statement.setString(1, "%" + query + "%"); // username
                        statement.setString(2, "%" + query + "%"); // name
                        statement.setString(3, "%" + query + "%"); // lastName
                    }
                }

                int totalRows = DataServer.CountRows(statement);
                int totalPages = (int) Math.ceil((double) totalRows / maxRows);
                rs = statementLimited.executeQuery();
                ObservableList<Model_Admin> admins = FXCollections.observableArrayList();
                while (rs.next()) {
                    Model_Admin modelAdmin = new Model_Admin();
                    modelAdmin.setIdMember(rs.getInt("idMember"));
                    modelAdmin.setName(rs.getString("name"));
                    modelAdmin.setAccess(rs.getBoolean("access"));
                    modelAdmin.setUsername(rs.getString("username"));
                    modelAdmin.setRoleName(rs.getString("roleName"));
                    modelAdmin.setStyle((modelAdmin.getAccess()) ? Styles.SUCCESS : Styles.DANGER);

                    admins.add(modelAdmin);
                }
                return new QueryRows(admins, totalRows, totalPages);
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return null;
        });
    }

    public static boolean UpdateAdmin(Model_Admin modelAdmin) {
        ParamBuilder paramBuilder = new ParamBuilder("ADMINS", "idAdmin", modelAdmin.getIdAdmin());
        paramBuilder.addParam("username", modelAdmin.getUsername());
        paramBuilder.addParam("password", modelAdmin.getPassword());
        paramBuilder.addParam("idRole", modelAdmin.getIdRole());
        return paramBuilder.executeUpdate();
    }
}
