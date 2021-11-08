package com.ocielgp.dao;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.utilities.Loading;
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

public class JDBC_Member_Fingerprint {
    public static boolean SCANNING = false;

    public static boolean CreateFingerprints(int idMember, ListIterator<Fmd> fingerprints) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            // remove all previous fingerprints if exists
            assert con != null;
            ps = con.prepareStatement("DELETE FROM MEMBERS_FINGERPRINTS WHERE idMember = ?");
            ps.setInt(1, idMember);
            ps.executeUpdate();

            while (fingerprints.hasNext()) {
                ps = con.prepareStatement("INSERT INTO MEMBERS_FINGERPRINTS(fingerprint, idMember) VALUE (?, ?)");
                ps.setBytes(1, fingerprints.next().getData());
                ps.setInt(2, idMember);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            return false;
        } finally {
            DataServer.closeConnection(con);
        }
    }

    synchronized public static void ReadFindFingerprint(Fmd fingerprint) {
        if (!SCANNING) {
            CompletableFuture.runAsync(() -> {
                Connection con = DataServer.getConnection();
                try {
                    PreparedStatement ps;
                    ResultSet rs;
                    assert con != null;
                    ps = con.prepareStatement("SELECT MF.fingerprint, MF.idMember, A.idMember AS 'idAdmin' FROM MEMBERS_FINGERPRINTS MF JOIN PAYMENTS_MEMBERSHIPS PM on MF.idMember = PM.idMember LEFT JOIN ADMINS A on MF.idMember = A.idMember WHERE DATE_ADD(PM.endDateTime, INTERVAL ? DAY) >= CURDATE() ORDER BY PM.startDateTime");
                    ps.setInt(1, UserPreferences.getPreferenceInt("MAX_DAYS_FINGERPRINTS"));
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        if (Fingerprint_Controller.CompareFingerprints(fingerprint, rs.getBytes("fingerprint"))) {
                            JDBC_Check_In.CreateCheckIn(rs.getBoolean("idAdmin"), rs.getInt("idMember"), 1);
                            return;
                        }
                    }
                    Notifications.Danger("gmi-fingerprint", "Lector de Huellas", "Huella no encontrada", 1.5);
                    Loading.close();
                } catch (SQLException sqlException) {
                    Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
                } finally {
                    DataServer.closeConnection(con);
                }
            });
        }
    }

    public static CompletableFuture<Pair<Integer, ArrayList<Fmd>>> ReadFingerprints(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Fmd> fingerprints = new ArrayList<>();
            int fingerprintCounter = 0;
            Connection con = DataServer.getConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT fingerprint FROM MEMBERS_FINGERPRINTS WHERE idMember = ?");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();

                while (rs.next()) {
                    try {
                        fingerprints.add(UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004));
                        fingerprintCounter++;
                    } catch (UareUException uareUException) {
                        Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException.getMessage(), uareUException);
                    }
                }
            } catch (SQLException sqlException) {
                Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
            } finally {
                DataServer.closeConnection(con);
            }
            return new Pair<>(fingerprintCounter, fingerprints);
        });
    }


}
