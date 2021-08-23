package com.ocielgp.database.members;

import com.ocielgp.database.QueryRows;
import com.ocielgp.database.Utilities;
import com.ocielgp.database.payments.DATA_DEBTS;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.utilities.DateFormatter;
import com.ocielgp.utilities.Input;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

public class DATA_MEMBERS {
    // TODO: INTENGER OR int difference in bytes
    // CRUD Create Read Update Delete

    public static int CreateMember(MODEL_MEMBERS modelMembers) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("INSERT INTO MEMBERS(name, lastName, gender, phone, email, notes, registrationDate, idGym) VALUE (?, ?, ?, ?, ?, ?, CURDATE(), ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, modelMembers.getName()); // name
            ps.setString(2, modelMembers.getLastName()); // lastName
            ps.setString(3, modelMembers.getGender()); // gender
            if (modelMembers.getPhone().equals("")) ps.setNull(4, Types.NULL); // phone
            else ps.setString(4, modelMembers.getPhone());
            if (modelMembers.getEmail().equals("")) ps.setNull(5, Types.NULL); // email
            else ps.setString(5, modelMembers.getEmail());
            if (modelMembers.getNotes().equals("")) ps.setNull(6, Types.NULL); // notes
            else ps.setString(6, modelMembers.getNotes());
            ps.setInt(7, modelMembers.getIdGym()); // idGym
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) { // Return new id member
                return rs.getInt(1);
            }

        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return 0;
    }

    public static Styles ReadStyle(int idMember) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT M.access, PM.endDate - CURDATE() AS 'daysLeft' FROM MEMBERS M JOIN PAYMENTS_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE (M.flag = 1) AND M.idMember = ? ORDER BY M.idMember DESC");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            if (rs.next()) {
                boolean access = rs.getBoolean("access");
                long daysLeft = rs.getInt("daysLeft");
                boolean haveDebts = DATA_DEBTS.ReadHaveDebts(idMember);

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
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return Styles.DANGER;
    }

    public static MODEL_MEMBERS ReadMember(int idMember) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;
        MODEL_MEMBERS modelMembers = new MODEL_MEMBERS();
        try {
            ps = con.prepareStatement("SELECT name, lastName, gender, phone, email, notes, registrationDate, access, idGym FROM MEMBERS WHERE idMember = ? ORDER BY idMember DESC");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();
            if (rs.next()) {
                modelMembers.setName(rs.getString("name"));
                modelMembers.setLastName(rs.getString("lastName"));
                modelMembers.setGender(rs.getString("gender"));
                modelMembers.setPhone(rs.getString("phone") == null ? "" : rs.getString("phone"));
                modelMembers.setEmail(rs.getString("email") == null ? "" : rs.getString("email"));
                modelMembers.setNotes(rs.getString("notes") == null ? "" : rs.getString("notes"));
                modelMembers.setRegistrationDate(rs.getString("registrationDate"));
                modelMembers.setAccess(rs.getBoolean("access"));
                modelMembers.setIdGym(rs.getInt("idGym"));

                MODEL_MEMBERS_PHOTOS modelMembersPhotos = DATA_MEMBERS_PHOTOS.ReadPhoto(idMember);
                modelMembers.setModelMembersPhotos(modelMembersPhotos);
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return modelMembers;
    }

    public static Task<QueryRows> ReadMembers(int limit, int page, String query, TableView tableView) {
        return new Task<>() {
            @Override
            protected QueryRows call() {
                Connection con = null;
                PreparedStatement statementLimited, statement;
                ResultSet rs;
                try {
                    // Query initial
                    // TODO: OPTIMIZE
                    String sqlQuery = "SELECT M.idMember, M.name, M.lastName, M.access, PM.endDate FROM MEMBERS M LEFT JOIN PAYMENTS_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE M.idMember NOT IN ( SELECT SM.idMember FROM STAFF_MEMBERS SM WHERE SM.flag = 1) AND M.flag = 1 ";

                    // fieldSearchContent
                    if (query.length() > 0) {
                        try {
                            Integer.parseInt(query);
                            sqlQuery += "AND M.idMember = ? ";
                        } catch (NumberFormatException exception) {
                            sqlQuery += "AND (M.name LIKE ? OR M.lastName LIKE ?) ";
                        }
                    }

                    // Filters
                    boolean filterAllGyms = Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberAllGyms"));
                    boolean filterOnlyActiveMembers = Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOnlyActiveMembers"));
                    // TODO DEBT LIST
                    boolean filterOnlyDebtors = Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOnlyDebtors"));
                    if (!filterAllGyms) {
                        sqlQuery += "AND PM.idGym = " + ConfigFiles.readProperty(ConfigFiles.File.APP, "idGym") + " ";
                    }
                    if (filterOnlyActiveMembers) {
                        sqlQuery += "AND PM.endDate >= CURRENT_DATE ";
                    }
                    if (filterOnlyDebtors) {
                        sqlQuery += "AND M.idMember IN (SELECT DISTINCT D.idMember FROM DEBTS D WHERE D.debtStatus = 1 AND D.flag = 1)";
                    }

                    // Gender filter
                    byte filterGender = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberGender")));
                    if (filterGender == 1) {
                        sqlQuery += "AND M.gender = 'Hombre' ";
                    } else if (filterGender == 2) {
                        sqlQuery += "AND M.gender = 'Mujer' ";
                    }

                    // Order by filter
                    byte filterOrderBy = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOrderBy")));
                    if (filterOrderBy == 0) {
                        sqlQuery += "ORDER BY M.idMember DESC ";
                    } else if (filterOrderBy == 1) {
                        sqlQuery += "ORDER BY M.registrationDate ";
                    }

                    statement = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    // Limit query ( pagination purposes )
                    sqlQuery += "LIMIT ?,?";
                    statementLimited = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                    // Set params // TODO: ADD DOCUMENTATION
                    ParameterMetaData parameters = statementLimited.getParameterMetaData();
                    if (parameters != null) {
                        int maxRegisters = limit * page;
                        if (parameters.getParameterCount() == 2) {
                            statementLimited.setInt(1, maxRegisters - limit);
                            statementLimited.setInt(2, limit);
                        } else if (parameters.getParameterCount() == 3) {
                            statementLimited.setInt(1, Integer.parseInt(query));
                            statementLimited.setInt(2, maxRegisters - limit);
                            statementLimited.setInt(3, limit);

                            statement.setInt(1, Integer.parseInt(query));
                        } else if (parameters.getParameterCount() == 4) {
                            statementLimited.setString(1, "%" + query + "%");
                            statementLimited.setString(2, "%" + query + "%");
                            statementLimited.setInt(3, maxRegisters - limit);
                            statementLimited.setInt(4, limit);

                            statement.setString(1, "%" + query + "%");
                            statement.setString(2, "%" + query + "%");
                        }
                    }

                    // TODO: OPTIMIZE CODE
                    // TODO: UTILITIES AND QUERY ROWS
                    int totalRows = Utilities.countRows(statement);
                    int totalPages = (int) Math.ceil((double) totalRows / limit);
                    rs = statementLimited.executeQuery();
                    ObservableList<MODEL_MEMBERS> members = FXCollections.observableArrayList();
                    tableView.setItems(members);
                    while (rs.next()) {
                        MODEL_MEMBERS modelMembers = new MODEL_MEMBERS();
                        modelMembers.setIdMember(rs.getInt("idMember"));
                        modelMembers.setName(rs.getString("name"));
                        modelMembers.setLastName(rs.getString("lastName"));
                        modelMembers.setAccess(rs.getBoolean("access"));
                        System.out.println(rs.getString("endDate"));
                        if (rs.getString("endDate") == null) {
                            modelMembers.setEndDate("-");
                        } else {
                            LocalDate endDate = LocalDate.parse(rs.getString("endDate"));
                            modelMembers.setEndDate(DateFormatter.getDayMonthYearComplete(endDate));
                        }
                        Platform.runLater(() -> {
                            members.add(modelMembers);

                            tableView.setRowFactory(row -> new TableRow<MODEL_MEMBERS>() {
                                @Override
                                public void updateItem(MODEL_MEMBERS modelMembers, boolean empty) {
                                    super.updateItem(modelMembers, empty);
                                    if (modelMembers != null) {
                                        String style = Input.styleToColor(DATA_MEMBERS.ReadStyle(modelMembers.getIdMember()));
                                        if (getStyleClass().size() == 5) {
                                            getStyleClass().set(4, style); // replace color style
                                        } else {
                                            getStyleClass().addAll("member-cell", style);
                                        }
                                    } else {
                                        if (getStyleClass().size() == 5) {
                                            getStyleClass().remove(4); // remove member-cell
                                            getStyleClass().remove(3); // remove color style
                                        }
                                    }
                                }
                            });
                        });
                    }

                    return new QueryRows(members, totalRows, totalPages);
                } catch (SQLException sqlException) {
                    Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
                }
                return null;
            }
        };
    }

    public static boolean UpdateName(int idMember, String newName) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS SET name = ? WHERE idMember = ?");
            ps.setString(1, newName);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static boolean UpdateLastName(int idMember, String newLastName) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS SET lastName = ? WHERE idMember = ?");
            ps.setString(1, newLastName);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static boolean UpdateGender(int idMember, String newGender) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS SET gender = ? WHERE idMember = ?");
            ps.setString(1, newGender);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static boolean UpdatePhone(int idMember, String newPhone) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS SET phone = ? WHERE idMember = ?");
            ps.setString(1, newPhone);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static boolean UpdateEmail(int idMember, String newEmail) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS SET email = ? WHERE idMember = ?");
            ps.setString(1, newEmail);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static boolean UpdateNotes(int idMember, String newNotes) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS SET notes = ? WHERE idMember = ?");
            ps.setString(1, newNotes);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static boolean UpdateAccess(int idMember, boolean access) {
        Connection con = null;
        PreparedStatement ps;
        try {
            ps = con.prepareStatement("UPDATE MEMBERS SET access = ? WHERE idMember = ?");
            ps.setBoolean(1, !access);
            ps.setInt(2, idMember);
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }
}
