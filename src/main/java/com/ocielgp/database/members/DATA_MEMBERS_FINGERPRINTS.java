package com.ocielgp.database.members;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.app.GlobalController;
import com.ocielgp.database.system.DATA_CHECK_IN;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.DateFormatter;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.Styles;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;

public class DATA_MEMBERS_FINGERPRINTS {
    private static final int fingerprintsDaysLimit;

    static {
        fingerprintsDaysLimit = Integer.parseInt(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "fingerprintsDaysLimit")));
    }

    public static boolean InsertFingerprints(int idMember, ListIterator<Fmd> fingerprints) {
        if (fingerprints == null) {
            return true; // Skip fingerprints
        }
        Connection con = null;
        PreparedStatement ps;
        try {
            // Remove all previous fingerprints if exists
            ps = con.prepareStatement("DELETE FROM MEMBERS_FINGERPRINTS WHERE idMember = ?");
            ps.setInt(1, idMember);
            ps.executeUpdate();

            while (fingerprints.hasNext()) {
                Fmd fingerprint = fingerprints.next();
                ps = con.prepareStatement("INSERT INTO MEMBERS_FINGERPRINTS(fingerprint, idMember) VALUE (?, ?)");
                ps.setBytes(1, fingerprint.getData());
                ps.setInt(2, idMember);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException sqlException) {
            Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        }
        return false;
    }

    public static int SelectSearchFingerprints(Fmd fingerprint) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = con.prepareStatement("SELECT MF.fingerprint AS 'fingerprint', MP.photo AS 'photo', M.idMember AS 'idMember', M.name AS 'name', M.lastName AS 'lastName', G.name AS 'gymName', PM.description AS 'description', PM.endDate AS 'endDate' FROM MEMBER_FINGERPRINTS MF JOIN MEMBERS M on MF.idMember = M.idMember LEFT JOIN MEMBER_PHOTOS MP on M.idMember = MP.idMember JOIN GYMS G on M.idGym = G.idGym JOIN PAYMENT_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE DATE_ADD(PM.endDate, INTERVAL ? DAY) >= CURDATE() AND ( M.flag = 1 AND PM.flag = 1 ) ORDER BY PM.startDate DESC");
            ps.setInt(1, fingerprintsDaysLimit);
            rs = ps.executeQuery();

            boolean userFound = false;
            while (rs.next()) {
                try {
                    Fmd bdFingerprint = UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
                    boolean fingerprintMatch = Fingerprint.compareFingerprint(fingerprint, bdFingerprint);
                    if (fingerprintMatch) {
                        userFound = true;
                        Styles style = DATA_MEMBERS.ReadStyle(rs.getInt("idMember"));

                        GlobalController.showUserInfo(
                                style,
                                rs.getBytes("photo"),
                                rs.getString("idMember"),
                                rs.getString("name") + " " + rs.getString("lastName"),
                                rs.getString("gymName"),
                                rs.getString("description") + " (" + DateFormatter.getDateWithDayName(LocalDate.parse(rs.getString("endDate"))) + ")"
                        );

                        if (style.equals("creative-style")) {
                            // TODO: NECESITA PAGAR SU DEUDA
                        } else if (style.equals("danger-style")) {
                            // NO DEJARLO PASAR
                        } else {
                            Notifications.buildNotification("gmi-verified-user", "Usuario encontrado", "Ingreso registrado.", 2);
                            DATA_CHECK_IN.CreateCheckIn(rs.getInt("idMember"), 1);
                            // TODO: GUARDAR REGISTRO EN LA BASE DE DATOS DEL INGRESO SI AUN NO VENCE
                        }
                        break;
                    }
                } catch (UareUException uareUException) {
                    Notifications.catchError(
                            MethodHandles.lookup().lookupClass().getSimpleName(),
                            Thread.currentThread().getStackTrace()[1],
                            uareUException.getMessage(),
                            uareUException
                    );
                    uareUException.printStackTrace();
                }
            }
            if (!userFound) {
                Notifications.warn("Lector de Huellas", "Huella no encontrada", 2);
            }
            System.out.println("termino");
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

    public static ArrayList<Fmd> SelectFingerprints(int idMember) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;
        ArrayList<Fmd> fingerprints = new ArrayList<>();
        try {
            ps = con.prepareStatement("SELECT fingerprint FROM MEMBERS_FINGERPRINTS WHERE idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            while (rs.next()) {
                try {
                    fingerprints.add(UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004));
                } catch (UareUException uareUException) {
                    Notifications.catchError(
                            MethodHandles.lookup().lookupClass().getSimpleName(),
                            Thread.currentThread().getStackTrace()[1],
                            uareUException.getMessage(),
                            uareUException
                    );
                    uareUException.printStackTrace();
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
        return fingerprints;
    }

    public static int countFingerprints(int idMember) {
        Connection con = null;
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = con.prepareStatement("SELECT COUNT(idFingerprint) AS 'fingerprints' FROM MEMBER_FINGERPRINTS WHERE idMember = ?");
            ps.setInt(1, idMember);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("idFingerprint");
            }
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


}
