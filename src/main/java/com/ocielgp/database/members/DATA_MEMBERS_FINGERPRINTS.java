package com.ocielgp.database.members;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.database.DataServer;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.Notifications;
import javafx.util.Pair;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;

public class DATA_MEMBERS_FINGERPRINTS {
    private static final int fingerprintsDaysLimit;

    static {
        fingerprintsDaysLimit = Integer.parseInt(ConfigFiles.readProperty(ConfigFiles.File.APP, "fingerprintsDaysLimit"));
    }

    public static void InsertFingerprints(int idMember, ListIterator<Fmd> fingerprints) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
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
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
        });
    }

    public static void SelectSearchFingerprints(Fmd fingerprint) {
        CompletableFuture.runAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            ResultSet rs;
            try {
                /*ps = con.prepareStatement("SELECT MF.fingerprint AS 'fingerprint', MP.photo AS 'photo', M.idMember AS 'idMember', M.name AS 'name', M.lastName AS 'lastName', G.name AS 'gymName', PM.description AS 'description', PM.endDate AS 'endDate' FROM MEMBERS_FINGERPRINTS MF JOIN MEMBERS M on MF.idMember = M.idMember LEFT JOIN MEMBERS_PHOTOS MP on M.idMember = MP.idMember JOIN GYMS G on M.idGym = G.idGym JOIN PAYMENTS_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE DATE_ADD(PM.endDate, INTERVAL ? DAY) >= CURDATE() AND ( M.flag = 1 AND PM.flag = 1 ) ORDER BY PM.startDate DESC");
                ps.setInt(1, fingerprintsDaysLimit);
                rs = ps.executeQuery();*/
                ps = con.prepareStatement("SELECT MF.fingerprint, MF.idMember FROM MEMBERS_FINGERPRINTS MF JOIN PAYMENTS_MEMBERSHIPS PM on MF.idMember = PM.idMember WHERE DATE_ADD(PM.endDate, INTERVAL ? DAY) >= CURDATE() ORDER BY PM.startDate");
                ps.setInt(1, fingerprintsDaysLimit);
                rs = ps.executeQuery();

                while (rs.next()) {
                    Fingerprint.compareFingerprint(fingerprint, rs.getBytes("fingerprint")).thenAccept(fingerprintMatch -> {
                        if (fingerprintMatch) {
                            DATA_MEMBERS.ReadMember()
                        }
                    });
//                    Fingerprint.compareFingerprint(fingerprint, rs.getBytes("fingerprint")).thenAccept(fingerprintMatch -> {
//                        if (fingerprintMatch) {
//                            try {
//                                Styles style = DATA_MEMBERS.ReadStyle(rs.getInt("idMember"));
//
//                                GlobalController.showUserInfo(
//                                        style,
//                                        rs.getBytes("photo"),
//                                        rs.getString("idMember"),
//                                        rs.getString("name") + " " + rs.getString("lastName"),
//                                        rs.getString("gymName"),
//                                        rs.getString("description") + " (" + DateFormatter.getDateWithDayName(LocalDate.parse(rs.getString("endDate"))) + ")"
//                                );
//                                if (style.equals("creative-style")) {
//                                    // TODO: NECESITA PAGAR SU DEUDA
//                                } else if (style.equals("danger-style")) {
//                                    // NO DEJARLO PASAR
//                                } else {
//                                    Notifications.buildNotification("gmi-verified-user", "Usuario encontrado", "Ingreso registrado", 2);
//                                    DATA_CHECK_IN.CreateCheckIn(rs.getInt("idMember"), 1);
//                                    // TODO: GUARDAR REGISTRO EN LA BASE DE DATOS DEL INGRESO SI AUN NO VENCE
//                                }
//                            } catch (SQLException sqlException) {
//                                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
//                            }
//                        } else {
//                            Notifications.warn("Lector de Huellas", "Huella no encontrada", 2);
//                        }
//                    });
                }
            } catch (SQLException sqlException) {
                Notifications.catchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            }
        });
    }

    public static CompletableFuture<Pair<Integer, ArrayList<Fmd>>> ReadFingerprints(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.getConnection();
            PreparedStatement ps;
            ResultSet rs;
            ArrayList<Fmd> fingerprints = new ArrayList<>();
            int fingerprintCounter = 0;
            try {
                ps = con.prepareStatement("SELECT fingerprint FROM MEMBERS_FINGERPRINTS WHERE idMember = ?");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();

                while (rs.next()) {
                    try {
                        fingerprints.add(UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004));
                        fingerprintCounter++;
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
            return new Pair<>(fingerprintCounter, fingerprints);
        });
    }


}
