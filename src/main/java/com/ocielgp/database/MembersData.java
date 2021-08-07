package com.ocielgp.database;

import com.digitalpersona.uareu.Fmd;
import com.ocielgp.app.AppController;
import com.ocielgp.database.models.MembersModel;
import com.ocielgp.database.models.MembershipsModel;
import com.ocielgp.database.models.PaymentDebtsModel;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.utilities.DateFormatter;
import com.ocielgp.utilities.Notifications;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDate;
import java.util.ListIterator;
import java.util.Objects;

public class MembersData {
    public static int createMember(MembersModel memberModel) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("INSERT INTO MEMBERS(name, lastName, gender, phone, email, notes, registrationDate, idGym) VALUE (?, ?, ?, ?, ?, ?, CURDATE(), ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, memberModel.getName());
            ps.setString(2, memberModel.getLastName());
            ps.setString(3, memberModel.getGender());
            if (memberModel.getPhone().equals("")) ps.setNull(4, Types.NULL);
            else ps.setString(4, memberModel.getPhone());
            if (memberModel.getEmail().equals("")) ps.setNull(5, Types.NULL);
            else ps.setString(5, memberModel.getEmail());
            if (memberModel.getNotes().equals("")) ps.setNull(6, Types.NULL);
            else ps.setString(6, memberModel.getNotes());
            ps.setInt(7, memberModel.getIdGym());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getInt(1); // Return new id member

        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return 0;
    }

    public static boolean uploadPhoto(int idMember, byte[] photoBytes) {
        if (photoBytes == null) {
            return true; // Skip photo
        }
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            // Check if member have an older photo
            ps = con.prepareStatement("SELECT idPhoto FROM MEMBER_PHOTOS WHERE idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                int idPhoto = rs.getInt("idPhoto");
                ps = con.prepareStatement("UPDATE MEMBER_PHOTOS SET photo = ? WHERE idPhoto = ?");
                ps.setBytes(1, photoBytes);
                ps.setInt(2, idPhoto);
            } else {
                ps = con.prepareStatement("INSERT INTO MEMBER_PHOTOS(photo, idMember) VALUE (?, ?)");
                ps.setBytes(1, photoBytes);
                ps.setInt(2, idMember);
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return false;
    }

    public static boolean uploadFingerprints(int idMember, ListIterator<Fmd> fingerprints) {
        if (fingerprints == null) {
            return true; // Skip fingerprints
        }
        Connection con;
        PreparedStatement ps;
        try {
            con = DataServer.getConnection();
            // Remove all previous fingerprints if exists
            ps = con.prepareStatement("DELETE FROM MEMBER_FINGERPRINTS WHERE idMember = ?");
            ps.setInt(1, idMember);
            ps.executeUpdate();

            while (fingerprints.hasNext()) {
                Fmd fingerprint = fingerprints.next();
                ps = con.prepareStatement("INSERT INTO MEMBER_FINGERPRINTS(fingerprint, idMember) VALUE (?, ?)");
                ps.setBytes(1, fingerprint.getData());
                ps.setInt(2, idMember);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return false;
    }

    public static boolean uploadMembership(int idMember, MembershipsModel membership, String notes) {
        Connection con;
        PreparedStatement ps;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("INSERT INTO PAYMENT_MEMBERSHIPS(price, description, days, startDate, endDate, notes, idMember, idGym, idStaffUser, backup) VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setDouble(1, membership.getPrice()); // price
            ps.setString(2, membership.getDescription()); // description
            ps.setLong(3, membership.getDays()); // days
            ps.setString(4, String.valueOf(LocalDate.now())); // startDate
            ps.setString(5, String.valueOf(LocalDate.now().plusDays(membership.getDays()))); // endDate
            if (notes.isEmpty()) ps.setNull(6, Types.NULL);
            else ps.setString(6, notes); // notes
            ps.setInt(7, idMember); // idMember
            ps.setInt(8, AppController.getCurrentGym().getIdGym()); // idGym
            ps.setInt(9, AppController.getStaffUserModel().getIdStaffUser()); // idStaffUser

            // TODO: FIX THIS BACKUP USER
            ps.setBoolean(10, false); // backup

            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return false;
    }

    public static boolean createDebt(int idMember, PaymentDebtsModel debt) {
        Connection con;
        PreparedStatement ps;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("INSERT INTO PAYMENT_DEBTS(dateTime, paidOut, owe, notes, idMember, idStaffUser) VALUE (NOW(), ?, ?, ?, ?, ?)");
            ps.setDouble(1, debt.getPaidOut());
            ps.setDouble(2, debt.getOwe());
            ps.setString(3, debt.getNotes());
            ps.setInt(4, idMember);
            ps.setInt(5, AppController.getStaffUserModel().getIdStaffUser());
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return false;
    }

    public static QueryRows getMembers(int limit, int page, String fieldSearchContent) {
        // CREATE A QUERY JUST TO GET DEBT COUNT
        Connection con;
        PreparedStatement statementLimited, statement;
        ResultSet rs;
        try {
            // Query initial
            String query = "SELECT M.idMember, M.name, M.lastName, PM.endDate, ( SELECT COUNT(idPaymentDebt) > 0 FROM PAYMENT_DEBTS WHERE flag = 1 AND idMember = M.idMember AND debtStatus = 1 ORDER BY dateTime DESC ) AS debtCount FROM MEMBERS M JOIN PAYMENT_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE ( M.flag = 1 AND PM.flag = 1 ) AND M.idMember NOT IN ( SELECT idMember FROM STAFF_EMPLOYEES WHERE flag = 1 ) ";

            // fieldSearchContent
            if (fieldSearchContent.length() > 0) {
                try {
                    Integer.parseInt(fieldSearchContent);
                    query += "AND M.idMember LIKE ? ";
                } catch (NumberFormatException exception) {
                    query += "AND ( M.name LIKE ? OR M.lastName LIKE ? ) ";
                }
            }

            // Filters
            boolean filterAllGyms = Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberAllGyms"));
            boolean filterOnlyActiveMembers = Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOnlyActiveMembers"));
            // TODO DEBT LIST
            boolean filterOnlyDebtors = Boolean.parseBoolean(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOnlyDebtors"));
            if (!filterAllGyms) {
                query += "AND PM.idGym = " + ConfigFiles.readProperty(ConfigFiles.File.APP, "idGym") + " ";
            }
            if (filterOnlyActiveMembers) {
                query += "AND PM.endDate >= CURDATE() ";
            }

            // Gender filter
            byte filterGender = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberGender")));
            if (filterGender == 1) {
                query += "AND M.gender = 'H' ";
            } else if (filterGender == 2) {
                query += "AND M.gender = 'M' ";
            }

            // Order by filter
            byte filterOrderBy = Byte.parseByte(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "memberOrderBy")));
            if (filterOrderBy == 0) {
                query += "ORDER BY M.idMember DESC ";
            } else if (filterOrderBy == 1) {
                query += "ORDER BY M.registrationDate ";
            }

            con = DataServer.getConnection();
            statement = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // Limit query ( pagination purposes )
            query += "LIMIT ?,?";
            statementLimited = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Set params
            ParameterMetaData parameters = statementLimited.getParameterMetaData();
            if (parameters != null) {
                int maxRegisters = limit * page;
                if (parameters.getParameterCount() == 2) {
                    statementLimited.setInt(1, maxRegisters - limit);
                    statementLimited.setInt(2, limit);
                } else if (parameters.getParameterCount() == 3) {
                    statementLimited.setInt(1, Integer.parseInt(fieldSearchContent));
                    statementLimited.setInt(2, maxRegisters - limit);
                    statementLimited.setInt(3, limit);

                    statement.setInt(1, Integer.parseInt(fieldSearchContent));
                } else if (parameters.getParameterCount() == 4) {
                    statementLimited.setString(1, "%" + fieldSearchContent + "%");
                    statementLimited.setString(2, "%" + fieldSearchContent + "%");
                    statementLimited.setInt(3, maxRegisters - limit);
                    statementLimited.setInt(4, limit);

                    statement.setString(1, "%" + fieldSearchContent + "%");
                    statement.setString(2, "%" + fieldSearchContent + "%");
                }
            }

            // TODO: OPTIMIZE CODE
            // TODO: UTILITIES AND QUERY ROWS
            int totalRows = Utilities.countRows(statement);
            int totalPages = (int) Math.ceil((double) totalRows / limit);
            rs = statementLimited.executeQuery();
            ObservableList<MembersModel> members = FXCollections.observableArrayList();
            while (rs.next()) {
                MembersModel member = new MembersModel();
                member.setIdMember(rs.getInt("idMember"));
                member.setName(rs.getString("name"));
                member.setLastName(rs.getString("lastName"));

                LocalDate endDate = LocalDate.parse(rs.getString("endDate"));
                member.setEndDate(DateFormatter.getDayMonthYearComplete(endDate));

                members.add(member);
            }

            return new QueryRows(members, totalRows, totalPages);
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return null;
    }

    public static String getStyle(int idMember) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;

        con = DataServer.getConnection();
        try {
            ps = con.prepareStatement("SELECT PM.endDate - CURDATE() AS 'daysLeft', (SELECT COUNT(PD.idPaymentDebt) FROM PAYMENT_DEBTS PD WHERE PD.flag = 1 AND PD.idMember = ? AND PD.debtStatus = 1 ORDER BY PD.dateTime DESC) AS 'debtCount' FROM MEMBERS M JOIN PAYMENT_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE (M.flag = 1 AND PM.flag = 1) AND M.idMember = ? ORDER BY M.idMember DESC");
            ps.setInt(1, idMember);
            ps.setInt(2, idMember);
            rs = ps.executeQuery();
            rs.next();
            long daysLeft = rs.getInt("daysLeft");
            int debtCount = rs.getInt("debtCount");

            /* DATES
             *  - 0 DAYS = DANGER
             * 1-3 DAYS = WARN
             * + 3 DAYS = SUCCESS
             */
            if (debtCount == 1) {
                return "creative-style";
            } else {
                if (daysLeft >= 0 && daysLeft <= 3) {
                    return "warn-style";
                } else if (daysLeft > 3) {
                    return "success-style";
                } else {
                    return "danger-style";
                }
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return "default-style";
    }

    public static MembersModel getMember(int idMember) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;

        con = DataServer.getConnection();
        MembersModel membersModel = new MembersModel();
        try {
            ps = con.prepareStatement("SELECT M.name, M.lastName, M.gender, M.phone, M.email, M.notes, M.registrationDate, (SELECT MP.photo FROM MEMBER_PHOTOS MP WHERE MP.idMember = M.idMember LIMIT 1) AS 'photo' FROM MEMBERS M WHERE (M.flag = 1) AND M.idMember = ? ORDER BY M.registrationDate DESC");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                membersModel = new MembersModel();
                membersModel.setName(rs.getString("name"));
                membersModel.setLastName(rs.getString("lastName"));
                membersModel.setGender(rs.getString("gender"));
                membersModel.setPhone(rs.getString("phone"));
                membersModel.setEmail(rs.getString("email"));
                membersModel.setNotes(rs.getString("notes"));
                membersModel.setRegistrationDate(rs.getString("registrationDate"));
            }
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return membersModel;
    }

    public static void checkIn(int idMember) {
        Connection con;
        PreparedStatement ps;

        con = DataServer.getConnection();
        try {
            ps = con.prepareStatement("INSERT INTO CHECK_IN(dateTime, idMember, idGym, openedBy) VALUE (CURRENT_TIMESTAMP(), ?, ?, 1)");
            ps.setInt(1, idMember);
            ps.setInt(2, AppController.getCurrentGym().getIdGym());
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
    }

    public static void checkIn(int idMember, int openedBy) {
        Connection con;
        PreparedStatement ps;

        con = DataServer.getConnection();
        try {
            ps = con.prepareStatement("INSERT INTO CHECK_IN(dateTime, idMember, idGym, openedBy) VALUE (CURRENT_TIMESTAMP(), ?, ?, ?)");
            ps.setInt(1, idMember);
            ps.setInt(2, AppController.getCurrentGym().getIdGym());
            ps.setInt(3, openedBy);
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
    }
}
